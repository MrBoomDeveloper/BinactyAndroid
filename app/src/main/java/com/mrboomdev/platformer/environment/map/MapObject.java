package com.mrboomdev.platformer.environment.map;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mrboomdev.platformer.game.GameHolder;
import com.squareup.moshi.Json;

public abstract class MapObject implements Comparable<MapObject> {
	@Json(ignore = true)
	private Vector2 cachedPosition;
	@Json(ignore = true)
	private long positionCachedLastTime;
	
	public abstract void draw(SpriteBatch batch);
	public abstract void setPosition(Vector2 position);
	public abstract Vector2 getPosition(boolean isBottom);
	public abstract int getLayer();
	public abstract void remove();
	public abstract Body getBody();

	public Vector2 getPosition() {
		return getPosition(false);
	}

	public Vector2 getCachedPosition() {
		if(getBody() == null) return getPosition();

		long currentTime = System.currentTimeMillis();
		if(cachedPosition == null || currentTime > positionCachedLastTime - (long)(GameHolder.getInstance().settings.objectPositionRecacheDelay * 1000)) {
			cachedPosition = getBody().getPosition();
			positionCachedLastTime = currentTime;
		}

		return cachedPosition;
	}
	
	@Override
    public int compareTo(@NonNull MapObject object) {
		var myPosition = this.getCachedPosition();
		var enemyPosition = object.getCachedPosition();

		if(this.getLayer() != object.getLayer()) {
			return this.getLayer() - object.getLayer();
		} else if(myPosition.y != enemyPosition.y) {
			return (enemyPosition.y > myPosition.y) ? 1 : -1;
		}

		return (enemyPosition.x > myPosition.x) ? 1 : -1;
	}
}