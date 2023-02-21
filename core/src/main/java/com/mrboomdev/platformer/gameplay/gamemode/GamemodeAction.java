package com.mrboomdev.platformer.gameplay.gamemode;

import com.mrboomdev.platformer.gameplay.gamemode.GamemodeConstants.*;

public class GamemodeAction {
	public ActionType action;
	public Options options;
	public Conditions conditions;
	public float delay;
		
	public void run() {
		
	}
		
	public class Options {
		public Target target;
		public float duration, speed;
		public String type, text;
		public int[] clockStart;
		public Direction direction;
		public Target toSpecial;
		public Target[] winners;
	}
	
	public class Conditions {
		public int[] timerTime;
		public Target target;
	}
}