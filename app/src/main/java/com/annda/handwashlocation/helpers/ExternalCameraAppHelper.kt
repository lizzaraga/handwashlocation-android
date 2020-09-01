package com.annda.handwashlocation.helpers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore

object ExternalCameraAppHelper {
    private const val REQUEST_CODE = 1062

    fun startCameraIntent(activity: Activity){
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        activity.startActivityForResult(intent, REQUEST_CODE)
    }

    fun getImage(requestCode: Int, resultCode: Int, data: Intent?): Bitmap?{
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE){
                return data?.extras?.get("data") as Bitmap
            }
        }
        return null
    }
}