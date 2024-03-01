package com.easyrent.abccarapp.abccar.ui.adapter.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.databinding.AdapterDetailAccessoriesItemBinding


class DetailAccessoriesAdapter(): RecyclerView.Adapter<DetailAccessoriesAdapter.DetailAccessoriesItemViewHolder>() {

    val accessoriesList : MutableList<Pair<Int, String>> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailAccessoriesAdapter.DetailAccessoriesItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterDetailAccessoriesItemBinding.inflate(layoutInflater, parent, false)
        return DetailAccessoriesAdapter.DetailAccessoriesItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DetailAccessoriesAdapter.DetailAccessoriesItemViewHolder,
        position: Int
    ) {
        holder.binding.tvDetailAccessoriesItem.text = accessoriesList[position].second
    }

    override fun getItemCount(): Int {
        return accessoriesList.size
    }

    class DetailAccessoriesItemViewHolder(
        val binding: AdapterDetailAccessoriesItemBinding
    ) : RecyclerView.ViewHolder(binding.root)
}