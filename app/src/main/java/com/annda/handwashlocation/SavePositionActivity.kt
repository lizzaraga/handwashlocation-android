package com.annda.handwashlocation

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.annda.handwashlocation.adapters.PositionImagesAdapter
import com.annda.handwashlocation.databinding.ActivitySavePositionBinding
import com.annda.handwashlocation.helpers.ExternalCameraAppHelper
import com.annda.handwashlocation.helpers.LatLngBundleHelper
import com.annda.handwashlocation.helpers.NotificationSoundHelper
import com.annda.handwashlocation.vms.locals.SavePositionVM
import com.mapbox.mapboxsdk.geometry.LatLng

class SavePositionActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySavePositionBinding
    private var latlng: LatLng? = null
    private val images: MutableList<Bitmap> = mutableListOf()
    private val positionImagesAdapter : PositionImagesAdapter = PositionImagesAdapter()

    private val viewModel: SavePositionVM by lazy{
        ViewModelProvider(this).get(SavePositionVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySavePositionBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        latlng = LatLngBundleHelper.getLatLng(intent.extras!!)

        viewModel.images.observe(this, Observer{
            positionImagesAdapter.images = it
            positionImagesAdapter.notifyDataSetChanged()
        })

        viewBinding.recyclerViewPositionImages.also {
            it.layoutManager  = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = positionImagesAdapter
            it.setHasFixedSize(true)
        }

        viewBinding.btnAddImage.setOnClickListener {
            ExternalCameraAppHelper.startCameraIntent(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bitmap: Bitmap? = ExternalCameraAppHelper.getImage(requestCode, resultCode, data)
        if(bitmap != null){
            //images.add(bitmap)
            //positionImagesAdapter.notifyDataSetChanged()
            viewModel.addImages(bitmap)
            NotificationSoundHelper.play(this)
        }
        else Toast.makeText(this, "Image not available", Toast.LENGTH_LONG).show()
    }
}