package com.mrboomdev.platformer.entity.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffect {
	public final List<ParticleInstance> instances = new ArrayList<>();
	public Preset preset;

	public void draw(SpriteBatch batch) {
		var iterator = instances.iterator();

		while(iterator.hasNext()) {
			var particle = iterator.next();
			var animation = preset.animation;

			var sprite = new Sprite(animation.getKeyFrame(particle.progress, false));
			sprite.setFlip(particle.flip, false);
			sprite.setAlpha(.75f);
			sprite.setCenter(particle.position.x, particle.position.y);
			sprite.draw(batch);
			particle.progress += Gdx.graphics.getDeltaTime();

			if(animation.isAnimationFinished(particle.progress)) iterator.remove();
		}
	}

	public void createParticle(Vector2 position, boolean flip) {
		var particle = new ParticleInstance();
		particle.flip = flip;
		particle.position = position;

		instances.add(particle);
	}

	private static class ParticleInstance {
		public Vector2 position;
		public boolean flip;
		public float progress;
	}

	public static class Preset {
		@Json(ignore = true)
		public Animation<Sprite> animation;
		public Entity.Animation skin;
		public FileUtil source;

		public void build(Texture texture) {
			var framesAnimation = new Array<Sprite>();

			for(var frame : skin.frames) {
				var region = new TextureRegion(texture, frame.region[0], frame.region[1], frame.region[2], frame.region[3]);
				var sprite = new Sprite(region);
				sprite.setSize(skin.size[0], skin.size[1]);
				framesAnimation.add(sprite);
			}

			animation = new Animation<>(skin.delay, framesAnimation, Animation.PlayMode.NORMAL);
		}
	}
}