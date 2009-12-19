package com.stickycoding.Rokon.Backgrounds;

import javax.microedition.khronos.opengles.GL10;

import com.stickycoding.Rokon.Background;
import com.stickycoding.Rokon.Rokon;
import com.stickycoding.Rokon.Texture;
import com.stickycoding.Rokon.TextureBuffer;


/**
 * One texture will be repeated as it scrolls horizontally
 * 
 * @author Richard
 */
public class ScrollingBackground extends Background {
	
	private TextureBuffer _buffer;
	private float _x;
	private float _y;

	private float _scrollX;
	private float _scrollY;

	private float _targetWidth;
	private float _targetHeight;
	private float _width;
	private float _height;
	
	public ScrollingBackground(Texture texture) {
		this(texture, 0);
	}
	
	public ScrollingBackground(Texture texture, float y) {
		_buffer = new TextureBuffer(texture);
		_x = 0;
		_y = y;
		_scrollX = 0;
		_scrollY = 0;
		_targetWidth = Rokon.getRokon().getWidth();
		_targetHeight = Rokon.getRokon().getHeight();
		_width = texture.getWidth();
		_height = texture.getHeight();
	}
	
	private float _startX;
	private float _startY;
	private int rows;
	private int cols;
	private float x;
	private float y;
	public void drawFrame(GL10 gl) {
		rows = 0;
		cols = 0;
		
		x = _scrollX;
		while(x > 0)
			x -= _width;
		_startX = x;
		
		y = _scrollY;
		while(y > 0)
			y -= _height;
		_startY = y;
		
		while(x < _targetWidth) {
			x += _width;
			rows++;
		}
		
		while(y < _targetHeight) {
			y += _height;
			cols++;
		}
		
		gl.glColor4f(1, 1, 1, 1);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _buffer.buffer);
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				x = _startX + (_width * i);
				y = _startY + (_height * j);
				gl.glLoadIdentity();
				gl.glTranslatef(x, y, 0);
				gl.glScalef(_width, _height, 0);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			}
		}
	}
	
	public void setScroll(float x, float y) {
		_scrollX = x;
		_scrollY = y;
	}
	
	public float getScrollX() {
		return _scrollX;
	}
	
	public float getScrollY() {
		return _scrollY;
	}
	
}
