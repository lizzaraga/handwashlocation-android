package com.annda.handwashlocation.vms.locals

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annda.handwashlocation.data.PositionInfo
import com.annda.handwashlocation.data.PositionModel
import com.annda.handwashlocation.helpers.DatabaseHelper
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SavePositionVM: ViewModel() {

    // LatLng
    private val _latlng : MutableLiveData<LatLng> = MutableLiveData()
    val latlng : LiveData<LatLng> = _latlng

    // Description
    private val _description: MutableLiveData<String> = MutableLiveData()
    val description: LiveData<String> = _description

    // Images
    private val _imagesList: MutableList<Bitmap> = mutableListOf()
    private val _imagesListLiveData : MutableLiveData<MutableList<Bitmap>> = MutableLiveData()
    val images : LiveData<MutableList<Bitmap>>  = _imagesListLiveData

    init {
        _imagesListLiveData.value = _imagesList
    }

    fun addImages(bitmap: Bitmap){
        _imagesList.add(bitmap)
        _imagesListLiveData.value = _imagesList
    }

    fun savePosition(latlng: LatLng, description: String, images: MutableList<Bitmap>) = viewModelScope.launch{
        val filenames = images.map {
            "${UUID.randomUUID().toString()}.jpg"
        }
        val positionInfo = PositionInfo().apply {
            this.description = description
            lat = latlng.latitude
            lng = latlng.longitude
            this.images = filenames
        }

        val positionModel = PositionModel().apply {
            info = positionInfo
            this.images = images
        }

        launch(Dispatchers.IO) {
            DatabaseHelper.savePositionModel(positionModel)
        }

    }


}