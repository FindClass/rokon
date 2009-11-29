package rokon;


/**
 * Represents a Particle inside an Emitter system
 */

public class Particle {
	
	public boolean dead = false;
	
	public float x;
	public float y;
	public float scale;
	public float alpha;
	public float alpharate;
	public float accelerationX;
	public float accelerationY;
	public float velocityX;
	public float velocityY;
	public float shrinkrate;
	public float red;
	public float green;
	public float blue;
	public long timeout;	
	
	private long _lastUpdate;
	
	public Particle(Emitter emitter, long life, float x, float y, float scale, float shrinkrate, float alpharate, float velocityX, float velocityY, float accelerationX, float accelerationY) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.alpharate = alpharate;
		this.alpha = 1;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.accelerationX = accelerationX;
		this.accelerationY = accelerationY;
		this.shrinkrate = shrinkrate;
		if(life > 0)
			this.timeout = Rokon.getTime() + life;
		else
			this.timeout = 0;
		red = 1;
		green = 1;
		blue = 1;
		_lastUpdate = Rokon.getTime();
	}
	
	long now, timeDiff;
	float modifier;
	public void update() {
		now = Rokon.getTime();
		timeDiff = now - _lastUpdate;
		modifier = (float)timeDiff / 1000f;
		alpha -= alpharate * modifier;
		scale -= shrinkrate * modifier;
		if(alpha <= 0 || scale <= 0 || (now > timeout && timeout > 0)) {
			alpha = 0;
			scale = 0;
			dead = true;
			return;
		}
		velocityX += accelerationX * modifier;
		velocityY += accelerationY * modifier;
		x += (velocityX * modifier) + ((shrinkrate * modifier) / 2);
		y += (velocityY * modifier) + ((shrinkrate * modifier) / 2);
		_lastUpdate = now;
	}

}
