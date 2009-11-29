package rokon;

import android.util.Log;


public class Debug {
	
	public static final boolean DEBUG_MODE = true;
	
	public static void print(String msg) {
		if(DEBUG_MODE)
			Log.v("Rokon", msg);
	}
	
	private static long startTime = 0;
	private static long lastInterval = 0;
	public static void startTimer() {
		if(DEBUG_MODE) {
			startTime = System.currentTimeMillis();
			lastInterval = startTime;
		}
	}
	
	public static void debugTimer(String message) {
		if(DEBUG_MODE) {
			long diff = System.currentTimeMillis() - startTime;
			Debug.print(message + " took " + diff + "ms");
		}
	}
	
	public static void debugTimer() {
		if(DEBUG_MODE) {
			long diff = System.currentTimeMillis() - startTime;
			Debug.print("Took " + diff + "ms");
		}
	}
	
	public static void debugInterval(String message) {
		if(DEBUG_MODE) {
			long diff = System.currentTimeMillis() - lastInterval;
			Debug.print(message + " interval took " + diff + "ms");
			lastInterval = System.currentTimeMillis();
		}
	}
	
	public static void debugInterval() {
		if(DEBUG_MODE) {
			long diff = System.currentTimeMillis() - lastInterval;
			Debug.print("Interval took " + diff + "ms");
			lastInterval = System.currentTimeMillis();
		}
	}

}
