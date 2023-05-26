package com.mrboomdev.platformer.entity.particle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.mrboomdev.platformer.entity.Entity
import com.mrboomdev.platformer.util.io.FileUtil
import com.squareup.moshi.Json

class ParticleEffect {
    private val instances = ArrayList<ParticleInstance>()
    lateinit var preset: Preset

    fun draw(batch: SpriteBatch) {
        val iterator = instances.iterator()
        while(iterator.hasNext()) {
            val particle = iterator.next()
            val sprite = Sprite(preset.animation.getKeyFrame(particle.progress, false))
            sprite.setFlip(particle.flip, false)
            sprite.setAlpha(.75f)
            sprite.setCenter(particle.position.x, particle.position.y)
            sprite.draw(batch)
            particle.progress += Gdx.graphics.deltaTime
            if(preset.animation.isAnimationFinished(particle.progress)) iterator.remove()
        }
    }

    fun createParticle(position: Vector2, flip: Boolean) {
        instances.add(ParticleInstance(position, flip, 0f))
    }

    private data class ParticleInstance(
            val position: Vector2,
            val flip: Boolean,
            var progress: Float
    )

    class Preset {
        @Json(ignore = true) lateinit var animation: Animation<Sprite>
        lateinit var skin: Entity.Animation
        lateinit var source: FileUtil

        fun build(texture: Texture) {
            val framesAnimation = Array<Sprite>()
            for(frame in skin.frames) {
                val region = TextureRegion(texture, frame.region[0], frame.region[1], frame.region[2], frame.region[3])
                val sprite = Sprite(region)
                sprite.setSize(skin.size[0], skin.size[1])
                framesAnimation.add(sprite)
            }
            animation = Animation(skin.delay, framesAnimation, Animation.PlayMode.NORMAL)
        }
    }
}