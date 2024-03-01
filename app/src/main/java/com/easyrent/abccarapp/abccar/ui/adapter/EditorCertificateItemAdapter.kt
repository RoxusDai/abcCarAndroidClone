package com.easyrent.abccarapp.abccar.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.databinding.AdapterCertificateItemLayoutBinding
import com.easyrent.abccarapp.abccar.manager.editor.feature.CertificateItem
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem

class EditorCertificateItemAdapter: Adapter<EditorCertificateItemAdapter.CertificateItemViewHolder>() {

    private var itemList = mutableListOf<CertificateItem>()

    // 因為外界 FilePicker result回應架構的問題，無法將對應的 Index 傳回來
    // 因此使用 editIndex 來紀錄做動應急，請小心使用
    // 如果發現 URL 填入位置錯誤，可以先檢查這邊。
    private var editIndex = -1

    private var onCerTypeSelectorClicked: ((Int) -> Unit)? = null
    private var onFileSelectorClicked: ((View) -> Unit)? = null
    private var onListEmptyEvent: (() -> Unit)? = null

    fun getListSize() = itemList.size

    fun getList(): List<CertificateItem> {
        return itemList
    }

    fun setList(list: List<CertificateItem>) {
        itemList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun setTypeSelectorEvent(event: (Int) -> Unit) {
        onCerTypeSelectorClicked = event
    }

    fun setFileSelectorEvent(event: (View) -> Unit) {
        onFileSelectorClicked = event
    }

    fun setOnListEmptyEvent(event: () -> Unit) {
        onListEmptyEvent = event
    }

    fun setItemCertificateType(index: Int, item: GetCertificateTypesItem) {
        if (itemList.lastIndex < index) return
        itemList[index].type = item

        notifyItemChanged(index)
    }

    fun setItemUrl(url: String) {

        if (editIndex == -1) return

        if (itemList.lastIndex < editIndex) {
            editIndex = -1 // 錯誤就復原
            return
        }
        itemList[editIndex].url = url

        notifyItemChanged(editIndex)

        editIndex = -1 // 記得還原
    }

    fun addItemList() {
        itemList.add(CertificateItem())
        notifyItemInserted(itemList.lastIndex)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterCertificateItemLayoutBinding.inflate(layoutInflater, parent, false)
        return CertificateItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CertificateItemViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = itemList[position]
        val type = item.type

        holder.apply {
            binding.tvCerName.text = if (type.id > 0) {
                type.name
            } else {
                "請選擇認證書"
            }

            binding.tvFileName.text = if (itemList[position].url.isNotBlank()) {
                item.url
            } else {
                "請選擇檔案"
            }

            binding.tvAddFile.setOnClickListener {
                // 編輯項目 Index 紀錄
                editIndex = position
                onFileSelectorClicked?.invoke(it)
            }

            binding.tvCerName.setOnClickListener {
                onCerTypeSelectorClicked?.invoke(position)
            }

            binding.tvDeleteFile.setOnClickListener {
                itemList.remove(item)
                notifyDataSetChanged()
            }

            if (item.url.isBlank()) {
                binding.ivFileImage.visibility = View.GONE
                binding.tvFileName.visibility = View.VISIBLE

                binding.tvFileName.text = "請選擇檔案"
            } else {
                val extension = item.url.substringAfterLast(".")
                if (extension == "jpg" || extension == "png" ) {
                    Glide.with(itemView)
                        .load(item.url)
                        .into(binding.ivFileImage)
                    binding.ivFileImage.visibility = View.VISIBLE
                    binding.tvFileName.visibility = View.GONE
                } else {
                    binding.ivFileImage.visibility = View.GONE
                    binding.tvFileName.visibility = View.VISIBLE

                    binding.tvFileName.text = item.url
                }

            }
        }
    }

    fun setItemList(certificateList: List<CertificateItem>) {
        itemList = certificateList.toMutableList()
        notifyDataSetChanged()
    }


    class CertificateItemViewHolder(
        val binding: AdapterCertificateItemLayoutBinding
    ) : ViewHolder(binding.root)

}