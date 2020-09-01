package com.annda.handwashlocation.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.annda.handwashlocation.R
import kotlinx.android.synthetic.main.view_position_image.view.*

class PositionImagesAdapter(): RecyclerView.Adapter<PositionImagesAdapter.PositionImage>() {


    inner class PositionImage(v: View): RecyclerView.ViewHolder(v){
        val image: ImageView = v.findViewById(R.id.image_view_position_image)
    }

    var images: MutableList<Bitmap> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionImage {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.view_position_image, parent, false)
        return PositionImage(view)
    }

    override fun getItemCount(): Int {
        return images.count()
    }

    override fun onBindViewHolder(holder: PositionImage, position: Int) {
        holder.image.setImageBitmap(images[position])
    }
}