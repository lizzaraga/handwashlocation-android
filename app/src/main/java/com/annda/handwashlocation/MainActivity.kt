package com.annda.handwashlocation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.NonNull
import com.annda.handwashlocation.databinding.ActivityMainBinding
import com.annda.handwashlocation.helpers.LatLngBundleHelper
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.OnLocationClickListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import java.lang.Exception
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {
    private var mapView: MapView? = null
    private lateinit var viewBinding : ActivityMainBinding
    private lateinit var locationEngine: LocationEngine
    private val DEFAULT_WAIT_TIME_IN_MS: Long = 1000L
    private val MAX_WAIT_TIME_IN_MS: Long = DEFAULT_WAIT_TIME_IN_MS * 5
    private val locationEngineCallback: MainActivityLocationEngineCallback =  MainActivityLocationEngineCallback(this)
    private var permissionManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private lateinit var currentLocation: Location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(
            this,
            "pk.eyJ1IjoibGl6emFyYWdhIiwiYSI6ImNrMmRmdGdjajAyczczY2xrdnh4NmtuamEifQ.qTuyj40o4v74VhT9BsByCw"
        )
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        mapView = viewBinding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        // handle save button click
        viewBinding.btnBeginSavePosition.setOnClickListener {
            val intent = Intent(this, SavePositionActivity::class.java)
            intent.putExtras(LatLngBundleHelper.createBundle(LatLng(currentLocation)))
            startActivity(intent)

        }
        
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }


    override fun onResume() {
        super.onResume()
        //locationEngine.removeLocationUpdates(locationEngineCallback)

        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        locationEngine.removeLocationUpdates(locationEngineCallback)

        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView?.onSaveInstanceState(outState)
        }
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()

        mapView?.onDestroy()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.Builder().fromUri("mapbox://styles/lizzaraga/ckbb4tpt90b1z1imurtusppqz"), Style.OnStyleLoaded {
                //Toast.makeText(this, "Style loaded", Toast.LENGTH_LONG).show()
                enabledUserLocationTracking(it)
            }
        )
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "This permission is needed to use app.", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted) {
            enabledUserLocationTracking(mapboxMap.style!!)
        }
        else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    fun enabledUserLocationTracking(@NonNull style: Style){
        // On teste si la permissions a été accordée sinon on la demande
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            // Creation du location component
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(Color.parseColor("#2e3c50"))
                .build()
            val customLocationComponentActivationOptions = LocationComponentActivationOptions.builder(this, style)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(customLocationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
            }

            mapboxMap.locationComponent.addOnLocationClickListener {
                //Toast.makeText(this, "Click position | Latitude: ${currentLocation.latitude}, Longitude: ${currentLocation.longitude}",
                    //Toast.LENGTH_LONG ).show()
            }
            initLocationEngine()

        }else{
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    fun initLocationEngine(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        val locationEngineRequest = LocationEngineRequest.Builder(DEFAULT_WAIT_TIME_IN_MS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(MAX_WAIT_TIME_IN_MS)
            .build()

        locationEngine.requestLocationUpdates(locationEngineRequest, locationEngineCallback, mainLooper)
        locationEngine.getLastLocation(locationEngineCallback)
    }


    inner class MainActivityLocationEngineCallback (private val activity: MainActivity):
        com.mapbox.android.core.location.LocationEngineCallback<LocationEngineResult>{

        private val weakRefActivity: WeakReference<MainActivity> = WeakReference(activity)
        override fun onSuccess(result: LocationEngineResult?) {
            val location = result?.lastLocation
            val activity =  weakRefActivity.get()
               activity?.currentLocation = location!!
//            Toast.makeText(weakRefActivity.get(),
//                "Latitude: ${location.latitude}, Longitude: ${location.longitude}",
//                Toast.LENGTH_SHORT
//            ).show()


            activity?.mapboxMap?.locationComponent?.forceLocationUpdate(location)
            activity?.mapboxMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location)))
            activity?.viewBinding?.latText?.text = location.latitude.toString()
            activity?.viewBinding?.lngText?.text = location.longitude.toString()
        }

        override fun onFailure(exception: Exception) {
            Toast.makeText(weakRefActivity.get(), "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }

    }
}
