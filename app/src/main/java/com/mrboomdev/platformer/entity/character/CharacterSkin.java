package com.mrboomdev.platformer.entity.character;

import static com.mrboomdev.platformer.entity.Entity.AnimationType.IDLE;

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

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CharacterSkin {
	@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused"})
	@Json(name = "animations")
	private Map<String, Entity.Animation> animationsJson;
	@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused"})
	@Json(name = "texture")
	private final String texturePath = "skin.png";
	@Json(ignore = true)
	private final Map<String, Animation<Entity.Frame>> animations = new HashMap<>();
	@Json(ignore = true)
	private String currentAnimation;
	@Json(ignore = true)
	private Sprite sprite;
	@Json(ignore = true)
	private float animationProgress;
	@Json(ignore = true)
	private int lastFrameIndex;
	@Json(ignore = true)
	private final GameHolder game = GameHolder.getInstance();
	@Json(ignore = true)
	private boolean isAnimationForce;

	public void setAnimationForce(@Nullable String animationName) {
		isAnimationForce = animationName != null;
		if(!isAnimationForce || !animations.containsKey(animationName)) return;

		setAnimationNow(animationName);
	}

	public void setAnimation(@NonNull Entity.AnimationType animationType) {
		if(isAnimationForce) return;

		var name = animationType.name().toLowerCase();
		if(animations.containsKey(name)) {
			setAnimation(name);
			return;
		}

		for(var alternative : animationType.getAlternatives()) {
			var alternativeName = alternative.name().toLowerCase();
			if(animations.containsKey(alternativeName)) {
				setAnimation(alternativeName);
				return;
			}
		}

		setAnimation("idle");
	}
	
	public void setAnimation(String animationType) {
		if(isAnimationForce) return;

		if(animationType == null || isShouldSkipAnimation(animationType)) return;

		var animation = getAnimationDeclaration(animationType);

		setAnimationNow(animationType);
		animationProgress = animation.isAction ? 0 : (float)(Math.random() * 5);
	}

	public void setAnimationNow(String name) {
		animationProgress = 0;
		currentAnimation = name;
		LogUtil.debug(LogUtil.Tag.ANIMATION, hashCode() + " : Set character animation to: " + name);
	}

	private boolean isShouldSkipAnimation(String animationType) {
		if(!animations.containsKey(animationType)) return true;

		if(currentAnimation != null) {
			var selectedAnimationInfo = getAnimationDeclaration(animationType);
			var activeAnimationInfo = getCurrentAnimationDeclaration();
			var activeAnimation = Objects.requireNonNull(animations.get(currentAnimation));

			if(activeAnimationInfo.overridable == Entity.Overridable.NEVER && !selectedAnimationInfo.force)
				return true;

			if(activeAnimationInfo.overridable == Entity.Overridable.ANYTIME_OTHER) 
				return Objects.equals(animationType, currentAnimation);

			if(activeAnimationInfo.overridable == Entity.Overridable.ANYTIME)
				return false;

			if(activeAnimationInfo.overridable == Entity.Overridable.UNTIL_END && !selectedAnimationInfo.force) {
				return !activeAnimation.isAnimationFinished(animationProgress);
			}

			if(selectedAnimationInfo.force) return false;
		}

		return false;
	}
	
	public void draw(
			SpriteBatch batch,
			@NonNull Vector2 position,
			@NonNull Direction direction,
			CharacterEntity entity,
			float opacity
	) {
		var activeAnimation = Objects.requireNonNull(animations.get(currentAnimation));
		animationProgress += Gdx.graphics.getDeltaTime();

		boolean isLooping = activeAnimation.getPlayMode() != PlayMode.NORMAL;
		sprite = new Sprite(activeAnimation.getKeyFrame(animationProgress, isLooping).sprite);

		sprite.setSize(
			direction.isForward() ? sprite.getWidth() : -sprite.getWidth(),
			sprite.getHeight());
		
		sprite.setAlpha(opacity * (Objects.equals(currentAnimation, "damage") ? 0.85f : 1));
		sprite.setCenter(position.x, position.y);
		sprite.draw(batch);
		
		int frameIndex = activeAnimation.getKeyFrameIndex(animationProgress);
		if((frameIndex == 1 || frameIndex == 6) && frameIndex != lastFrameIndex &&
		  (Objects.equals(currentAnimation, "walk") || Objects.equals(currentAnimation, "run"))) {
			AudioUtil.play3DSound(game.assets.get("audio/sounds/step.mp3"), .2f, 15, position);
		}

		lastFrameIndex = frameIndex;

		if(Objects.equals(currentAnimation, "run") && frameIndex == 1) {
			game.environment.particles.createParticle("__dust",
					position.cpy().add(0, entity.worldBody.bottom[3]), direction.isBackward());
		}
	}

	public Entity.Animation getCurrentAnimationDeclaration() {
		return animationsJson.get(currentAnimation);
	}

	public Entity.Animation getAnimationDeclaration(String name) {
		return animationsJson.get(name);
	}
	
	public Entity.Frame getCurrentFrame() {
		var activeAnimation = Objects.requireNonNull(animations.get(currentAnimation));
		return activeAnimation.getKeyFrame(animationProgress, activeAnimation.getPlayMode() != PlayMode.NORMAL);
	}
	
	public CharacterSkin build(@NonNull FileUtil source) {
		Texture texture = new Texture(source.goTo(texturePath).getFileHandle());
		for(var entry : animationsJson.entrySet()) {
			Array<Entity.Frame> frames = Array.with(entry.getValue().frames);

			for(var frame : frames) {
				frame.fillEmpty(entry.getValue());
				var region = new TextureRegion(texture, frame.region[0], frame.region[1], frame.region[2], frame.region[3]);
				frame.sprite = new Sprite(region);
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