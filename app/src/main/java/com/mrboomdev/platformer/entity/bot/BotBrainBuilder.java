package com.mrboomdev.platformer.entity.bot;

import com.mrboomdev.platformer.entity.bot.BotBrain;

public class BotBrainBuilder {
	private String[] tiles;
	private String family;
	
	public BotBrainBuilder setTiles(String... tiles) {
		this.tiles = tiles;
		return this;
	}
	
	public BotBrainBuilder setFamily(String team) {
		this.family = team;
		return this;
	}
	
	public BotBrain build() {
		return null;
	}
}