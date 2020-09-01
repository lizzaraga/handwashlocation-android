package com.annda.handwashlocation.helpers

import android.graphics.Bitmap
import com.annda.handwashlocation.data.PositionModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

object DatabaseHelper {
    private val database = Firebase.database.reference
    private val storage = Firebase.storage.reference
    private val positions = database.child("positions")
    private val images = storage.child("images/positions")


    private fun savePositionImages(
        positionModel: PositionModel,
        onSavePositionImageCallback: (() -> Unit)?
    ){
        val byteArrayList = positionModel.images.map {
            val baos = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            baos.toByteArray()
        }

        for ((i, value)in byteArrayList.withIndex()){
            images.child(positionModel.info?.images?.get(i)!!).putBytes(value)
        }
    }

    fun savePositionModel(
        positionModel: PositionModel,
        onSavePositionInfoCallback: (() -> Unit)? = null,
        onSavePositionImageCallback:( () -> Unit)? = null
    ){
        val key: String? = positions.push().key
        positions.child(key!!).setValue(positionModel.info)
            .addOnSuccessListener {
                onSavePositionInfoCallback?.invoke()
                savePositionImages(positionModel, onSavePositionImageCallback)
            }
            .addOnFailureListener {

            }
    }

    fun allPositionModels(){
        positions.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                
            }

        })
    }

    fun getPositionImages(positionModel: PositionModel){

    }



}