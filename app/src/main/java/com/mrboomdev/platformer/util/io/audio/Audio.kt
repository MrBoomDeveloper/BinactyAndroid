package com.mrboomdev.platformer.util.io.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.mrboomdev.platformer.util.io.FileUtil

@Suppress("unused")
class Audio {
    var volume = 1f
    var distance = 15f
    var is3dSetup = false
    val isMusic: Boolean
    var music: Music? = null
    var sound: Sound? = null
    var isStopped = false
    var position: Vector2? = null
    private var isLooping = false

    constructor(music: Music) {
        this.isMusic = true
        this.music = music
    }

    constructor(sound: Sound) {
        this.isMusic = false
        this.sound = sound
    }

    constructor(source: FileUtil, path: String, isMusic: Boolean) {
        this.isMusic = isMusic
        if(isMusic) {
            music = source.goTo(path).getLoaded(Music::class.java)
        } else {
            sound = source.goTo(path).getLoaded(Sound::class.java)
        }
    }

    fun play(): Audio {
        if(position != null) {
            setPosition(position!!.x, position!!.y)
        }

        isStopped = false
        AudioUtil.updateSingle(this)
        music?.play()
        music?.volume = volume
        music?.isLooping = isLooping
        return this
    }

    fun stop(): Audio {
        isStopped = true
        music?.stop()
        return this
    }

    fun setLooping(loop: Boolean): Audio {
        this.isLooping = loop
        music?.isLooping = loop
        return this
    }

    fun setDistance(distance: Float): Audio {
        this.distance = distance
        return this
    }

    fun setVolume(volume: Float): Audio {
        music?.volume = volume
        this.volume = volume
        return this
    }

    fun setPosition(x: Float, y: Float): Audio {
        if(!is3dSetup) {
            AudioUtil.activeAudio.add(this)
            position = Vector2()
            is3dSetup = true
        }

        position?.set(x, y)
        return this
    }
}