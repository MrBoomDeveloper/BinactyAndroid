package com.mrboomdev.platformer.script.entry;

import com.mrboomdev.platformer.game.pack.PackData;
public interface ScriptEntry {
	boolean isReady();
	boolean isCompiled();
	void compile(PackData.GamemodeEntry entry);
	void start();
	void resume();
	void pause();
	void destroy();
}