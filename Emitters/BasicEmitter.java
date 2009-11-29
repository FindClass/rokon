package rokon.Emitters;

import rokon.Emitter;
import rokon.Particle;
import rokon.Texture;

/**
 * @author Richard Taylor
 * BasicEmitter is a very flexible emitter, that requires all parameters to be set
 */
public class BasicEmitter extends Emitter {

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
	private long _life;
	
	public BasicEmitter(float x, float y, float rate, Texture texture, long life, float minVelX, float maxVelX, float minVelY, float maxVelY, float minAccX, float maxAccX, float minAccY, float maxAccY, float minScale, float maxScale, float minShrinkRate, float maxShrinkRate, float minAlphaRate, float maxAlphaRate) {
		super(x, y, rate, texture);
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
		_life = life;
	}
	
	private float scale, x, y, shrinkRate, alphaRate, velY, velX, accX, accY;
	public void spawn() {
		scale = _minScale + (float)(Math.random() * (_maxScale - _minScale));
		x = spawnX() - (scale / 2);
		y = spawnY() - (scale / 2);
		shrinkRate = _minShrinkRate + (float)(Math.random() * (_maxShrinkRate - _minShrinkRate));
		alphaRate = _minAlphaRate + (float)(Math.random() * (_maxAlphaRate - _minAlphaRate));
		velX = _minVelX + (float)(Math.random() * (_maxVelX - _minVelX));
		velY = _minVelY + (float)(Math.random() * (_maxVelY - _minVelY));
		accX = _minAccX + (float)(Math.random() * (_maxAccX - _minAccX));
		accY = _minAccY + (float)(Math.random() * (_maxAccY - _minAccY));
		spawnParticle(new Particle(this, _life, x, y, scale, shrinkRate, alphaRate, velX, velY, accX, accY));
	}

}
