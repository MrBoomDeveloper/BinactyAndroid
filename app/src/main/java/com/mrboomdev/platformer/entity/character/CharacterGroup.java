package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.character.CharacterEntity;

public class CharacterGroup extends CharacterEntity {
	private Array<CharacterEntity> list;
	
	public CharacterGroup(Array<CharacterEntity> list) {
		this.list = list;
	}
	
	@Override
	public void gainDamage(int damage, Vector2 power) {
		for(var character : list) {
			character.gainDamage(damage, power);
		}
	}
}