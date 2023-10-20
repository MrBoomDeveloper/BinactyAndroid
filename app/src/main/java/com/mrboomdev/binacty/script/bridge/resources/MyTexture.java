package com.mrboomdev.binacty.script.bridge.resources;

import com.badlogic.gdx.graphics.Texture;
import com.mrboomdev.binacty.api.resources.BinactyTexture;

public class MyTexture extends BinactyTexture {
	private final Texture nativeTexture;

	public MyTexture(Texture nativeTexture) {
		this.nativeTexture = nativeTexture;
	}
}