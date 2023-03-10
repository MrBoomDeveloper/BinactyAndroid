package com.mrboomdev.platformer.environment.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.EntityAbstract;

public class MapEntity implements MapObject<MapObject> {
    private EntityAbstract entity;
	
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
    public void remove() {
		//TODO
	}

    @Override
    public int compareTo(MapObject object) {
		return Math.round(object.getPosition().x - getPosition().x);
	}
}