package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.BotTarget;

public class PathPoint implements BotTarget {
	public Vector2 position;
	public int index = 0;
	
	public PathPoint(Vector2 position) {
		this.position = position;
	}
	
	@Override
	public Vector2 getPosition() {
		return position;
	}
}