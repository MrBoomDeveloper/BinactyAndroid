package com.mrboomdev.platformer.entity.data;

@Deprecated
public class PlayerData {
	public String path;
	public String icon;
	public boolean unlockable = true;
	public Requirements requires;
	
	class Requirements {
		public String[] achievments;
		public int level, diamonds, coins;
		public String[] missions;
	}
}