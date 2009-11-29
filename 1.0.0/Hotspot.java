package rokon;

/**
 * Hotspot class is used as a way of simplying the detection of touches on
 * the screen, and triggers onHotspotTouched
 *
 */
public class Hotspot {
	
	public float x;
	public float y;
	public float width;
	public float height;
	
	public Sprite sprite;
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Hotspot(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		sprite = null;
	}
	
	/**
	 * Defines a hotspot by a sprite, rather than coordinates
	 * @param sprite
	 */
	public Hotspot(Sprite _sprite) {
		sprite = _sprite;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void update(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		sprite = null;
	}
	
	/**
	 * @param sprite
	 */
	public void update(Sprite _sprite) {
		sprite = _sprite;
	}
}
