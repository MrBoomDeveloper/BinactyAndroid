package com.mrboomdev.platformer.environment.logic;

import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;

import java.util.List;

public class Trigger {
	public static List<Trigger> triggers;
	private final float radius, x, y;
	private final TriggerCallback callback;

	public Trigger(float x, float y, float radius, TriggerCallback callback) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.callback = callback;
		triggers.add(this);
	}

	public void remove() {
		triggers.remove(this);
	}

	public static void update() {
		if(triggers.isEmpty()) return;
		var game = GameHolder.getInstance();

		for(var trigger : triggers) {
			game.environment.world.QueryAABB((fixture) -> {
				var unknown = fixture.getBody().getUserData();

				if(unknown instanceof CharacterEntity) {
					trigger.callback.triggered((CharacterEntity) unknown);
				}

				return true;
			},
			trigger.x - trigger.radius,
			trigger.y - trigger.radius,
			trigger.x + trigger.radius,
			trigger.y + trigger.radius);
		}
	}

	public interface TriggerCallback {
		void triggered(CharacterEntity character);
	}
}