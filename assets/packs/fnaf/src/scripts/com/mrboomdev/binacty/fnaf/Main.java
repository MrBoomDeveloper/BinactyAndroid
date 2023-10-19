package com.mrboomdev.binacty.fnaf;

import com.mrboomdev.binacty.api.client.BinactyClient;
import com.mrboomdev.binacty.api.pack.PackContext;
import com.mrboomdev.binacty.api.util.BinactyException;
import com.mrboomdev.binacty.fnaf.cutscenes.IntroCutscene;

public class Main extends BinactyClient {

	public Main(PackContext context) {
		super(context);
	}

	@Override
	public void create() {
		try {
			var clazz = Class.forName("com.mrboomdev.binacty.LegacyFnafBridge");
			var constructor = clazz.getConstructor(BinactyClient.class);
			constructor.newInstance(this);
		} catch(Exception e) {
			throw new BinactyException("Failed to start a game! Please check if you have the latest version.", e);
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}
}