package com.easyrent.abccarapp.abccar.ui.adapter.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.databinding.AdapterDetailSpecItemBinding

class DetailSpecAdapter(
    val list : List<Pair<String,String>>
)
    :RecyclerView.Adapter<DetailSpecAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailSpecAdapter.MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterDetailSpecItemBinding.inflate(layoutInflater, parent, false)

        return DetailSpecAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailSpecAdapter.MyViewHolder, position: Int) {
        holder.binding.tvDetailSpecLabel.text = list[position].first
        holder.binding.tvDetailSpecValue.text = list[position].second
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(
        val binding: AdapterDetailSpecItemBinding
    ) : RecyclerView.ViewHolder(binding.root)
}

