package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mrboomdev.platformer.util.ActorUtil;

public class StatBarWidget extends ActorUtil {
	private Sprite iconSprite, progressSprite;
	private int progress, maxProgress;
	
	public StatBarWidget(Texture iconTexture, Texture progressTexture, int max) {
		this(progressTexture, max);
		this.iconSprite = new Sprite(iconTexture);
	}
	
	public StatBarWidget(Texture progressTexture, int max) {
		this.progressSprite = new Sprite(progressTexture);
		this.maxProgress = max;
	}
	
	public StatBarWidget setProgress(int progress) {
		this.progress = progress;
		return this;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		
	}
}