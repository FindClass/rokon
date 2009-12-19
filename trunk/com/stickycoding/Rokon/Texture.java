package com.stickycoding.Rokon;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Texture's are very important, and can be applied to Sprite's.
 * A Texture class contains a reference to a particular image loaded by createTextureXXX functions in Rokon
 * The actual image is held on the hardware, accessed through TextureAtlas
 * 
 * @author Richard
 */
public class Texture {
	
	public boolean inserted = false;
	public int atlasX;
	public int atlasY;
	public int atlasIndex;
	
	private int _width;
	private int _height;
	private int[] _pixels;
	private int _tileCols;
	private int _tileRows;
	
	public boolean isAsset;
	public String assetPath;
	
	public String fileName = null;
	public int suggestAtlas, suggestX, suggestY;
	
	private Rect srcRect = null, atlasRect = null;
	
	private void updateRect() {
		if(atlasRect == null)
			atlasRect = new Rect(atlasX, atlasY, atlasX + _width, atlasY + _height);
		if(srcRect == null)
			srcRect = new Rect(0, 0, _width, _height);
	}
	
	/**
	 * Creates a texture from a Bitmap, with a path reference for reloading
	 * @param path file location in /assets/
	 * @param bmp the Bitmap of the texture to be loaded
	 */
	public Texture(String path, Bitmap bmp) {
		_width = bmp.getWidth();
		_height = bmp.getHeight();
		_pixels = new int[_width * _height];
		bmp.getPixels(_pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		_tileCols = 1;
		_tileRows = 1;
		inserted = false;
		atlasIndex = -1;
		isAsset = true;
		assetPath = path;
		
	}

	/**
	 * DEPRECATED
	 * All Texture's must now be assets
	 * @param bmp
	 */
	public Texture(Bitmap bmp) {
		/*_width = bmp.getWidth();
		_height = bmp.getHeight();
		_tileCols = 1;
		_tileRows = 1;
		inserted = false;
		atlasIndex = -1;
		isAsset = false;*/
	}
	
	/**
	 * Replaces the current asset with another
	 * Note that dimensions must be the same
	 * @param path location of the file in /assets/
	 */
	public void replace(String path) {
		try {
			Bitmap bmp = BitmapFactory.decodeStream(Rokon.getRokon().getActivity().getAssets().open(path));
			replace(bmp);
		} catch (IOException e) {
			Debug.print("CANNOT FIND " + path);
			e.printStackTrace();
		}
	}
	
	private void replace(Bitmap bitmap) {
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
	
	/**
	 * @return the width of the texture, in pixels
	 */
	public int getWidth() {
		return _width;
	}
	
	/**
	 * @return the height of the texture, in pixels
	 */
	public int getHeight() {
		return _height;
	}
	
	/**
	 * Splits the Texture into a number of tiles, based on their size
	 * @param tileWidth
	 * @param tileHeight
	 */
	public void setTileSize(int tileWidth, int tileHeight) {
		_tileCols = _width / tileWidth;
		_tileRows = _height / tileHeight;
	}
	
	/**
	 * Splits the Texture into a number of tiles, defined through columns and rows
	 * @param columns number of columns (across)
	 * @param rows number of rows (downwards)
	 */
	public void setTileCount(int columns, int rows) {
		_tileCols = columns;
		_tileRows = rows;
	}
	
	/**
	 * @return the number of tile columns (across)
	 */
	public int getTileCols() {
		return _tileCols;
	}
	
	/**
	 * @return the number of tile rows (downwards)
	 */
	public int getTileRows() {
		return _tileRows;
	}
}
