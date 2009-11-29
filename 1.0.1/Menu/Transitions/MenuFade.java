package rokon.Menu.Transitions;

import rokon.Rokon;
import rokon.Sprite;
import rokon.Menu.Menu;
import rokon.Menu.MenuLayers;
import rokon.Menu.MenuTransition;

public class MenuFade extends MenuTransition {
	
	private int _time;
	private long _startTime;
	private Sprite _sprite;
	
	public MenuFade(int time) {
		_sprite = new Sprite(0, 0, Rokon.getRokon().getWidth(), Rokon.getRokon().getHeight());
		_sprite.setColor(0, 0, 0, 1);
		_time = time;
	}
	
	public void begin(Menu menu) {
		super.begin(menu);
		Rokon.getRokon().addSprite(_sprite, MenuLayers.OVERLAY);
		_startTime = Rokon.time;
	}
	
	private float position;
	public void loop() {
		if(running) {
			position = ((_startTime + _time) - Rokon.time) / _time;
			if(position < 0) {
				running = false;
				Rokon.getRokon().removeSprite(_sprite, MenuLayers.OVERLAY);
			} else
				_sprite.setAlpha(position);
		}
	}

}
