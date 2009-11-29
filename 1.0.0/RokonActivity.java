package rokon;

import javax.microedition.khronos.opengles.GL10;

import rokon.Handlers.InputHandler;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;

/**
 * @version 1.0.0
 * @author Richard Taylor - Sticky Coding
 * 
 * This class does not have to be used, but provides a very simple way of handling events.
 */
public abstract class RokonActivity extends Activity {
	
	public final static int TOUCH_THRESHOLD = 100;
	
	public Rokon rokon;
	public static RokonActivity singleton;
	
	private boolean touchDown = false;
	private long lastTouchTime = 0;
	private int touchX, touchY;
	private Hotspot lastHotspot;
	
	private boolean _hasLoadingScreen;
    private PowerManager pm;
    private PowerManager.WakeLock wl;

	public abstract void onCreate();
	public abstract void onLoad();
	public abstract void onLoadComplete();
	public abstract void onGameLoop();
	public void onTouchDown(int x, int y, boolean hotspot) { }
	public void onTouchUp(int x, int y, boolean hotspot) { }
	public void onTouch(int x,int y, boolean hotspot) { }
	public void onHotspotTouch(Hotspot hotspot) { }
	public void onHotspotTouchUp(Hotspot hotspot) { }
	public void onHotspotTouchDown(Hotspot hotspot) { }
	public void onDrawBackground(GL10 gl) { }
	public void onDraw(GL10 gl, int layer) { }
	public void onAfterDraw(GL10 gl) { }
	public void onIncomingCall() { }

    public void onDestroy() {
    	super.onDestroy();
    	rokon.end();
    }
    
    public void onResume() {
    	super.onResume();
    	MyPhoneStateListener phoneListener = new MyPhoneStateListener(); 
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE); 
    	pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Rokon");
		wl.acquire();
    	rokon.onResume();
    }
    
	@Override
	public void onPause() {
		super.onPause();
    	wl.release();
    	rokon.onPause();
	}
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		singleton = this;
		onCreate();
	}
	
	public void createEngine(int width, int height, boolean landscape) {
		createEngine(null, width, height, landscape);
	}
	
	public void createEngine(String loadingScreen, int width, int height, final boolean landscape) {
		if(loadingScreen == null)
			rokon = Rokon.createEngine(this, width, height);
		else
			rokon = Rokon.createEngine(this, loadingScreen, width, height);
		_hasLoadingScreen = (loadingScreen != null);
		rokon.setFullscreen();
		rokon.init();
		if(_hasLoadingScreen) {
			new Thread(new Runnable() {
	    		public void run() {
	    			if(landscape) 
	    				rokon.fixLandscape();
	    			else
	    				rokon.fixPortrait();
	    	    	rokon.setBackgroundColor(0.5f, 0.2f, 0.2f);
	    	    	rokon.setInputHandler(touchHandler);
	    	    	rokon.setRenderHook(renderHook);
	    	    	onLoad();
	                System.gc();
	    			rokon.setLoading(false);
	    			onLoadComplete();
	    		}
	    	}).start();
		} else {
			if(landscape) 
				rokon.fixLandscape();
			else
				rokon.fixPortrait();
	    	rokon.setBackgroundColor(0.5f, 0.2f, 0.2f);
	    	rokon.setInputHandler(touchHandler);
	    	rokon.setRenderHook(renderHook);
	    	onLoad();
            System.gc();
			onLoadComplete();
		}
	}
	
	private RenderHook renderHook = new RenderHook() {
		public void onGameLoop() {
			if(touchDown && Rokon.realTime >= lastTouchTime + TOUCH_THRESHOLD) {
				if(lastHotspot != null) {
					touchDown = false;
					onHotspotTouchUp(lastHotspot);
					onTouchUp(touchX, touchY, true);
					lastHotspot = null;
				} else {
					touchDown = false;
					onTouchUp(touchX, touchY, false);
				}
			}
			singleton.onGameLoop();
		}
		
		public void onDrawBackground(GL10 gl) {
			singleton.onDrawBackground(gl);
		}
		
		public void onDraw(GL10 gl, int layer) {
			singleton.onDraw(gl, layer);
		}
		
		public void onAfterDraw(GL10 gl) {
			singleton.onAfterDraw(gl);
		}
	};
    
    private InputHandler touchHandler = new InputHandler() {
    	public void onTouchEvent(MotionEvent event, boolean hotspot) {
    		lastTouchTime = Rokon.realTime;
    		touchX = (int)event.getX();
    		touchY = (int)event.getY();
    		if(!hotspot) {
	    		if(!touchDown) {
	    			touchDown = true;
	        		onTouchDown((int)event.getX(), (int)event.getY(), hotspot);
	    		}
    		}
    		onTouch((int)event.getX(), (int)event.getY(), hotspot);
    	}
    	
    	public void onHotspotTouched(Hotspot hotspot) {
    		lastTouchTime = Rokon.realTime;
    		if(!touchDown) {
    			lastHotspot = hotspot;
    			if(!touchDown) {
    				touchDown = true;
	        		onHotspotTouchDown(hotspot);
    			}
    			onHotspotTouch(hotspot);
    		}
    	}
    };

    public class MyPhoneStateListener extends PhoneStateListener { 
        @Override 
        public void onCallStateChanged(int state,String incomingNumber){ 
        	if(state == TelephonyManager.CALL_STATE_RINGING) {
        		Debug.print("PHONE IS RINGING");
        		onIncomingCall();
        	}
        } 
    } 
}
