package com.mrboomdev.binacty.script.bridge;

import com.mrboomdev.binacty.api.pack.PackContext;
import com.mrboomdev.binacty.api.resources.BinactyResources;
import com.mrboomdev.platformer.game.pack.PackData;

public class MyPackContext extends PackContext {
	public final PackData.GamemodeEntry entry;
	public final BinactyResources resources;

	public MyPackContext(PackData.GamemodeEntry entry) {
		super(null);
		this.entry = entry;

		this.resources = new MyResources(this);
	}

	@Override
	public String getId() {
		return entry.id;
	}

	@Override
	public PackContext getContext() {
		return this;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public BinactyResources getResources() {
		return resources;
	}

	@Override
	public void start() {}
}