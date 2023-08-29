package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.math.Vector2;

public interface BotTarget {
	Vector2 getPosition();

	class SimpleBotTarget implements BotTarget {
		private Vector2 position;

		public SimpleBotTarget(float x, float y) {
			setPosition(x, y);
		}

		public void setPosition(float x, float y) {
			this.position = new Vector2(x, y);
		}

		@Override
		public Vector2 getPosition() {
			return position;
		}
	}
}