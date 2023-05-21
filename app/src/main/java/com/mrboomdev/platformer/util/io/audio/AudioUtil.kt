package com.mrboomdev.platformer.util.io.audio

import com.mrboomdev.platformer.util.AudioUtil as JavaAudioUtil

class AudioUtil {
    companion object {
        val activeAudio = ArrayList<Audio>()

        fun update() {
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

                    if(resultVolume <= 0) continue
                    audio.music?.volume = resultVolume
                }
            }
        }
    }
}