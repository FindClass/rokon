package com.stickycoding.Rokon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A holder for the a Texture buffer, easier way of handling the ByteBuffer
 * 
 * @author Richard
 */
public class TextureBuffer {
	
	public ByteBuffer buffer;
	public Texture texture;
	
	private int clipLeft = 0, clipRight = 0, clipTop = 0, clipBottom = 0;
	
	public TextureBuffer(Texture texture) {
		this.texture = texture;
		buffer = ByteBuffer.allocate(8*4);
		buffer.order(ByteOrder.nativeOrder());
		update();
	}
	
	public void clip(int left, int top, int right, int bottom) {
		clipLeft = left;
		clipTop = top;
		clipRight = right;
		clipBottom = bottom;
		update();
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void update() {
		float x1 = texture.atlasX + clipLeft;
		float y1 = texture.atlasY + clipTop;
		float x2 = texture.atlasX + texture.getWidth() - clipRight;
		float y2 = texture.atlasY + texture.getHeight() - clipBottom;
		
		float fx1 = x1 / (float)TextureAtlas.getWidth();
		float fx2 = x2 / (float)TextureAtlas.getWidth();
		float fy1 = y1 / (float)TextureAtlas.getHeight(texture.atlasIndex);
		float fy2 = y2 / (float)TextureAtlas.getHeight(texture.atlasIndex);

		buffer.position(0);
		
		buffer.putFloat(fx1);
		buffer.putFloat(fy1);

		buffer.putFloat(fx2);
		buffer.putFloat(fy1);

		buffer.putFloat(fx1);
		buffer.putFloat(fy2);

		buffer.putFloat(fx2);
		buffer.putFloat(fy2);
		
		buffer.position(0);
	}

}
