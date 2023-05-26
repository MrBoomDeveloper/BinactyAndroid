package com.mrboomdev.platformer.environment.gamemode;

import java.util.ArrayList;
import java.util.List;

public class GamemodeScript {
	public List<GamemodeFunction> start = new ArrayList<>();
	public GamemodeOptions options;
	
	public static class GamemodeOptions {
		public float initialFade = 0;
	}
}