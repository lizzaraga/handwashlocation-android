package com.annda.handwashlocation.helpers

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import com.annda.handwashlocation.R

object NotificationSoundHelper {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context){
        mediaPlayer = MediaPlayer.create(context, R.raw.insight)
        mediaPlayer?.start()
    }
}