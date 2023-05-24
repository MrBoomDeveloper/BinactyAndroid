package com.mrboomdev.platformer.script;

@SuppressWarnings("unused")
public interface ScriptRunner {
	void create();
	void loaded();
	void start();
	void end();

	default void createPlayers(PlayerCreationHandler handler) {

	}

	interface PlayerCreationHandler {}
}