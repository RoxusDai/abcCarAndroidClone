package com.easyrent.abccarapp.abccar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.databinding.AdapterEditorPhotoGridLayoutBinding
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem

class EditorPhotoAdapter(
    private val maxSize: Int
) : RecyclerView.Adapter<EditorPhotoAdapter.PhotoViewHolder>(), ItemTouchHelperAdapter {
    private val imageUrlList: MutableList<PhotoItem> = mutableListOf()

    private var onEmptyEvent: ((Boolean) -> Unit)? = null

    private var onDeleteEvent: ((PhotoItem) -> Unit)? = null

    fun setOnEmptyEvent(event: (Boolean) -> Unit) {
        onEmptyEvent = event
    }

    fun setOnDeleteEvent(event: (PhotoItem) -> Unit) {
        onDeleteEvent = event
    }

    fun getLeftSize() = maxSize - imageUrlList.size

    fun getList(): List<PhotoItem> = imageUrlList

    fun updateList(list: List<PhotoItem>) {
        imageUrlList.clear()
        imageUrlList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterEditorPhotoGridLayoutBinding.inflate(layoutInflater, parent, false)

        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.binding.apply {
            // init Photo
            val url = imageUrlList[position].url
            Glide.with(this.root)
                .load(url)
                .into(ivPhoto)

            cbIsCover.isChecked = imageUrlList[position].isCover

            cbIsCover.setOnClickListener {
                if(cbIsCover.isChecked){
                    imageUrlList.find { it.isCover }?.isCover = false
                    imageUrlList.find{it.url == url}?.isCover = true
                }
                else{
                    imageUrlList.find { it.isCover }?.isCover = false
                }
                notifyDataSetChanged()
            }

            ivDelete.setOnClickListener {
                onDeleteEvent?.invoke(imageUrlList[position])
                onEmptyEvent?.invoke(imageUrlList.isEmpty())
                notifyDataSetChanged()
            }
        }
    }


    override fun onItemMove(from: Int, to: Int) {
        //imageUrlList.swap(items, from, to)
        val data = imageUrlList.removeAt(from)
        imageUrlList.add(to , data)
    }

    override fun onItemDismiss(position: Int) {
        imageUrlList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getTouchList(): MutableList<PhotoItem>  = imageUrlList

    class PhotoViewHolder(
        val binding: AdapterEditorPhotoGridLayoutBinding
    ) : ViewHolder(binding.root)
}

///make adapter draggable
class PhotoItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        0
    ) {
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        //更新list
        adapter.onItemMove(fromPosition,toPosition)

        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {}
}

interface ItemTouchHelperAdapter {
    fun onItemMove(from: Int, to: Int)
    fun onItemDismiss(position: Int)
    fun getTouchList(): MutableList<PhotoItem>
}
