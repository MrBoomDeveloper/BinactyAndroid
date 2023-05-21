package com.mrboomdev.platformer.entity.particle

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ObjectMap
import com.mrboomdev.platformer.util.io.FileUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ParticleManager(val world: World) {
    private val particles = ObjectMap<String, ParticleEffect>()

    fun draw(batch: SpriteBatch) {
        for(particle in particles.values()) {
            particle.draw(batch)
        }
    }

    fun createParticle(name: String, position: Vector2, flip: Boolean) {
        if(!particles.containsKey(name)) return
        val particle = particles.get(name)
        if(particle.preset.skin.sprite == null) {
            particle.preset.build(particle.preset.source.goTo(particle.preset.skin.texture).getLoaded(Texture::class.java))
        }
        particle.createParticle(position, flip)
    }

    fun loadParticle(file: FileUtil, name: String) {
        val particle = ParticleEffect()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(ParticleEffect.Preset::class.java)
        particle.preset = adapter.fromJson(file.goTo("manifest.json").readString(true))!!
        particle.preset.source = file
        file.goTo(particle.preset.skin.texture).loadAsync(Texture::class.java)
        particles.put(name, particle)
    }
}