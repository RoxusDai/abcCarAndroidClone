package com.easyrent.abccarapp.abccar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.AdapterContactListItemBinding
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListRespItem

class ContactListAdapter(
    private val onFullSelectedEvent: () -> Unit
) : RecyclerView.Adapter<ContactListAdapter.ContactItemViewHolder>() {

    private var contractList = mutableListOf<GetContactPersonListRespItem>()

    private var selectedCount = 0

    fun setList(list: List<GetContactPersonListRespItem>) {
        contractList = list.toMutableList()
        // 設定已選擇數量
        selectedCount = contractList.filter { it.isChecked }.size
        notifyDataSetChanged()
    }

    fun getList() = contractList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterContactListItemBinding.inflate(layoutInflater, parent, false)
        return ContactItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contractList.size
    }

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        val context = holder.itemView.context
        val info = contractList[position]

        holder.binding.apply {
            tvName.text = info.name

            tvPhoneNumber.text = String.format(
                ContextCompat.getString(context, R.string.contact_phone_number),
                info.cellPhone,
                info.telephone
            )

            tvLineId.text = String.format(
                ContextCompat.getString(context, R.string.contact_info_line_id),
                info.lineID
            )

            tvLineUrl.text = String.format(
                ContextCompat.getString(context, R.string.contact_info_line_url),
                info.lineUrl
            )

            tvAddress.text = info.address

            // 初始化
            checkBox.isChecked = info.isChecked

            checkBox.setOnClickListener {
                // 如果以選擇三個且是勾選狀態，則取消勾選並返回
                if (selectedCount >= 3 && !contractList[position].isChecked) {
                    onFullSelectedEvent.invoke()
                    checkBox.isChecked = false
                    return@setOnClickListener
                } else {
                    contractList[position].isChecked = !contractList[position].isChecked
                }
                if (contractList[position].isChecked) selectedCount ++ else selectedCount --
            }
        }
    }


    class ContactItemViewHolder(
        val binding: AdapterContactListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)
}