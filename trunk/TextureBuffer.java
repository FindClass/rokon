package rokon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * A holder for the a Texture buffer
 */
public class TextureBuffer {
	
	public ByteBuffer buffer;
	public Texture texture;
	
	public TextureBuffer(Texture texture) {
		this.texture = texture;
		buffer = ByteBuffer.allocate(8*4);
		buffer.order(ByteOrder.nativeOrder());
		update();
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void update() {
		float x1 = texture.atlasX;
		float y1 = texture.atlasY;
		float x2 = texture.atlasX + texture.getWidth();
		float y2 = texture.atlasY + texture.getHeight();
		
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
