package com.easyrent.abccarapp.abccar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.databinding.AdapterEditorSelectorItemBinding

class EditorSelectorAdapter(
    private val list: List<String> = mutableListOf(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<EditorSelectorAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterEditorSelectorItemBinding.inflate(layoutInflater, parent, false)

        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.binding.tvItemName.text = list[position]

        holder.binding.tvItemName.setOnClickListener {
            onItemSelected(position)
        }
    }


    class ItemViewHolder(
        val binding: AdapterEditorSelectorItemBinding
    ): RecyclerView.ViewHolder(binding.root)

}