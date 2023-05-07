package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class StatBarWidget extends ActorUtil {
	private Track track;
	private Sprite iconSprite, barSprite, progressSprite;
	private GameHolder game = GameHolder.getInstance();
	private BitmapFont font;
	private float currentProgress;
	public static final int SIZE = 50;
	
	public StatBarWidget(Track track) {
		this.track = track;
		Texture texture = game.assets.get("ui/overlay/generalIcons.png", Texture.class);
		Texture icons = game.assets.get("ui/overlay/large_icons.png", Texture.class);
		switch(track) {
			case HEALTH:
				iconSprite = new Sprite(icons, 49, 17, 14, 14);
				progressSprite = new Sprite(texture, 49, 12, 2, 2);
				break;
			case STAMINA:
				iconSprite = new Sprite(icons, 33, 33, 13, 14);
				progressSprite = new Sprite(texture, 49, 10, 2, 2);
				break;
		}
		iconSprite.setSize(SIZE, SIZE);
		barSprite = new Sprite(texture, 25, 10, 24, 6);
		barSprite.setSize(200, SIZE - 8);
		font = game.assets.get("statBarWidget.ttf", BitmapFont.class);
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
		
		font.draw(batch, getData(), getX() + SIZE / 2, getY() + 33, 200, Align.center, false);
	}
	
	private float getProgress() {
		if(connectedEntity.stats.maxHealth == 0 || connectedEntity.stats.maxStamina == 0) return 0;
		float result = 0;
		switch(track) {
			case HEALTH:
				result = connectedEntity.stats.health * 100 / connectedEntity.stats.maxHealth;
				break;
			case STAMINA:
				result = connectedEntity.stats.stamina * 100 / connectedEntity.stats.maxStamina;
				break;
		}
		result = currentProgress + (result - currentProgress) * .2f;
		currentProgress = result;
		return result;
	}
	
	private String getData() {
		switch(track) {
			case HEALTH:
				return connectedEntity.stats.health + " / " + connectedEntity.stats.maxHealth;
			case STAMINA:
				return (int)connectedEntity.stats.stamina + " / " + (int)connectedEntity.stats.maxStamina;
			default:
				return "0 / 0";
		}
	}
	
	public enum Track {
		HEALTH,
		STAMINA
	}
}