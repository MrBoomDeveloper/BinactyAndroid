package com.mrboomdev.binacty.fnaf;

import com.mrboomdev.binacty.api.client.BinactyClient;
import com.mrboomdev.binacty.fnaf.cutscenes.IntroCutscene;

public class Main extends BinactyClient {

	@Override
	public boolean isReady() {
		System.out.println("LOL");
		return false;
	}
}