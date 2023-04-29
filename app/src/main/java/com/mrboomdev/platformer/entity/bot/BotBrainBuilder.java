package com.mrboomdev.platformer.entity.bot;

import com.mrboomdev.platformer.entity.bot.BotBrain;

public class BotBrainBuilder {
	private BotBrain brain;
	
	public BotBrainBuilder() {
		brain = new BotBrain();
	}
	
	public BotBrainBuilder setResponder(BotBrain.Responder responder) {
		brain.responder = responder;
		return this;
	}
	
	public BotBrainBuilder setRefreshRate(int refreshRate) {
		brain.refreshRate = refreshRate;
		return this;
	}
	
	public BotBrain build() {
		return brain;
	}
}