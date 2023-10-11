package com.mrboomdev.platformer.entity.particle;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.io.IOException;
import java.util.Objects;

public class ParticleManager {
	private final ObjectMap<String, ParticleEffect> particles = new ObjectMap<>();

	public void draw(SpriteBatch batch) {
		for(var particle : particles.values()) {
			particle.draw(batch);
		}
	}

	public void createParticle(String name, Vector2 position, boolean flip) {
		if(!particles.containsKey(name)) return;

		var particle = particles.get(name);
		if(particle.preset.skin.sprite == null) {
			particle.preset.build(particle.preset.source.goTo(particle.preset.skin.texture).getLoaded(Texture.class));
		}

		particle.createParticle(position, flip);
	}

	public void loadParticle(@NonNull FileUtil file, String name) {
		var particle = new ParticleEffect();
		var adapter = Constants.moshi.adapter(ParticleEffect.Preset.class);

		try {
			particle.preset = adapter.fromJson(file.goTo("manifest.json").readString());
		} catch(IOException e) {
			throw BoomException.builder("Failed to deserialize a particle: ").addQuoted(name).build();
		}

		Objects.requireNonNull(particle.preset).source = file;
		file.goTo(particle.preset.skin.texture).loadAsync(Texture.class);
		particles.put(name, particle);
	}
}