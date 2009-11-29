package rokon.SpriteModifiers;

import rokon.Rokon;
import rokon.Sprite;
import rokon.SpriteModifier;
import rokon.Handlers.BasicHandler;

public class Timer extends SpriteModifier {
	
	private long time;
	private long timeout;
	private BasicHandler handler;
	
	public Timer(int delay, BasicHandler handler) {
		this.handler = handler;
		this.time = delay;
		timeout = 0;
	}
	
	public void onUpdate(Sprite sprite) {
		if(timeout == 0) {
			timeout = Rokon.time + time;
			return;
		}
		if(Rokon.time > timeout) {
			setExpired(true);
			handler.onFinished();
		}
	}

}
