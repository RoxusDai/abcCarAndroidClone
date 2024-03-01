package com.easyrent.abccarapp.abccar.ui.adapter.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.AdapterDescribeImageBinding


class DetailDescribePicAdapter(
    private val imageUrls: List<String>,
    val onImageViewClicked: (String) -> Unit = {}

)
    :
    RecyclerView.Adapter<DetailDescribePicAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = AdapterDescribeImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ///去除 resize url
        val realurl = imageUrls[position].replace("https://img.abccar.com.tw/resize?key=650x435/","")
        Glide.with(holder.binding.root)
            .load(realurl)
            .placeholder(R.drawable.ic_detail_car_id)
//            .override(650, 435)
            .into(holder.binding.ivDescribeImage)

        holder.binding.root.setOnClickListener {
            onImageViewClicked(realurl)
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    class ViewHolder(
        val binding: AdapterDescribeImageBinding
    ) : RecyclerView.ViewHolder(binding.root)
}