package com.mrboomdev.platformer.environment.logic;

import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;

import java.util.List;

public class Trigger {
	public static List<Trigger> triggers;
	protected boolean isCompleted, isSearching;
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
		isCompleted = true;
	}

	public void update() {
		if(!isSearching && !isCompleted) {
			isSearching = true;
			var game = GameHolder.getInstance();

			game.environment.world.QueryAABB((fixture) -> {
				var unknown = fixture.getBody().getUserData();
				isSearching = false;

				if(unknown instanceof CharacterEntity) {
					boolean isOk = callback.triggered((CharacterEntity) unknown);
					if(isOk) isCompleted = true;
					return !isOk;
				}

				return true;
			}, x - radius, y - radius, x + radius, y + radius);
		}
	}

	public static void updateAll() {
		if(triggers.isEmpty()) return;

		var iterator = triggers.iterator();
		while(iterator.hasNext()) {
			var trigger = iterator.next();

			if(trigger.isCompleted) {
				iterator.remove();
				return;
			}

			trigger.update();
		}
	}

	public interface TriggerCallback {
		boolean triggered(CharacterEntity character);
	}
}