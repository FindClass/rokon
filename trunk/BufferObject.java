package rokon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferObject {

	public ByteBuffer buffer;

	public BufferObject(int x1, int y1, int x2, int y2) {
		buffer = ByteBuffer.allocate(8*4);
		buffer.order(ByteOrder.nativeOrder());
		update(x1, y1, x2, y2);
	}
	
	public void update(int x1, int y1, int x2, int y2) {
		buffer.position(0);
		buffer.putFloat(x1);
		buffer.putFloat(y1);
		buffer.putFloat(x2);
		buffer.putFloat(y1);
		buffer.putFloat(x1);
		buffer.putFloat(y2);
		buffer.putFloat(x2);
		buffer.putFloat(y2);
		buffer.position(0);
	}
	
	public void destroy() {
		buffer = null;
	}

}
