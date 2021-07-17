package com.example.el_indeciso

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log

class SFX_Manager (val context: Context) {

    private var soundPool: SoundPool
    private val sounds = mutableMapOf<Sound, Int>()
    private val streamsAmount = Sound.values().size

    init {
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val audioAttributes = AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()

                            SoundPool.Builder()
                                .setMaxStreams(streamsAmount)
                                .setAudioAttributes(audioAttributes)
                                .build()
                    } else {
                        SoundPool(streamsAmount, AudioManager.STREAM_MUSIC, 0)
                    }

        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            soundPool.play(sampleId, 1F, 1F, 0, 0, 1F)
        }

        sounds.put(Sound.CARD_DROP, soundPool.load(context, R.raw.card_drop, 1))
        sounds.put(Sound.BUTTON_CLICK, soundPool.load(context, R.raw.button_click, 1))
        sounds.put(Sound.HAPPY_POP_UP, soundPool.load(context, R.raw.happy_pop_up, 1))
        sounds.put(Sound.SAD_POP_UP, soundPool.load(context, R.raw.sad_pop_up, 1))
        sounds.put(Sound.NORMAL_POP_UP, soundPool.load(context, R.raw.normal_pop_up, 1))
    }

    fun play(sound: Sound) {
        val cosito = soundPool.play(sounds[sound]!!, 1F, 1F, 0 , 0 , 1F)
        Log.d("sonidito", "$cosito")
    }

    fun release() {
        soundPool.release()
    }
}