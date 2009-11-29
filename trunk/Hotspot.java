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
	private int _id;
	
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
		_id = -1;
		sprite = null;
	}
	
	public Hotspot(float x, float y, float width, float height, int id) {
		this(x, y, width, height);
		_id = id;
	}
	
	/**
	 * Defines a hotspot by a sprite, rather than coordinates
	 * @param sprite
	 */
	public Hotspot(Sprite _sprite) {
		sprite = _sprite;
	}
	
	public Hotspot(Sprite _sprite, int id) {
		this(_sprite);
		_id = id;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
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
