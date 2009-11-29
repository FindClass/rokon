package rokon;

/**
 * A sprite which attaches itsself to a parent, commonly used in game situations.
 */
public class TrackingSprite extends Sprite {
	
	private Sprite _parent;
	private float _offsetX;
	private float _offsetY;
	private float _scale;
	
	public boolean stopTracking = false;
	
	public TrackingSprite(Sprite parent, float offsetX, float offsetY, float scale, Texture texture) {
		super(parent.getX(), parent.getY(), texture);
		_parent = parent;
		_offsetX = offsetX;
		_offsetY = offsetY;
		_scale = scale;
	}
	
	public void stopTracking() {
		stopTracking = true;
	}
	
	@Override
	public void updateMovement() {
		_parent.updateMovement();
		if(stopTracking)
			return;
		setTileIndex(_parent.getTileIndex());
		setWidth(_parent.getWidth() * _scale);
		setHeight(_parent.getHeight() * _scale);
		setScale(_parent.getScaleX(), _parent.getScaleY());
		setXY(_parent.getX() + _offsetX, _parent.getY() + _offsetY);
		setRotation(_parent.getRotation());
		setAlpha(_parent.getAlpha() / 3);
	}
	
	public void setOffset(float x, float y) {
		_offsetX = x;
		_offsetY = y;
	}
	
	public float getOffsetX() {
		return _offsetX;
	}
	
	public float getOffsetY() {
		return _offsetY;
	}
	
}
