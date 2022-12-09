package com.mrboomdev.platformer.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Block {
	public Texture texture;
	public Vector2 position;
	
	public Block(String name, Vector2 position) {
		this.texture = new Texture(Gdx.files.internal("img/terrain/" + name + ".jpg"));
		this.position = position;
	}
}