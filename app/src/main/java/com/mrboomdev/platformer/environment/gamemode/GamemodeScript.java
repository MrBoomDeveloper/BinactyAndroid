package com.mrboomdev.platformer.environment.gamemode;

import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction.*;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamemodeScript {
	public List<LoadingFiles.File> load = new ArrayList<>();
	public List<GamemodeFunction> start = new ArrayList<>();
	public Map<Action, List<GamemodeFunction>> listeners = new HashMap<>();
	public List<Team> teams;
	public GamemodeOptions options;
	
	public static class Team {
		public String name = "Team";
		public Target target;
		public int count = 100;
		public boolean strictOrder = false;
		public boolean repeatCharacters = false;
		public String[] forceCharacters;
	}
	
	public static class GamemodeOptions {
		public float initialFade = 0;
	}
}