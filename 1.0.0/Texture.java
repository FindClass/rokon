package rokon;

import android.graphics.Bitmap;

/**
 * Texture's are very important, and can be applied to Sprite's.
 * A Texture class contains a reference to a particular image loaded by createTextureXXX functions in Rokon
 * The actual image is held on the hardware, accessed through TextureAtlas
 */
public class Texture {
	
	public boolean inserted;
	public int atlasX;
	public int atlasY;
	public int atlasIndex;
	
	private Bitmap _bmp;
	private int _width;
	private int _height;
	private int _tileCols;
	private int _tileRows;
	
	public boolean isAsset;
	public String assetPath;
	
	public String fileName = null;
	public int suggestAtlas, suggestX, suggestY;

	public void setBitmap(Bitmap bmp) {
		_bmp = bmp;
	}
	
	public Texture(String path, Bitmap bmp) {
		_width = bmp.getWidth();
		_height = bmp.getHeight();
		_tileCols = 1;
		_tileRows = 1;
		inserted = false;
		atlasIndex = -1;
		isAsset = true;
		assetPath = path;
	}

	public Texture(Bitmap bmp) {
		_bmp = bmp;
		_width = bmp.getWidth();
		_height = bmp.getHeight();
		_tileCols = 1;
		_tileRows = 1;
		inserted = false;
		atlasIndex = -1;
		isAsset = false;
	}
	
	public Bitmap getBitmap() {
		return _bmp;
	}
	
	public void cleanBitmap() {
		if(_bmp != null)
			_bmp.recycle();
		_bmp = null;
	}
	
	public int getWidth() {
		return _width;
	}
	
	public int getHeight() {
		return _height;
	}
	
	public void setTileSize(int tileWidth, int tileHeight) {
		_tileCols = _width / tileWidth;
		_tileRows = _height / tileHeight;
	}
	
	public void setTileCount(int columns, int rows) {
		_tileCols = columns;
		_tileRows = rows;
	}
	
	public int getTileCols() {
		return _tileCols;
	}
	
	public int getTileRows() {
		return _tileRows;
	}
}
