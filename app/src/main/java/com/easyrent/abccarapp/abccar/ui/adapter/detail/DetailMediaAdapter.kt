package com.easyrent.abccarapp.abccar.ui.adapter.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.databinding.AdapterDetailMediaBinding
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem

class DetailMediaAdapter(
    val onImageViewClicked: (String) -> Unit = {}
) : RecyclerView.Adapter<DetailMediaAdapter.PhotoViewHolder>(){

    private val imageUrlList: MutableList<PhotoItem> = mutableListOf()
    fun setImageList(list: List<PhotoItem>) {
        if (itemCount != 0 && list.isNotEmpty()) imageUrlList.clear()
        imageUrlList.addAll(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailMediaAdapter.PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterDetailMediaBinding.inflate(layoutInflater, parent, false)

        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = imageUrlList.size

    override fun onBindViewHolder(holder: DetailMediaAdapter.PhotoViewHolder, position: Int) {
        val url = imageUrlList[position].url

        Glide.with(holder.binding.root)
            .load(url)
            .into(holder.binding.carImage)
        holder.itemView.setOnClickListener{
            onImageViewClicked(url)
        }
    }

    class PhotoViewHolder(
        val binding: AdapterDetailMediaBinding
    ) : RecyclerView.ViewHolder(binding.root)

}