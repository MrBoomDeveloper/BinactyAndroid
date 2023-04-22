package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.SerializedName;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.Direction;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;
import java.util.HashMap;
import com.mrboomdev.platformer.entity.Entity;
import static com.mrboomdev.platformer.entity.Entity.Animation.*;
import java.util.Map;

public class CharacterSkin {
	private Map<Entity.Animation, AnimationObject> animations;
	private String texture = "skin.png";
	@Json(ignore = true) Map<Entity.Animation, Animation<Sprite>> animations0 = new HashMap<>();
	@Json(ignore = true) Entity.Animation currentAnimation;
	@Json(ignore = true) Sprite sprite;
	@Json(ignore = true) @SerializedName("skin") HashMap<Entity.Animation, AnimationObject> serailizedAnimations;
	@Json(ignore = true) float animationProgress;
	@Json(ignore = true) int lastframeIndex;
	
	public void setAnimation(Entity.Animation animation) {
		if(currentAnimation == animation) return;
		var selectedAnimation = getValidAnimation(animation);
		currentAnimation = animations0.containsKey(selectedAnimation) ? selectedAnimation : IDLE;
		animationProgress = (float)(Math.random() * 5);
	}
	
	private Entity.Animation getValidAnimation(Entity.Animation animation) {
		switch(animation) {
			case WALK: return animations0.containsKey(WALK) ? WALK : RUN;
			case RUN: return animations0.containsKey(RUN) ? RUN : WALK;
			case DASH: return animations0.containsKey(DASH) ? DASH : IDLE;
			case DAMAGE: return animations0.containsKey(DAMAGE) ? DAMAGE : WALK;
			default: return animation;
		}
	}
	
	public void draw(SpriteBatch batch, Vector2 position, Direction direction) {
		var activeAnimation = animations0.get(currentAnimation);
		animationProgress += Gdx.graphics.getDeltaTime();
		
		sprite = new Sprite(activeAnimation.getKeyFrame(animationProgress, activeAnimation.getPlayMode() != PlayMode.NORMAL));
		sprite.setSize(
			direction.isForward() ? sprite.getWidth() : -sprite.getWidth(),
			sprite.getHeight());
		sprite.setCenter(position.x, position.y);
		sprite.draw(batch);
		
		int frameIndex = activeAnimation.getKeyFrameIndex(animationProgress);
		if((frameIndex == 1 || frameIndex == 6) && frameIndex != lastframeIndex &&
		  (currentAnimation == WALK || currentAnimation == RUN)) {
			var assets = GameHolder.getInstance().assets;
			AudioUtil.play3DSound(assets.get("audio/sounds/step.mp3"), .2f, 15, position);
		}
		lastframeIndex = frameIndex;
	}
	
	public CharacterSkin build(FileUtil source, boolean isNew) {
		Texture texture = new Texture(source.goTo(this.texture).getFileHandle());
		for(HashMap.Entry<Entity.Animation, AnimationObject> entry : animations.entrySet()) {
			AnimationObject object = entry.getValue();
			Sprite[] sprites = new Sprite[object.frames.length];
			for(int i = 0; i < object.frames.length; i++) {
				int[] bounds = object.frames[i];
				TextureRegion region = new TextureRegion(texture, bounds[0], bounds[1], bounds[2], bounds[3]);
				Sprite sprite = new Sprite(region);
				sprite.setSize(object.size[0], object.size[1]);
				sprites[i] = sprite;
			}
			Animation<Sprite> animation = new Animation<>(object.delay, sprites);
			animation.setPlayMode(object.mode != null ? object.mode : PlayMode.LOOP);
			animation.setFrameDuration(object.delay);
			animations0.put(entry.getKey(), animation);
		}
		setAnimation(IDLE);
		return this;
	}
	
	public CharacterSkin build(FileUtil file) {
		Texture texture = new Texture(file.goTo(this.texture).getHandle());
		for(HashMap.Entry<Entity.Animation, AnimationObject> entry : serailizedAnimations.entrySet()) {
			AnimationObject object = entry.getValue();
			Sprite[] sprites = new Sprite[object.frames.length];
			for(int i = 0; i < object.frames.length; i++) {
				int[] bounds = object.frames[i];
				TextureRegion region = new TextureRegion(texture, bounds[0], bounds[1], bounds[2], bounds[3]);
				Sprite sprite = new Sprite(region);
				sprite.setSize(object.size[0], object.size[1]);
				sprites[i] = sprite;
			}
			Animation<Sprite> animation = new Animation<>(object.delay, sprites);
			animation.setPlayMode(object.mode != null ? object.mode : PlayMode.LOOP);
			animation.setFrameDuration(object.delay);
			animations0.put(entry.getKey(), animation);
		}
		setAnimation(IDLE);
		return this;
	}
	
	public static class AnimationObject {
		public float delay;
		public float[] size;
		public int[][] frames;
		public PlayMode mode;
	}
}