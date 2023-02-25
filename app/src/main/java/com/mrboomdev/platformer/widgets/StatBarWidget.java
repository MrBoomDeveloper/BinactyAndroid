package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.entity.character.CharacterConfig;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class StatBarWidget extends ActorUtil {
	private Track track;
	private Sprite iconSprite, barSprite, progressSprite;
	private BitmapFont font;
	private float currentProgress;
	public static final int SIZE = 55;
	
	public StatBarWidget(Track track) {
		this.track = track;
		AssetManager asset = GameHolder.getInstance().assets;
		Texture texture = asset.get("ui/overlay/generalIcons.png", Texture.class);
		switch(track) {
			case HEALTH:
				iconSprite = new Sprite(texture, 11, 0, 14, 14);
				iconSprite.setSize(SIZE, SIZE);
				progressSprite = new Sprite(texture, 49, 12, 2, 2);
				break;
			case STAMINA:
				iconSprite = new Sprite(texture, 0, 0, 11, 14);
				iconSprite.setSize(47, SIZE);
				progressSprite = new Sprite(texture, 49, 10, 2, 2);
				break;
		}
		barSprite = new Sprite(texture, 25, 10, 24, 6);
		barSprite.setSize(200, SIZE - 8);
		font = asset.get("statBarWidget.ttf", BitmapFont.class);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		progressSprite.setPosition(getX() + SIZE / 2 + 10, getY() + 5 + 8);
		progressSprite.setSize(getProgress() * 1.8f, SIZE - 20);
		progressSprite.draw(batch);
		barSprite.setPosition(getX() + SIZE / 2, getY() + 5);
		barSprite.draw(batch);
		iconSprite.setPosition(getX() + (track == Track.HEALTH ? 0 : 6.5f), getY());
		iconSprite.draw(batch);
		
		font.draw(batch, getData(), getX() + SIZE / 2, getY() + 35, 200, Align.center, false);
	}
	
	private float getProgress() {
		CharacterConfig.Stats stats = connectedEntity.config.stats;
		float result = 0;
		switch(track) {
			case HEALTH:
				result = stats.health * 100 / stats.maxHealth;
				break;
			case STAMINA:
				result = stats.stamina * 100 / stats.maxStamina;
				break;
		}
		result = currentProgress + (result - currentProgress) * .2f;
		currentProgress = result;
		return result;
	}
	
	private String getData() {
		CharacterConfig.Stats stats = connectedEntity.config.stats;
		switch(track) {
			case HEALTH:
				return stats.health + " / " + stats.maxHealth;
			case STAMINA:
				return (int)stats.stamina + " / " + (int)stats.maxStamina;
			default:
				return "0 / 0";
		}
	}
	
	public enum Track {
		HEALTH,
		STAMINA
	}
}