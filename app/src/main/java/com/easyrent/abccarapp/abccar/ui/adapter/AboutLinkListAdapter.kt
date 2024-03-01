package com.easyrent.abccarapp.abccar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.easyrent.abccarapp.abccar.databinding.AdapterAboutItemListLayoutBinding

class AboutLinkListAdapter(
    private val itemList: List<AboutItem>
) : RecyclerView.Adapter<AboutLinkListAdapter.AboutItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterAboutItemListLayoutBinding.inflate(layoutInflater, parent, false)
        return AboutItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: AboutItemViewHolder, position: Int) {
        holder.binding.apply {

            tvItemLink.text = itemList[position].linkName
            ivItemIcon.setImageResource(itemList[position].icon)

            root.setOnClickListener {
                itemList[position].onClickEvent.invoke()
            }
        }
    }


    class AboutItemViewHolder(
        val binding: AdapterAboutItemListLayoutBinding
    ) : ViewHolder(binding.root)

}

data class AboutItem(
    @DrawableRes val icon: Int,
    val linkName: String,
    val onClickEvent: () -> Unit
)