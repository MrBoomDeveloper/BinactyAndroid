package com.mrboomdev.platformer.environment.map.tile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.squareup.moshi.Json;

public class TileInteraction {
	public float[] range;
	public float timeout;
	public boolean selectable;
	@Json(ignore = true) public InteractionListener listener;
	@Json(ignore = true) public MapTile owner;
	@Json(ignore = true) GameHolder game = GameHolder.getInstance();
	
	public TileInteraction getSerialized() {
		var copy = new TileInteraction(null);
		return copy;
	}
	
	public TileInteraction(TileInteraction interaction) {
		this.clone(interaction);
	}
	
	public void clone(TileInteraction interaction) {
		if(interaction == null) return;
		range = interaction.range;
		timeout = interaction.timeout;
		selectable = interaction.selectable;
	}
	
	public void build(World world, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position);
		var body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Entity.INTRACTABLE;
		fixtureDef.filter.maskBits = Entity.CHARACTER;
		PolygonShape shape = new PolygonShape();
		if(range != null) {
			shape.setAsBox(range[0], range[1]);
		} else {
			shape.setAsBox(1, 1);
		}
		fixtureDef.shape = shape;
		
		body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
	}
	
	public void act() {
		if(listener == null) return;
		listener.use();
	}
	
	public interface InteractionListener {
		void use();
	}
}