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
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.util.Direction;
import com.mrboomdev.platformer.util.FileUtil;
import java.util.HashMap;
import com.mrboomdev.platformer.entity.Entity;
import static com.mrboomdev.platformer.entity.Entity.Animation.*;

public class CharacterSkin {
	private HashMap<Entity.Animation, Animation<Sprite>> animations = new HashMap<>();
	private Entity.Animation currentAnimation;
	private Sprite sprite;
	private float animationProgress;
	
	@SerializedName("skin")
	private HashMap<Entity.Animation, AnimationObject> serailizedAnimations;
	
	@SerializedName("texture")
	private String texturePath = "skin.png";
	
	public void setAnimation(Entity.Animation animation) {
		if(animation == currentAnimation) return;
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
					setAnimation(animations.containsKey(DASH) ? DASH : IDLE);
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
	
	public CharacterSkin build(FileUtil file) {
		Texture texture = new Texture(file.goTo(texturePath).getHandle());
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
			animations.put(entry.getKey(), animation);
		}
		setAnimation(IDLE);
		return this;
	}
	
	public class AnimationObject {
		public float delay = .1f;
		public float[] size;
		public int[][] frames;
	}
}