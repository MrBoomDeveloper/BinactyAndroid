package com.mrboomdev.platformer.entity.character;

import static com.mrboomdev.platformer.entity.Entity.AnimationType.DAMAGE;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.DASH;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.IDLE;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.RUN;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.WALK;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.Direction;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.squareup.moshi.Json;

import java.util.HashMap;
import java.util.Map;

public class CharacterSkin {
	@Json(name = "animations") Map<Entity.AnimationType, Entity.Animation> animationsJson;
	@Json(name = "texture") String texturePath = "skin.png";
	@Json(ignore = true) Map<Entity.AnimationType, Animation<Entity.Frame>> animations = new HashMap<>();
	@Json(ignore = true) Entity.AnimationType currentAnimation;
	@Json(ignore = true) Sprite sprite;
	@Json(ignore = true) float animationProgress;
	@Json(ignore = true) int lastFrameIndex;
	@Json(ignore = true) GameHolder game = GameHolder.getInstance();
	
	public void setAnimation(Entity.AnimationType animation) {
		if(currentAnimation == getValidAnimation(animation)) return;
		var selectedAnimation = getValidAnimation(animation);
		currentAnimation = animations.containsKey(selectedAnimation) ? selectedAnimation : IDLE;
		animationProgress = (float)(Math.random() * 5);
		LogUtil.debug(LogUtil.Tag.ANIMATION, "Set character animation to: " + animation.name());
	}
	
	private Entity.AnimationType getValidAnimation(@NonNull Entity.AnimationType animation) {
		switch(animation) {
			case WALK: return animations.containsKey(WALK) ? WALK : RUN;
			case RUN: return animations.containsKey(RUN) ? RUN : WALK;
			case DASH: return animations.containsKey(DASH) ? DASH : IDLE;
			case DAMAGE: return animations.containsKey(DAMAGE) ? DAMAGE : WALK;
			default: return animation;
		}
	}
	
	public void draw(SpriteBatch batch, @NonNull Vector2 position, @NonNull Direction direction, CharacterEntity entity) {
		var activeAnimation = animations.get(currentAnimation);
		animationProgress += Gdx.graphics.getDeltaTime();
		
		sprite = new Sprite(activeAnimation.getKeyFrame(animationProgress, activeAnimation.getPlayMode() != PlayMode.NORMAL).sprite);
		sprite.setSize(
			direction.isForward() ? sprite.getWidth() : -sprite.getWidth(),
			sprite.getHeight());
		
		sprite.setAlpha(currentAnimation == DAMAGE ? 0.85f : 1);
		sprite.setCenter(position.x, position.y);
		sprite.draw(batch);
		
		int frameIndex = activeAnimation.getKeyFrameIndex(animationProgress);
		if((frameIndex == 1 || frameIndex == 6) && frameIndex != lastFrameIndex &&
		  (currentAnimation == WALK || currentAnimation == RUN)) {
			AudioUtil.play3DSound(game.assets.get("audio/sounds/step.mp3"), .2f, 15, position);
		}
		lastFrameIndex = frameIndex;

		if(currentAnimation == RUN && frameIndex == 1) {
			game.environment.particles.createParticle("__dust",
					position.cpy().add(0, entity.worldBody.bottom[3]), direction.isBackward());
		}
	}
	
	public Entity.Frame getCurrentFrame() {
		var activeAnimation = animations.get(currentAnimation);
		return activeAnimation.getKeyFrame(animationProgress, activeAnimation.getPlayMode() != PlayMode.NORMAL);
	}
	
	public CharacterSkin build(@NonNull FileUtil source) {
		Texture texture = new Texture(source.goTo(texturePath).getFileHandle());
		for(HashMap.Entry<Entity.AnimationType, Entity.Animation> entry : animationsJson.entrySet()) {
			Array<Entity.Frame> frames = Array.with(entry.getValue().frames);
			for(var frame : frames) {
				frame.fillEmpty(entry.getValue());
				frame.sprite = new Sprite(new TextureRegion(texture, frame.region[0], frame.region[1], frame.region[2], frame.region[3]));
				frame.sprite.setSize(entry.getValue().size[0], entry.getValue().size[1]);
			}
			var mode = entry.getValue().mode != null ? entry.getValue().mode : PlayMode.LOOP;
			Animation<Entity.Frame> animation = new Animation<>(entry.getValue().delay, frames, mode);
			animations.put(entry.getKey(), animation);
		}
		setAnimation(IDLE);
		return this;
	}
}