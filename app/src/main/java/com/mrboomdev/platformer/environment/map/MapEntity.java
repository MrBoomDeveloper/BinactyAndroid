package com.mrboomdev.platformer.environment.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.EntityAbstract;

public class MapEntity extends MapObject {
    private EntityAbstract entity;
	private int layer;
	
	public MapEntity(EntityAbstract entity) {
		this.entity = entity;
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
    public Vector2 getPosition() {
        return entity.body.getPosition();
    }
	
	@Override
	public int getLayer() {
		return layer;
	}

    @Override
    public void remove() {
		//TODO
	}
}