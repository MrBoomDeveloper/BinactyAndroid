package com.mrboomdev.platformer.script.entry;

import com.mrboomdev.platformer.game.pack.PackData;

public class BeanshellEntry extends ScriptEntry {

	public BeanshellEntry(PackData.GamemodeEntry entry) {
		super(entry);
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public boolean isCompiled() {
		return false;
	}

	@Override
	public void compile() {

	}

	@Override
	public void load() {

	}

	@Override
	public void start() {

	}

	@Override
	public void create() {

	}

	@Override
	public void destroy() {

	}
}