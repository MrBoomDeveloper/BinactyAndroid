package com.mrboomdev.platformer.util.io.audio

import com.badlogic.gdx.Gdx
import com.mrboomdev.platformer.util.AudioUtil as JavaAudioUtil

private var updateReloadProgress = 1f

class AudioUtil {
    companion object {
        val activeAudio = ArrayList<Audio>()

        fun update() {
            if(updateReloadProgress < .5f) {
                updateReloadProgress += Gdx.graphics.deltaTime
                return
            }

            val iterator = activeAudio.iterator()
            while(iterator.hasNext()) {
                val audio = iterator.next()
                if(audio.isStopped) {
                    audio.is3dSetup = false
                    iterator.remove()
                    continue
                }

                if(audio.isMusic) {
                    val resultVolume = JavaAudioUtil.getVolume(
                            audio.position, audio.distance) * audio.volume * JavaAudioUtil.musicVolume

                    audio.music?.volume = resultVolume
                }
            }
        }
    }
}