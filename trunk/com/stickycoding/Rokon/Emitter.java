package com.stickycoding.Rokon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.stickycoding.Rokon.OpenGL.RokonRenderer;


/**
 * The emitter is the spawning point for particles, much work is still needed to be done here
 * @author Richard
 */
public class Emitter {
	public static final int MAX_PARTICLES = 100;
	
	private Particle[] particleArr = new Particle[MAX_PARTICLES];
	
	private boolean _dead = false;
	private float _x;
	private float _y;
	private float _rate;
	private long _lastUpdate;
	private Texture _texture;
	
	private int i, j, k;
	
	private TextureBuffer _texBuffer;

	/**
	 * @param x
	 * @param y
	 * @param rate number of particles created per second
	 * @param texture texture of each particle
	 */
	public Emitter(float x, float y, float rate, Texture texture) {
		_x = x;
		_y = y;
		_rate = (1 / rate) * 1000;
		_texture = texture;
		_lastUpdate = Rokon.getTime();
		_texBuffer = new TextureBuffer(texture);
	}
	
	/**
	 * Lets the engine know to remove this emitter on the next loop
	 * @param mark
	 */
	public void markForDelete(boolean mark) {
		_dead = mark;
	}
	
	/**
	 * @return true if this Emitter is ready to be removed from the engine
	 */
	public boolean isDead() {
		return _dead;
	}
	
	private void _spawn() {

	}
	
	/**
	 * @param particle Particle object to be created
	 */
	public void spawnParticle(Particle particle) {
		j = -1;
		for(i = 0; i < MAX_PARTICLES; i++)
			if(particleArr[i] == null)
				j = i;
		if(j == -1) {
			Debug.print("TOO MANY PARTICLES");
			return;
		}
		particleArr[j] = particle;
	}
	
	private long now, timeDiff;
	private int count;
	private void _updateSpawns() {
		now = Rokon.getTime();
		timeDiff = now - _lastUpdate;
		count = Math.round(timeDiff / _rate);
		if(count > 0) {
			for(i = 0; i < count; i++)
				_spawn();
			_lastUpdate = now;
		}
	}
	
	int texToBe;
	public void drawFrame(GL10 gl) {
		_updateSpawns();
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texBuffer.buffer);
		gl.glVertexPointer(2, GL11.GL_FLOAT, 0, RokonRenderer.vertexBuffer);

		texToBe = TextureAtlas.texId[_texture.atlasIndex];
		if(Rokon.getRokon().currentTexture != texToBe) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texToBe);
			Rokon.getRokon().currentTexture = texToBe;
		}
		
		for(i = 0; i < MAX_PARTICLES; i++) {
			if(particleArr[i] != null) {
				particleArr[i].update();
				if(particleArr[i].dead)
					particleArr[i] = null;
				else {
					if(particleArr[i].x + particleArr[i].scale < 0 || particleArr[i].x > Rokon.getRokon().getWidth() || particleArr[i].y + particleArr[i].scale < 0 || particleArr[i].y > Rokon.getRokon().getHeight()) {
						if(Rokon.getRokon().isForceOffscreenRender()) {
							gl.glLoadIdentity();
							gl.glTranslatef(particleArr[i].x, particleArr[i].y, 0);
							gl.glScalef(particleArr[i].scale, particleArr[i].scale, 0);
							gl.glColor4f(particleArr[i].red, particleArr[i].green, particleArr[i].blue, particleArr[i].alpha);
							gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
						}
					} else {
						gl.glLoadIdentity();
						gl.glTranslatef(particleArr[i].x, particleArr[i].y, 0);
						gl.glScalef(particleArr[i].scale, particleArr[i].scale, 0);
						gl.glColor4f(particleArr[i].red, particleArr[i].green, particleArr[i].blue, particleArr[i].alpha);
						gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
					}
				}
			}
		}
		
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
	/**
	 * Sets the coordinates of the Emitter
	 * @param x
	 * @param y
	 */
	public void setXY(float x, float y) {
		_x = x;
		_y = y;
	}
	
	/**
	 * @return the X coordinate of the Emitter
	 */
	public float spawnX() {
		return _x;
	}
	
	/**
	 * @return the Y coordinate of the Emitter
	 */
	public float spawnY() { 
		return _y;
	}
	
	/**
	 * @return the current count of Particle's in the Emitter
	 */
	public int particleCount() {
		k = 0;
		for(i = 0; i < MAX_PARTICLES; i++)
			if(particleArr[i] != null)
				k++;
		return k;
	}
	
	/**
	 * @return true if particle count is zero
	 */
	public boolean noParticles() {
		for(i = 0; i < MAX_PARTICLES; i++)
			if(particleArr[i] != null)
				return false;
		return true;
	}
}
