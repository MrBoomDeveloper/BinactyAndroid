package com.mrboomdev.platformer.entity.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.character.CharacterSkin;

public class ItemInventory {
	public Array<Item> items = new Array<>();
	public int capacity = 6;
	private int current;

	public Item getCurrentItem() {
		if(current >= items.size) return null;

		return items.get(current);
	}

	public int getCurrentItemIndex() {
		return this.current;
	}

	public void setCurrentItem(int index) {
		var wasItem = getCurrentItem();

		if(wasItem != null) wasItem.setIsSelected(false);

		this.current = index;

		var newItem = getCurrentItem();
		if(newItem != null) newItem.setIsSelected(true);
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
		if(items.isEmpty() || (current >= items.size) || (getCurrentItem() == null)) return;
		
		var item = getCurrentItem();
		item.update();

		var offset = item.getOffset(characterSkin);

		var gotSprite = item.getSprite();
		if(gotSprite == null) return;

		var sprite = new Sprite(gotSprite);

		float x = position.x + (offset.x * (isFlip ? -1 : 1));
		float y = position.y + offset.y;
		
		sprite.setFlip(isFlip, false);

		//TODO: Well instead of being rotated inside of the hand its just races in all around map!
		//sprite.setOrigin(x, y);
		//sprite.setRotation(item.degree);

		sprite.setCenter(x, y);
		sprite.draw(batch);
	}
}