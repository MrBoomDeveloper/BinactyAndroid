package com.mrboomdev.platformer.gameplay.gamemode;

import java.util.HashMap;
import java.util.ArrayList;
import com.mrboomdev.platformer.gameplay.gamemode.GamemodeConstants.*;

public class Gamemode {
	public ArrayList<GamemodeAction> start = new ArrayList<>();
	public HashMap<ActionType, ArrayList<GamemodeAction>> listeners = new HashMap<>();
	public ArrayList<Team> teams;
	
	public class TEAM {
		public String name;
		public Target target;
		public int count;
		public boolean repeatCharacters = true;
		public String[] forceCharacters;
	}
}