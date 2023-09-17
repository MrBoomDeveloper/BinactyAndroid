package com.mrboomdev.binacty;

import com.squareup.moshi.Moshi;

public class Constants {
	public static Moshi moshi;

	static {
		moshi = new Moshi.Builder().build();
	}
}