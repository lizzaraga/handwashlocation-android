package com.annda.handwashlocation.helpers

import android.location.Location
import android.os.Bundle
import com.mapbox.mapboxsdk.geometry.LatLng

object LatLngBundleHelper {

    private const val COORDS_LAT = "Latitude"
    private const val COORDS_LNG = "Longitude"

    fun createBundle(latLng: LatLng): Bundle{
        val bundle = Bundle()
        bundle.putDouble(COORDS_LAT, latLng.latitude)
        bundle.putDouble(COORDS_LNG, latLng.longitude)
        return bundle
    }

    fun getLatLng(bundle: Bundle): LatLng?{
        var latLng: LatLng? = null
        if(bundle.containsKey(COORDS_LNG) && bundle.containsKey(COORDS_LAT))
            latLng = LatLng(bundle.getDouble(COORDS_LAT), bundle.getDouble(COORDS_LNG))
        return latLng
    }
}