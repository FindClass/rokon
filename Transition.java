package rokon;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;

/**
 * Transitions allow the appearance of basic changes between scenes.
 *  The screen is grabbed, drawn to a texture and rendered as an effect while
 *  a new scene is brought in.
 *  
 *  
 *  Current inactive.
 */
public class Transition {
	
	private boolean _waiting;
	
	private Bitmap _bitmap;
	private boolean _active;
	private long _time;
	private long _startTime;
	private int _textureId;
	
	public Transition(long time) {
		_waiting = true;
		_startTime = Rokon.getTime();
		_time = time;
		_active = false;
	}
	
	/**
	 * @return TRUE if the Transition is waiting to grab the screen
	 */
	public boolean isWaiting() {
		return _waiting;
	}
	
	/**
	 * @return TRUE is the Transition is currently drawing itsself
	 */
	public boolean isActive() {
		return _active;
	}
	
	/**
	 * Grabs the pixels from the screen and applies it to a new texture
	 * @param gl
	 */
	private int tex;
	public void grabScreen(GL10 gl) {
		ByteBuffer buffer = ByteBuffer.allocate(Rokon.getRokon().getWidth() * Rokon.getRokon().getHeight() * 4);
		gl.glReadPixels(0, 0, Rokon.getRokon().getWidth(), Rokon.getRokon().getHeight(), GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);

		int[] tmp_tex = new int[1];
		gl.glGenTextures(1, tmp_tex, 0);
		tex = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, 4, Rokon.getRokon().getWidth(), Rokon.getRokon().getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);
		
		Debug.print("NEW TEXTURE BOUND");
		gl.glBindTexture(GL10.GL_TEXTURE_2D, Rokon.getRokon().tex);
	}
	
	/**
	 * Draws the transition, this should be overridden
	 * @param gl
	 */
	public void drawScreen(GL10 gl) {

	}

}
