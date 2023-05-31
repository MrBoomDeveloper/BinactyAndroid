package com.mrboomdev.platformer.entity.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.character.CharacterSkin;

public class ItemInventory {
	public Array<Item> items = new Array<>();
	public int current, capacity = 6;

	public Item getCurrentItem() {
		return items.get(current);
	}
	
	public boolean add(Item newItem) {
		var existingItem = items.select(foundItem -> foundItem.name.equals(newItem.name));
		if((existingItem == null) || (items.size < capacity)) {
			items.add(newItem);
			return true;
		}
		
		return false;
	}
	
	public void draw(SpriteBatch batch, Vector2 position, CharacterSkin characterSkin, boolean isFlip) {
		if(items.isEmpty() || (current >= items.size) || (items.get(current) == null)) return;
		
		var item = items.get(current);
		var offset = item.getOffset(characterSkin);
		var sprite = new Sprite(item.getSprite());
		
		sprite.setFlip(isFlip, false);
		if(!isFlip) {
			sprite.setCenter(position.x + offset.x, position.y + offset.y);
		} else {
			sprite.setCenter(position.x - offset.x, position.y + offset.y);
		}
		
		sprite.draw(batch);
	}
}