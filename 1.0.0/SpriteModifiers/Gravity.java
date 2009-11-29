package rokon.SpriteModifiers;

import rokon.Rokon;
import rokon.Sprite;
import rokon.SpriteModifier;

/**
 * The gravity modifier allows you to make a Sprite behave under the effects of gravity
 */
public class Gravity extends SpriteModifier {

	private float _gravityX;
	private float _gravityY;
	private long _lastUpdate;
	
	public Gravity(float gravityY) {
		this(0, gravityY);
	}

	public Gravity(float gravityX, float gravityY) {
		_gravityX = gravityX;
		_gravityY = gravityY;
		_lastUpdate = Rokon.getTime();
	}
	
	public void onUpdate(Sprite sprite) {
		long timeDiff = Rokon.getTime() - _lastUpdate;
		sprite.setVelocityRelative(_gravityX * (float)(timeDiff / 1000), _gravityY * (float)(timeDiff / 1000));
	}

}
