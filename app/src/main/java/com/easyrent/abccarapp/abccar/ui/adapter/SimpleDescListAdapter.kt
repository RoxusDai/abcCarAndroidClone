package com.easyrent.abccarapp.abccar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.databinding.AdapterSimpleDescItemBinding

class SimpleDescListAdapter : RecyclerView.Adapter<SimpleDescListAdapter.DescViewHolder>() {

    private val descList = mutableListOf<String>()

    fun setList(list: List<String>) {
        descList.clear()
        descList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterSimpleDescItemBinding.inflate(layoutInflater, parent, false)
        return DescViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return descList.size
    }

    override fun onBindViewHolder(holder: DescViewHolder, position: Int) {
        holder.binding.tvDesc.text = descList[position]
    }

    class DescViewHolder(
        val binding: AdapterSimpleDescItemBinding
    ) : RecyclerView.ViewHolder(binding.root)
}