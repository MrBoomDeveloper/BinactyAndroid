package com.mrboomdev.platformer.environment.gamemode;

import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction.*;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import java.util.ArrayList;
import java.util.HashMap;

public class GamemodeScript {
	public ArrayList<LoadingFiles.File> load = new ArrayList<>();
	public ArrayList<GamemodeFunction> start = new ArrayList<>();
	public HashMap<Action, ArrayList<GamemodeFunction>> listeners = new HashMap<>();
	public ArrayList<Team> teams;
	public GamemodeOptions options;
	
	public class Team {
		public String name = "Team";
		public Target target;
		public int count = 100;
		public boolean strictOrder = false;
		public boolean repeatCharacters = false;
		public String[] forceCharacters;
	}
	
	public class GamemodeOptions {
		public float initialFade = 0;
	}
}