package rokon.Handlers;

import rokon.Hotspot;
import android.view.MotionEvent;

/**
 * InputHandler allows you to be notified of screen touches, and alerted when specific hotspots are touched
 */
public class InputHandler {
	
	public void onTouchEvent(MotionEvent event, boolean hotspot) { }

	public void onHotspotTouched(Hotspot hotspot) { }
	
	public void onHotspotTouched(Hotspot hotspot, MotionEvent event) { }

}
