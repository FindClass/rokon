/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This file was lifted from the APIDemos sample.  See:
// http://developer.android.com/guide/samples/ApiDemos/src/com/example/android/apis/graphics/index.html
package rokon.OpenGL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import rokon.Debug;
import rokon.Hotspot;
import rokon.Rokon;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author A mix of my own code and snippets from various places
 * 
 * An implementation of SurfaceView that uses the dedicated surface for
 * displaying an OpenGL animation.  This allows the animation to run in a
 * separate thread, without requiring that it be driven by the update mechanism
 * of the view hierarchy.
 *
 * The application-specific rendering code is delegated to a GLView.Renderer
 * instance.
 */
public class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public GLSurfaceView(Context context) {
        super(context);
        init();
    }
    
    private Hotspot[] hotspotArr;
    private int i;
    
    public boolean onTouchEvent(MotionEvent event) {
    	if(Rokon.getRokon().getInputHandler() != null) {
    		boolean hit = false;
    		hotspotArr = Rokon.getRokon().getHotspots();
    		
    		for(i = 0; i < Rokon.MAX_HOTSPOTS; i++) {
    			if(hotspotArr[i] != null)
	    			if(hotspotArr[i].sprite != null) {
	    				event.setLocation(Rokon.screenWidth / Rokon.fixedWidth * event.getX(), Rokon.screenHeight / Rokon.fixedHeight * event.getY());
						if(event.getX() >= hotspotArr[i].sprite.getX() && event.getX() <= hotspotArr[i].sprite.getX() + hotspotArr[i].sprite.getWidth() && event.getY() >= hotspotArr[i].sprite.getY() && event.getY() <= hotspotArr[i].sprite.getY() + hotspotArr[i].sprite.getHeight()) {
		    				Rokon.getRokon().getInputHandler().onHotspotTouched(hotspotArr[i]);
		    				Rokon.getRokon().getInputHandler().onHotspotTouched(hotspotArr[i], event);
							hit = true;
						} 
	    			} else if(event.getX() >= hotspotArr[i].x && event.getX() <= hotspotArr[i].x + hotspotArr[i].width && event.getY() >= hotspotArr[i].y && event.getY() <= hotspotArr[i].y + hotspotArr[i].height) {
						Rokon.getRokon().getInputHandler().onHotspotTouched(hotspotArr[i]);
	    				Rokon.getRokon().getInputHandler().onHotspotTouched(hotspotArr[i], event);
						hit = true;
	    			}
			
    		}
    		Rokon.getRokon().getInputHandler().onTouchEvent(event, hit);
    	}
    	return true;
    }

    public GLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
    	//Debug.print("SurfaceView init()");
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
    }

    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    public void setGLWrapper(GLWrapper glWrapper) {
        mGLWrapper = glWrapper;
    }

    public void setRenderer(Renderer renderer) {
    	//Debug.print("setRenderer, GLThread starting");
        mGLThread = new GLThread(renderer);
        mGLThread.start();
    }

    public void surfaceCreated(SurfaceHolder holder) {
    	//Debug.print("Surface Created");
        mGLThread.surfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	//Debug.print("Surface Destroyed");
        // Surface will be destroyed when we return
        mGLThread.surfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Surface size or format has changed. This should not happen in this
        // example.
        mGLThread.onWindowResize(w, h);
    }

    /**
     * Inform the view that the activity is paused.
     */
    public void onPause() {
    	//Debug.print("OnPause!");
    	Rokon.getRokon().pause();
        mGLThread.onPause();
    }

    /**
     * Inform the view that the activity is resumed.
     */
    public void onResume() {
    	//Debug.print("OnResume!");
        mGLThread.onResume();
    }

    /**
     * Inform the view that the window focus has changed.
     */
    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mGLThread.onWindowFocusChanged(hasFocus);
    }

    /**
     * Set an "event" to be run on the GL rendering thread.
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void setEvent(Runnable r) {
        mGLThread.setEvent(r);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mGLThread.requestExitAndWait();
    }

    // ----------------------------------------------------------------------

    public interface GLWrapper {
      GL wrap(GL gl);
    }

    // ----------------------------------------------------------------------

    /**
     * A generic renderer interface.
     */
    public interface Renderer {
        /**
         * @return the EGL configuration specification desired by the renderer.
         */
        int[] getConfigSpec();

        /**
         * Surface created.
         * Called when the surface is created. Called when the application
         * starts, and whenever the GPU is reinitialized. This will
         * typically happen when the device awakes after going to sleep.
         * Set your textures here.
         */
        void surfaceCreated(GL10 gl);
       
        /**
         * Called when the rendering thread is about to shut down.  This is a
         * good place to release OpenGL ES resources (textures, buffers, etc).
         * @param gl
         */
        void shutdown(GL10 gl);
       
        /**
         * Surface changed size.
         * Called after the surface is created and whenever
         * the OpenGL ES surface size changes. Set your viewport here.
         * @param gl
         * @param width
         * @param height
         */
        void sizeChanged(GL10 gl, int width, int height);
        /**
         * Draw the current frame.
         * @param gl
         */
        void drawFrame(GL10 gl);
    }

    /**
     * An EGL helper class.
     */

    private class EglHelper {
        public EglHelper() {

        }

        /**
         * Initialize EGL for a given configuration spec.
         * @param configSpec
         */
        public void start(int[] configSpec){
            /*
             * Get an EGL instance
             */
            mEgl = (EGL10) EGLContext.getEGL();

            /*
             * Get to the default display.
             */
            mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);


            /*
             * We can now initialize EGL for that display
             */
            int[] version = new int[2];
            mEgl.eglInitialize(mEglDisplay, version);

            EGLConfig[] configs = new EGLConfig[1];
            int[] num_config = new int[1];
            mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1,
                    num_config);
            mEglConfig = configs[0];

            /*
            * Create an OpenGL ES context. This must be done only once, an
            * OpenGL context is a somewhat heavy object.
            */
            mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig,
                    EGL10.EGL_NO_CONTEXT, null);

            mEglSurface = null;
        }

        /*
         * Create and return an OpenGL surface
         */
        public GL createSurface(SurfaceHolder holder) {
            /*
             *  The window size has changed, so we need to create a new
             *  surface.
             */
            if (mEglSurface != null) {

                /*
                 * Unbind and destroy the old EGL surface, if
                 * there is one.
                 */
                mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            }

            /*
             * Create an EGL surface we can render into.
             */
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay,
                    mEglConfig, holder, null);

            /*
             * Before we can issue GL commands, we need to make sure
             * the context is current and bound to a surface.
             */
            mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface,
                    mEglContext);


            GL gl = mEglContext.getGL();
            if (mGLWrapper != null) {
                gl = mGLWrapper.wrap(gl);
            }
            return gl;
        }

        /**
         * Display the current render surface.
         * @return false if the context has been lost.
         */
        public boolean swap() {
            mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);

            /*
             * Always check for EGL_CONTEXT_LOST, which means the context
             * and all associated data were lost (For instance because
             * the device went to sleep). We need to sleep until we
             * get a new surface.
             */
            return mEgl.eglGetError() != EGL11.EGL_CONTEXT_LOST;
        }

        public void finish() {
            if (mEglSurface != null) {
                mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_CONTEXT);
                mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
                mEglSurface = null;
            }
            if (mEglContext != null) {
                mEgl.eglDestroyContext(mEglDisplay, mEglContext);
                mEglContext = null;
            }
            if (mEglDisplay != null) {
                mEgl.eglTerminate(mEglDisplay);
                mEglDisplay = null;
            }
        }

        EGL10 mEgl;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
    }

    /**
     * A generic GL Thread. Takes care of initializing EGL and GL. Delegates
     * to a Renderer instance to do the actual drawing.
     *
     */

    public class GLThread extends Thread {
        GLThread(Renderer renderer) {
            super();
            mDone = false;
            mWidth = 0;
            mHeight = 0;
            mRenderer = renderer;
            setName("GLThread");
        }

        @Override
        public void run() {
            /*
             * When the android framework launches a second instance of
             * an activity, the new instance's onCreate() method may be
             * called before the first instance returns from onDestroy().
             *
             * This semaphore ensures that only one instance at a time
             * accesses EGL.
             */
            try {
                try {
                sEglSemaphore.acquire();
                } catch (InterruptedException e) {
                    return;
                }
                guardedRun();
            } catch (InterruptedException e) {
                // fall thru and exit normally
            } finally {
                sEglSemaphore.release();
            }
        }

        private void guardedRun() throws InterruptedException {
            mEglHelper = new EglHelper();
            /*
             * Specify a configuration for our opengl session
             * and grab the first configuration that matches is
             */
            int[] configSpec = mRenderer.getConfigSpec();
            mEglHelper.start(configSpec);

            GL10 gl = null;
            boolean tellRendererSurfaceCreated = true;
            boolean tellRendererSurfaceChanged = true;
            
            /*
             * This is our main activity thread's loop, we go until
             * asked to quit.
             */
            while (!mDone) {
                /*
                 *  Update the asynchronous state (window size)
                 */
                int w, h;
                boolean changed;
                boolean needStart = false;
                synchronized (this) {
                    if (mEvent != null) {
                        mEvent.run();
                    }
                    if (mPaused) {
                        mEglHelper.finish();
                        needStart = true;
                    }
                    if(needToWait()) {
                        while (needToWait()) {
                            wait();
                        }
                    }
                    if (mDone) {
                        break;
                    }
                    changed = mSizeChanged;
                    w = mWidth;
                    h = mHeight;
                    mSizeChanged = false;
                }
                if (needStart) {
                    mEglHelper.start(configSpec);
                    tellRendererSurfaceCreated = true;
                    changed = true;
                }
                if (changed) {
                    gl = (GL10) mEglHelper.createSurface(mHolder);
                    tellRendererSurfaceChanged = true;
                }
                if (tellRendererSurfaceCreated) {
                    mRenderer.surfaceCreated(gl);
                    tellRendererSurfaceCreated = false;
                }
                if (tellRendererSurfaceChanged) {
                    mRenderer.sizeChanged(gl, w, h);
                    tellRendererSurfaceChanged = false;
                }
                if ((w > 0) && (h > 0)) {
                	if(Rokon.getRokon().isLoading()) {
                		Bitmap tbmp = null;
                		try {
                			tbmp = BitmapFactory.decodeStream(Rokon.getRokon().getActivity().getAssets().open(Rokon.getRokon().getLoadingImage()));
                		} catch (IOException e) {
                			Debug.print("LOADING SCREEN CANNOT FIND");
                			e.printStackTrace();
                		}
                		Bitmap bmp = null;
                		float realWidth, realHeight;
                		if(tbmp == null) {
                			Debug.print("LOADING SCREEN ERROR");
                			System.exit(0); 
                			return;
                		} else {
                			if(tbmp.getWidth() > 512 || tbmp.getHeight() > 512)
                				bmp = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888);
                			else
                				bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                			realWidth = tbmp.getWidth();
                			realHeight = tbmp.getHeight();
                			Canvas canvas = new Canvas(bmp);
                			canvas.drawBitmap(tbmp, 0, 0, new Paint());
                		}
            			
            			int[] tmp_tex = new int[1];
            			gl.glGenTextures(1, tmp_tex, 0);
            			int tex = tmp_tex[0];
            			gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
                        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
                        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
                        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
                        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
                        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
            			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
            			//Debug.print("Texture created tex=" + tex + " w=" + bmp.getWidth() + " h=" + bmp.getHeight());
            			
						gl.glClearColor(0.6f, 0.6f, 1, 1);
						gl.glVertexPointer(2, GL11.GL_FLOAT, 0, GLRenderer.vertexBuffer);

						ByteBuffer texBuffer = ByteBuffer.allocate(8*4);
						texBuffer.order(ByteOrder.nativeOrder());
						
						texBuffer.position(0);
						
						texBuffer.putFloat(0);
						texBuffer.putFloat(0);
						
						float height = bmp.getHeight();
						float width = bmp.getWidth();

						if(Rokon.getRokon().isLandscape()) {
							texBuffer.putFloat(realWidth / width);
							texBuffer.putFloat(0);
							texBuffer.putFloat(0);
							texBuffer.putFloat(realHeight / height);
							texBuffer.putFloat(realWidth / width);
							texBuffer.putFloat(realHeight / height);
						} else {
							texBuffer.putFloat(realHeight / height);
							texBuffer.putFloat(0);
							texBuffer.putFloat(0);
							texBuffer.putFloat(realWidth / width);
							texBuffer.putFloat(realHeight / height);
							texBuffer.putFloat(realWidth / width);
						}

						texBuffer.position(0);
						
						gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
                		while(Rokon.getRokon().isLoading()) {
			
                	    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT);    	
                	    	gl.glMatrixMode(GL10.GL_MODELVIEW);
                	    	
                			gl.glLoadIdentity();
                			gl.glScalef(Rokon.getRokon().getWidth(), Rokon.getRokon().getHeight(), 0);
                			gl.glColor4f(1, 1, 1, 1);
                			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
                			
                            mEglHelper.swap();
                            
                            if(Rokon.getRokon().isLoadingScreen()) {
                            	Rokon.getRokon().setIsLoadingScreen(false);
                            }
                            
                		}
            			System.gc();
                	} else {
                    	Rokon.getRokon().loadTextures(gl);
                        mRenderer.drawFrame(gl);
                        mEglHelper.swap();
                	}
                }
             }


            /*
             * clean-up everything...
             */
            if (gl != null) {
                mRenderer.shutdown(gl);
            }
           
            mEglHelper.finish();
        }

    	private ByteBuffer bb;
    	private FloatBuffer fb;
    	private FloatBuffer _makeFloatBuffer(float[] arr) {
    		bb = ByteBuffer.allocateDirect(arr.length*4);
    		bb.order(ByteOrder.nativeOrder());
    		fb = bb.asFloatBuffer();
    		fb.put(arr);
    		fb.position(0);
    		return fb;
    	}
    	
        private boolean needToWait() {
            return (mPaused || (! mHasFocus) || (! mHasSurface) || mContextLost)
                && (! mDone);
        }

        public void surfaceCreated() {
            synchronized(this) {
                mHasSurface = true;
                mContextLost = false;
                notify();
            }
        }

        public void surfaceDestroyed() {
            synchronized(this) {
                mHasSurface = false;
                notify();
            }
        }

        public void onPause() {
            synchronized (this) {
                mPaused = true;
            }
        }

        public void onResume() {
            synchronized (this) {
                mPaused = false;
                notify();
            }
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            synchronized (this) {
                mHasFocus = hasFocus;
                if (mHasFocus == true) {
                    notify();
                }
            }
        }
        public void onWindowResize(int w, int h) {
            synchronized (this) {
                mWidth = w;
                mHeight = h;
                mSizeChanged = true;
            }
        }

        public void requestExitAndWait() {
            // don't call this from GLThread thread or it is a guaranteed
            // deadlock!
            synchronized(this) {
                mDone = true;
                notify();
            }
            try {
                join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Queue an "event" to be run on the GL rendering thread.
         * @param r the runnable to be run on the GL rendering thread.
         */
        public void setEvent(Runnable r) {
            synchronized(this) {
                mEvent = r;
            }
        }
       
        public void clearEvent() {
            synchronized(this) {
                mEvent = null;
            }
        }

        public boolean mDone;
        private boolean mPaused;
        private boolean mHasFocus;
        private boolean mHasSurface;
        private boolean mContextLost;
        private int mWidth;
        private int mHeight;
        private Renderer mRenderer;
        private Runnable mEvent;
        private EglHelper mEglHelper;
    }

    private static final Semaphore sEglSemaphore = new Semaphore(1);
    private boolean mSizeChanged = true;

    private SurfaceHolder mHolder;
    public GLThread mGLThread;
    private GLWrapper mGLWrapper;
}
