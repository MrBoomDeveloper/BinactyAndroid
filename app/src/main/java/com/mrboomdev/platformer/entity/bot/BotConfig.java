package com.mrboomdev.platformer.entity.bot;

import java.util.Map;

public class BotConfig {
	public Map<String, State> states;
	public State current;
	public String initial;
	public Listener listener;
	
	public BotConfig build() {
		current = states.get(initial);
		return this;
	}
	
	public void update() {
		
	}
	
	public static class State {
		public float visionDistance;
		public float speed, duration;
		public String next;
	}
	
	public static class Listener {
		
	}
}