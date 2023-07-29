package com.mrboomdev.platformer.entity.character;

import static com.mrboomdev.platformer.entity.Entity.AnimationType.CURRENT;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.DAMAGE;
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
import com.mrboomdev.platformer.util.helper.BoomException;
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
	private Map<Entity.AnimationType, Entity.Animation> animationsJson;
	@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused"})
	@Json(name = "custom_animations")
	private Map<String, Entity.Animation> customAnimationsJson;
	@Json(name = "texture")
	private final String texturePath = "skin.png";
	@Json(ignore = true)
	private final Map<Entity.AnimationType, Animation<Entity.Frame>> animations = new HashMap<>();
	@Json(ignore = true)
	private Entity.AnimationType currentAnimation;
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
	
	public void setAnimation(Entity.AnimationType animation) {
		var selectedAnimation = getValidAnimation(animation);
		if(isAnimationForce && selectedAnimation != currentAnimation) return;

		if(selectedAnimation == null) {
			selectedAnimation = (Entity.AnimationType) animations.keySet().toArray()[0];
		}

		if(((currentAnimation == selectedAnimation) && (!selectedAnimation.isAction()))
			|| (currentAnimation != null && currentAnimation.isAction() &&
				!Objects.requireNonNull(animations.get(currentAnimation)).isAnimationFinished(animationProgress))) return;

		currentAnimation = animations.containsKey(selectedAnimation) ? selectedAnimation : IDLE;
		animationProgress = selectedAnimation.isAction() ? 0 : (float)(Math.random() * 5);
		LogUtil.debug(LogUtil.Tag.ANIMATION, "Set character animation to: " + animation.name());
	}

	public void setAnimationForce(@Nullable Entity.AnimationType animation) {
		if(animation == null) {
			this.isAnimationForce = false;
			return;
		}

		this.setAnimation(animation);
	}

	@SuppressWarnings("unused")
	public void setCustomAnimation(String name) {
		if(!customAnimationsJson.containsKey(name)) return;
		throw new BoomException("This feature isn't done yet!");
	}
	
	private Entity.AnimationType getValidAnimation(@NonNull Entity.AnimationType animation) {
		if(animations.containsKey(animation)) return animation;
		if(animation == CURRENT) return currentAnimation;
		if(animation.isAction() && animation.getAlternatives() == null) return currentAnimation;

		var alternatives = animation.getAlternatives();
		if(alternatives == null) return IDLE;

		for(var alternative : alternatives) {
			if(alternative == CURRENT) return currentAnimation;
			if(animations.containsKey(alternative)) return alternative;
		}

		return IDLE;
	}
	
	public void draw(
			SpriteBatch batch,
			@NonNull Vector2 position,
			@NonNull Direction direction,
			CharacterEntity entity,
			float opacity
	) {
		var activeAnimation = animations.get(currentAnimation);
		animationProgress += Gdx.graphics.getDeltaTime();
		
		sprite = new Sprite(Objects.requireNonNull(activeAnimation).getKeyFrame(animationProgress, activeAnimation.getPlayMode() != PlayMode.NORMAL).sprite);
		sprite.setSize(
			direction.isForward() ? sprite.getWidth() : -sprite.getWidth(),
			sprite.getHeight());
		
		sprite.setAlpha(opacity * (currentAnimation == DAMAGE ? 0.85f : 1));
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
		return Objects.requireNonNull(activeAnimation).getKeyFrame(animationProgress, activeAnimation.getPlayMode() != PlayMode.NORMAL);
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