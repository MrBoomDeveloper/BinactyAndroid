package com.mrboomdev.platformer.widgets;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;

import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

import org.jetbrains.annotations.Contract;

public class StatBarWidget extends ActorUtil {
	public static final int SIZE = 40;
	private final Track track;
	private final BitmapFont font;
	private Sprite iconSprite;
	private Sprite progressSprite;
	private float currentProgress;
	
	public StatBarWidget(@NonNull Track track) {
		this.track = track;
		GameHolder game = GameHolder.getInstance();
		Texture texture = game.assets.get("ui/overlay/generalIcons.png", Texture.class);
		Texture icons = game.assets.get("ui/overlay/large_icons.png", Texture.class);
		switch(track) {
			case HEALTH:
				iconSprite = new Sprite(icons, 49, 17, 13, 13);
				progressSprite = new Sprite(texture, 2, 14, 2, 2);
				break;
			case STAMINA:
				iconSprite = new Sprite(icons, 33, 33, 13, 14);
				progressSprite = new Sprite(texture, 0, 14, 2, 2);
				break;
		}
		iconSprite.setSize(SIZE, SIZE);
		font = game.assets.get("statBarWidget.ttf", BitmapFont.class);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		iconSprite.setPosition(getX() + (track == Track.HEALTH ? 0 : 6.5f), getY());
		iconSprite.draw(batch);

		currentProgress = getProgress();
		progressSprite.setPosition(getX() + SIZE + 25, getY() + 4);
		progressSprite.setSize(currentProgress * 1.8f, SIZE - 8);
		progressSprite.draw(batch);

		font.draw(batch, getData(), getX() + SIZE + 15, getY() + 26, 200, Align.center, false);
	}

	private float getProgress() {
		if(connectedEntity.stats.maxHealth == 0 || connectedEntity.stats.maxStamina == 0) return 0;
		float result = 0;
		switch(track) {
			case HEALTH:
				result = connectedEntity.stats.health * 100f / connectedEntity.stats.maxHealth;
				break;
			case STAMINA:
				result = connectedEntity.stats.stamina * 100 / connectedEntity.stats.maxStamina;
				break;
		}
		result = currentProgress + (result - currentProgress) * .2f;
		return result;
	}
	
	@NonNull
	@Contract(pure = true)
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