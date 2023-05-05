package com.mrboomdev.platformer.entity.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ItemInventory {
	public Array<Item> items = new Array<>();
	public int current, capacity = 6;
	
	public boolean add(Item newItem) {
		var existingItem = items.select(foundItem -> foundItem.name.equals(newItem));
		if((existingItem == null) || (items.size < capacity)) {
			items.add(newItem);
			return true;
		}
		
		return false;
	}
	
	public void draw(SpriteBatch batch, Vector2 position) {
		if(items.isEmpty() || (current >= items.size) || (items.get(current) == null)) return;
		var sprite = new Sprite(items.get(current).getSprite(position));
		sprite.draw(batch);
	}
}