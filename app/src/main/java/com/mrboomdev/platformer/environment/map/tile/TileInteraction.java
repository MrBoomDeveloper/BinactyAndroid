package com.mrboomdev.platformer.environment.map.tile;

import java.util.List;

public class TileInteraction {
	public List<Action> actions;
	public List<String> queue;
	
	public static class Action {
		public ActionType action;
		public Target target;
	}
	
	public enum Target {
		ME,
		CONNECTED
	}
	
	public enum ActionType {
		TOGGLE_STYLE,
		CALL_FUNCTION
	}
}