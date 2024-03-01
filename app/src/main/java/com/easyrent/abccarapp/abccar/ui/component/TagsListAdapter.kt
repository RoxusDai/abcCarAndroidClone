package com.easyrent.abccarapp.abccar.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.easyrent.abccarapp.abccar.databinding.AdapterTagePickerItemBinding

/**
 *  選配Adapter
 *
 */
class TagsListAdapter : RecyclerView.Adapter<TagsListAdapter.TagsItemViewHolder>() {

    private val tagsList = mutableListOf<TagsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterTagePickerItemBinding.inflate(layoutInflater, parent, false)
        return TagsItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    override fun onBindViewHolder(holder: TagsItemViewHolder, position: Int) {
        holder.binding.ctItemTag.text = tagsList[position].name
        holder.binding.ctItemTag.isChecked = tagsList[position].isChecked
        holder.binding.root.setOnClickListener {
            val isChecked = !holder.binding.ctItemTag.isChecked
            holder.binding.ctItemTag.isChecked = isChecked
            tagsList[position].isChecked = isChecked
        }
    }

    fun setList(list: List<TagsItem>) {
        tagsList.clear()
        tagsList.addAll(list)
        notifyItemRangeChanged(0, list.size)
    }

    fun selectAll() {
        tagsList.forEach {
            it.isChecked = true
        }
        notifyItemRangeChanged(0, tagsList.size)
    }

    fun cancelAll() {
        tagsList.forEach {
            it.isChecked = false
        }
        notifyItemRangeChanged(0, tagsList.size)
    }

    fun getCheckedItemSum(): Int {
        val list = tagsList.filter { it.isChecked }
        return list.sumOf { it.id }
    }

    fun setCheckState(list: List<Int>) {
        tagsList.forEachIndexed { index, tagsItem ->
            val isChecked = list[index] == 1
            if (list[index] == 1) {
                tagsItem.isChecked = isChecked
                notifyItemChanged(index)
            }
        }
    }

    fun getTagsList(): List<TagsItem> = tagsList

    class TagsItemViewHolder(
        val binding: AdapterTagePickerItemBinding
    ) : ViewHolder(binding.root)
}