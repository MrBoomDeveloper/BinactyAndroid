package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.math.Vector2;

public abstract class CharacterBrain {
	private CharacterEntity entity;

	public void updateHoldingItem() {
		updateHoldingItem(null);
	}

	public void updateHoldingItem(Vector2 power) {
		var item = getEntity().inventory.getCurrentItem();

		if(item != null) {
			if(power != null) item.setPower(power);

			item.update();
		}
	}
	
	public void setEntity(CharacterEntity entity) {
		this.entity = entity;
	}

	public CharacterEntity getEntity() {
		return entity;
	}

	public void update() {}

	public void start() {}

	public void end() {}
}