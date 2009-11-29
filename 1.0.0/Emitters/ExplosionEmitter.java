package rokon.Emitters;

import rokon.Emitter;
import rokon.Particle;
import rokon.Texture;
import rokon.TextureBuffer;
import rokon.Debug;
 
public class ExplosionEmitter extends Emitter {
	
	private boolean _didExplode;
	private int _particleCount;

	private float _minVelX;
	private float _maxVelX;
	private float _minVelY;
	private float _maxVelY;
	private float _minScale;
	private float _maxScale;
	private float _minAlphaRate;
	private float _maxAlphaRate;
	private float _minShrinkRate;
	private float _maxShrinkRate;
	private float _minAccX;
	private float _maxAccX;
	private float _minAccY;
	private float _maxAccY;
	
	public ExplosionEmitter(float x, float y, Texture texture, int particleCount, float minVelX, float maxVelX, float minVelY, float maxVelY, float minAccX, float maxAccX, float minAccY, float maxAccY, float minScale, float maxScale, float minShrinkRate, float maxShrinkRate, float minAlphaRate, float maxAlphaRate) {
		super(x, y, 1, texture);
		_didExplode = false;
		_particleCount = particleCount;
		_minVelX = minVelX;
		_maxVelX = maxVelX;
		_minVelY = minVelY;
		_maxVelY = maxVelY;
		_minScale = minScale;
		_maxScale = maxScale;
		_minAlphaRate = minAlphaRate;
		_maxAlphaRate = maxAlphaRate;
		_minShrinkRate = minShrinkRate;
		_maxShrinkRate = maxShrinkRate;
		_minAccX = minAccX;
		_maxAccX = maxAccX;
		_minAccY = minAccY;
		_maxAccY = maxAccY;
	}
	
	private float avgScale, scale, x, y, shrinkRate, alphaRate, velY, velX, accX, accY;
	public void updateSpawns() {
		if(!_didExplode) {
			_didExplode = true;
			avgScale = _minScale + ((_maxScale - _minScale) / 2);
			for(int i = 0; i < _particleCount; i++) {
				scale = _minScale + (float)(Math.random() * (_maxScale - _minScale));
				x = spawnX() - (avgScale / 2);
				y = spawnY() - (avgScale / 2);
				shrinkRate = _minShrinkRate + (float)(Math.random() * (_maxShrinkRate - _minShrinkRate));
				alphaRate = _minAlphaRate + (float)(Math.random() * (_maxAlphaRate - _minAlphaRate));
				velY = _minVelX + (float)(Math.random() * (_maxVelX - _minVelX));
				velX = _minVelY + (float)(Math.random() * (_maxVelY - _minVelY));
				accX = _minAccX + (float)(Math.random() * (_maxAccX - _minAccX));
				accY = _minAccY + (float)(Math.random() * (_maxAccY - _minAccY));
				spawnParticle(new Particle(this, 0, x, y, scale, shrinkRate, alphaRate, velX, velY, accX, accY));
			}
		}
		if(noParticles()) {
			markForDelete(true);
		}
	}

}
