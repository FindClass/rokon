package com.stickycoding.Rokon.Backgrounds;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.stickycoding.Rokon.Background;
import com.stickycoding.Rokon.Rokon;
import com.stickycoding.Rokon.Texture;
import com.stickycoding.Rokon.TextureAtlas;
import com.stickycoding.Rokon.TextureBuffer;
import com.stickycoding.Rokon.OpenGL.RokonRenderer;


/**
 * A very basic, static textured background image
 * 
 * @author Richard
 */
public class FixedBackground extends Background {
	
	public TextureBuffer _buffer;
	
	public FixedBackground(Texture texture) {
		_buffer = new TextureBuffer(texture);
	}
	
	private int texToBe;
	public void drawFrame(GL10 gl) {
		texToBe = TextureAtlas.texId[_buffer.texture.atlasIndex];
		if(Rokon.getRokon().currentTexture != texToBe) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texToBe);
			Rokon.getRokon().currentTexture = texToBe;
		}
		
		gl.glVertexPointer(2, GL11.GL_FLOAT, 0, RokonRenderer.vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _buffer.buffer);
		gl.glLoadIdentity();
		gl.glScalef(Rokon.getRokon().getWidth(), Rokon.getRokon().getHeight(), 0);
		gl.glColor4f(1, 1, 1, 1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}
}
