package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.google.gson.annotations.SerializedName;
import com.mrboomdev.platformer.util.Direction;
import java.util.HashMap;
import static com.mrboomdev.platformer.entity.character.CharacterSkin.CharacterAnimation.*;

public class CharacterSkin {
	private HashMap<CharacterAnimation, Animation<Sprite>> animations = new HashMap<>();
	private CharacterAnimation currentAnimation;
	private Sprite sprite;
	private float animationProgress;
	
	@SerializedName("skin")
	private HashMap<CharacterAnimation, AnimationObject> serailizedAnimations;
	
	public void setAnimation(CharacterAnimation animation) {
		if(animations.containsKey(animation)) {
			currentAnimation = animation;
		} else {
			switch(animation) {
				case WALK:
					setAnimation(animations.containsKey(RUN) ? RUN : IDLE);
					break;
				case RUN:
					setAnimation(animations.containsKey(WALK) ? WALK : IDLE);
					break;
				case DASH:
					setAnimation(animations.containsKey(RUN) ? RUN : IDLE);
					break;
				default:
					setAnimation(IDLE);
					break;
			}
		}
		animationProgress = (float)(Math.random() * 5);
	}
	
	public void draw(SpriteBatch batch, Vector2 position, Direction direction) {
		animationProgress += Gdx.graphics.getDeltaTime();
		sprite = new Sprite(animations.get(currentAnimation).getKeyFrame(animationProgress, true));
		sprite.setSize(
			direction.isForward() ? sprite.getWidth() : -sprite.getWidth(),
			sprite.getHeight());
		sprite.setCenter(position.x, position.y);
		sprite.draw(batch);
	}
	
	public CharacterSkin build() {
		Texture texture = new Texture(Gdx.files.internal("world/player/characters/klarrie/skin.png"));
		for(HashMap.Entry<CharacterAnimation, AnimationObject> entry : serailizedAnimations.entrySet()) {
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
			animations.put(entry.getKey(), animation);
		}
		setAnimation(IDLE);
		return this;
	}
	
	public enum CharacterAnimation {
		IDLE,
		WALK,
		RUN,
		DASH,
		ATTACK,
		SHOOT,
		DAMAGE,
		DEATH
	}
	
	public class AnimationObject {
		public float delay = .1f;
		public float[] size;
		public int[][] frames;
	}
}