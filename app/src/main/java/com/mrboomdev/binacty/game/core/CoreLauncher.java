package com.mrboomdev.binacty.game.core;

import android.content.SharedPreferences;

public interface CoreLauncher {

	default void exit(ExitStatus status) {}

	default void pause() {}

	SharedPreferences getSave();

	enum ExitStatus {
		CRASH,
		GAME_OVER,
		LOBBY
	}
}