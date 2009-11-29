package rokon;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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
	
	private Rect srcRect = null, atlasRect = null;

	public void setBitmap(Bitmap bmp) {
		_bmp = bmp;
	}
	
	private void updateRect() {
		if(atlasRect == null)
			atlasRect = new Rect(atlasX, atlasY, atlasX + _width, atlasY + _height);
		if(srcRect == null)
			srcRect = new Rect(0, 0, _width, _height);
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
	
	public void replace(String path) {
		try {
			Bitmap bmp = BitmapFactory.decodeStream(Rokon.getRokon().getActivity().getAssets().open(path));
			replace(bmp);
		} catch (IOException e) {
			Debug.print("CANNOT FIND " + path);
			e.printStackTrace();
		}
	}
	
	public void replace(Bitmap bitmap) {
		//Debug.startTimer();
		if(bitmap.getWidth() != _width || bitmap.getHeight() != _height) {
			Debug.print("updateTexture requires matching dimensions");
			return;
		}
		updateRect();
		Bitmap atlasBitmap = TextureAtlas.getBitmap(atlasIndex);
		Canvas canvas = new Canvas(atlasBitmap);
		canvas.drawBitmap(bitmap, srcRect, atlasRect, TextureAtlas.paint);
		TextureAtlas.reloadTexture(atlasIndex, atlasBitmap);
		//Debug.debugInterval("Loaded New Bitmap");
	}
	
	public Bitmap getBitmap() {
		if(_bmp == null) {
			Bitmap bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			updateRect();
			canvas.drawBitmap(TextureAtlas.getBitmap(atlasIndex), atlasRect, srcRect, TextureAtlas.paint);
			return bitmap;
		}
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
