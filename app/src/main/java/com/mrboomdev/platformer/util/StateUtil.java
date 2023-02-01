package com.mrboomdev.platformer.util;

import android.app.Activity;
import java.util.HashMap;

public class StateUtil {
	private static HashMap<String, Activity> activities = new HashMap<>();
	private static HashMap<String, Object> keys = new HashMap<>();
	
	public static void addActivity(String name, Activity activity) {
		activities.put(name, activity);
	}
	
	public static Activity getActivity(String name) {
		return activities.get(name);
	}
	
	public static void addKey(String name, Object key) {
		keys.put(name, key);
	}
	
	public static Object getKey(String key) {
		if(keys.containsKey(key)) return keys.get(key);
		return null;
	}
}