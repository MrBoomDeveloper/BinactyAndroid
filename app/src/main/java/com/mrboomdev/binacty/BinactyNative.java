package com.mrboomdev.binacty;

import com.mrboomdev.binacty.online.BinactyOnline;
import com.mrboomdev.platformer.util.helper.BoomException;
public class BinactyNative {
	private static boolean isInitialized;

	public static void init() {
		if(isInitialized) return;

		System.loadLibrary("EOSSDK");
		System.loadLibrary("platformer");

		if(!BinactyOnline.getInstance().init()) {
			throw new BoomException("Failed to initialize Online Services!");
		}

		isInitialized = true;
	}
}