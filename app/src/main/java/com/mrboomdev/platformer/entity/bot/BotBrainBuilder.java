package com.mrboomdev.platformer.entity.bot;

import com.mrboomdev.platformer.entity.bot.ai.AiState;

@SuppressWarnings("unused")
public class BotBrainBuilder {
	private final BotBrain brain;
	
	public BotBrainBuilder() {
		brain = new BotBrain();
	}
	
	public BotBrainBuilder setResponder(BotBrain.Responder responder) {
		brain.responder = responder;
		return this;
	}

	public BotBrainBuilder setStates(AiState state) {
		brain.state = state;
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