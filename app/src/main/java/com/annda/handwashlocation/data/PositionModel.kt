package com.annda.handwashlocation.data

import android.graphics.Bitmap

data class PositionInfo(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var description: String = "",
    var images: List<String> = listOf()

)

class PositionModel {
    var info: PositionInfo? = null
    var images: MutableList<Bitmap> = mutableListOf()

    fun hasImages(): Boolean = images.size > 0
}