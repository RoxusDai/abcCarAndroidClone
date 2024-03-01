package com.easyrent.abccarapp.abccar.ui.adapter.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.AdapterDetailContactListItemBinding
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListRespItem

class DetailContactAdapter(
    private val contactList :List<GetContactPersonListRespItem>,
    private val context : Context
): BaseAdapter() {
    override fun getCount(): Int = contactList.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long =0


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val data = contactList[position]

        val inflater = LayoutInflater.from(context)
        val binding : AdapterDetailContactListItemBinding = AdapterDetailContactListItemBinding.inflate(inflater,parent,false)
        val contactview = binding.root

        binding.tvName.text = "聯絡人姓名: "+ data.name
        binding.tvAddress.text = "地址: " + data.address
        binding.tvLineId.text = "line: " + data.lineID
        binding.tvLineUrl.text = "line連結: " +data.lineUrl
        binding.tvPhoneNumber.text = "連絡電話: " + data.cellPhone

        return contactview
    }
}