package com.easyrent.abccarapp.abccar.ui.adapter.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.AdapterDetailCertificateBinding
import com.easyrent.abccarapp.abccar.manager.editor.feature.CertificateItem

class DetailCertificateAdapter(
    val certificateList: MutableList<CertificateItem>,
    val onImageViewClicked: (String) -> Unit
) : RecyclerView.Adapter<DetailCertificateAdapter.CertificateItemViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailCertificateAdapter.CertificateItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterDetailCertificateBinding.inflate(layoutInflater, parent, false)

        return DetailCertificateAdapter.CertificateItemViewHolder(binding)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(
        holder: DetailCertificateAdapter.CertificateItemViewHolder,
        position: Int
    ) {
        val data = certificateList[position]
        if (data.url.isNotEmpty()) {
            //認證書類型
            when (data.type.id) {
                1 -> {
                    ///其他認證
                    holder.binding.tvDetailCertificate.text = data.type.name
                }
                3->{
                    holder.binding.tvDetailCertificate.text = "行照"
                }
                4 -> {
                    holder.binding.tvDetailCertificate.text = "HAA鑑定書"
                }

                5 -> {
                    holder.binding.tvDetailCertificate.text = "GOO鑑定書"
                }

                6 -> {
                    holder.binding.tvDetailCertificate.text = "德國萊茵"
                }

                7 -> {
                    holder.binding.tvDetailCertificate.text = "HOT認證書"
                }

                8 -> {
                    holder.binding.tvDetailCertificate.text = "SAA鑑定書"
                }

                9 -> {
                    holder.binding.tvDetailCertificate.text = "SAVE認證書"
                }

                10 -> {
                    holder.binding.tvDetailCertificate.text = "YES認證書"
                }
            }

            //URL
            val datatype = data.url.substring(data.url.length - 4)
            if (datatype.contains("pdf")) {
                holder.binding.ivDetailCertificate.setImageResource(R.drawable.pdf_preview)
            } else Glide.with(holder.binding.root).load(data.url)
                .into(holder.binding.ivDetailCertificate)

            //點擊查看
            holder.binding.ivDetailCertificate.setOnClickListener {
                onImageViewClicked(data.url)
            }
        }
        else{
            holder.itemView.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return certificateList.size
    }

    fun setCertificateList(list: List<CertificateItem>) {
        if (certificateList.isNotEmpty()) certificateList.clear()
        certificateList.addAll(list)
    }

    class CertificateItemViewHolder(
        val binding: AdapterDetailCertificateBinding
    ) : RecyclerView.ViewHolder(binding.root)

}