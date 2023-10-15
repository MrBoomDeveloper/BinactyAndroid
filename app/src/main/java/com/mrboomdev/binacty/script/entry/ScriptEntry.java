package com.mrboomdev.binacty.script.entry;

import com.mrboomdev.platformer.game.pack.PackData;

public abstract class ScriptEntry {
	private PackData.GamemodeEntry entry;

	public ScriptEntry(PackData.GamemodeEntry entry) {
		this.entry = entry;
	}

	public void setEntry(PackData.GamemodeEntry entry) {
		this.entry = entry;
	}

	public PackData.GamemodeEntry getEntry() {
		return this.entry;
	}

	public float getProgress() {
		return 0;
	}

	public boolean isSoftReady() {
		return true;
	}

	public abstract boolean isReady();

	public abstract boolean isCompiled();

	public abstract void compile();

	public abstract void load();

	public abstract void create();

	public abstract void start();

	public abstract void destroy();
}