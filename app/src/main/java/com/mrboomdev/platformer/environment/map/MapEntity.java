package com.mrboomdev.platformer.environment.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.character.CharacterEntity;

public class MapEntity extends MapObject {
    private CharacterEntity entity;
	private int layer;
	
	public MapEntity(CharacterEntity entity) {
		this.entity = entity;
		layer = 1;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		entity.draw(batch);
	}

    @Override
    public void setPosition(Vector2 position) {
		entity.body.setTransform(position, 0);
	}

    @Override
    public Vector2 getPosition(boolean isBottom) {
		if(!isBottom) return entity.body.getPosition();
		return entity.getPosition().add(0, entity.worldBody.bottom[3]);
    }
	
	@Override
	public int getLayer() {
		return layer;
	}

    @Override
    public void remove() {
		entity.gainDamage(99999);
	}
}