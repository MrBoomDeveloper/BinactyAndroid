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
			Class.forName("com.mrboomdev.binacty.LegacyFnafBridge").newInstance();
		} catch(Exception e) {
			throw new BinactyException("Failed to start a game! Please check if you have the latest version.", e);
		}

		getResources().getMusic("music/music_box.wav").play();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}