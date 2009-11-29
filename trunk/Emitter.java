package rokon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import rokon.OpenGL.GLRenderer;

/**
 * This is part of the planned particle engine.
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
	
	private FloatBuffer _texBuffer;

	public Emitter(float x, float y, float rate, Texture texture) {
		_x = x;
		_y = y;
		_rate = (1 / rate) * 1000;
		_texture = texture;
		_lastUpdate = Rokon.getTime();
		_updateTextureBuffer();
	}
	
	public void markForDelete(boolean mark) {
		_dead = mark;
	}
	
	public boolean isDead() {
		return _dead;
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
	
	private float x1, x2, y1, y2, fx1, fx2, fy1, fy2;
	private void _updateTextureBuffer() {
		x1 = _texture.atlasX;
		y1 = _texture.atlasY;
		x2 = _texture.atlasX + _texture.getWidth();
		y2 = _texture.atlasY + _texture.getHeight();
		
		fx1 = x1 / (float)TextureAtlas.getWidth();
		fx2 = x2 / (float)TextureAtlas.getWidth();
		fy1 = y1 / (float)TextureAtlas.getHeight(_texture.atlasIndex);
		fy2 = y2 / (float)TextureAtlas.getHeight(_texture.atlasIndex);
		
		_texBuffer = _makeFloatBuffer(new float[] {
			fx1, fy1,
			fx2, fy1,
			fx1, fy2,
			fx2, fy2			
		});
	}
	
	public void spawn() {

	}
	
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
	public void updateSpawns() {
		now = Rokon.getTime();
		timeDiff = now - _lastUpdate;
		count = Math.round(timeDiff / _rate);
		if(count > 0) {
			for(i = 0; i < count; i++)
				spawn();
			_lastUpdate = now;
		}
	}
	
	int texToBe;
	public void drawFrame(GL10 gl) {
		updateSpawns();
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texBuffer);
		gl.glVertexPointer(2, GL11.GL_FLOAT, 0, GLRenderer.vertexBuffer);

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
	public void setXY(float x, float y) {
		_x = x;
		_y = y;
	}
	
	public float spawnX() {
		return _x;
	}
	
	public float spawnY() { 
		return _y;
	}
	
	public int particleCount() {
		k = 0;
		for(i = 0; i < MAX_PARTICLES; i++)
			if(particleArr[i] != null)
				k++;
		return k;
	}
	
	public boolean noParticles() {
		for(i = 0; i < MAX_PARTICLES; i++)
			if(particleArr[i] != null)
				return false;
		return true;
	}
}
