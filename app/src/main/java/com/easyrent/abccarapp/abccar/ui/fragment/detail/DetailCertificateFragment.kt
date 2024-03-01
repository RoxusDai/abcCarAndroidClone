package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailCertificateBinding
import com.easyrent.abccarapp.abccar.manager.editor.feature.CertificateItem
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem
import com.easyrent.abccarapp.abccar.ui.adapter.detail.DetailCertificateAdapter
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.FeatureEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel
import com.easyrent.abccarapp.abccar.viewmodel.detail.DetailCertificateViewModel

class DetailCertificateFragment : BaseFragment() {
    var _binding: FragmentDetailCertificateBinding? = null
    private val binding get() = _binding!!


    private val shareViewModel: CarDetailViewModel by activityViewModels()
    private val viewModel: DetailCertificateViewModel by viewModels {
        DetailCertificateViewModel.Factory
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailCertificateBinding.inflate(inflater, container, false)

        initLiveData()
        initView()
        return binding.root
    }

    private fun initView() {

    }

    private fun initLiveData() {
        shareViewModel.tableLiveData.observe(viewLifecycleOwner) { status ->
            when (status) {
                is DetailApiStatus.InitSpec -> {
                    viewModel.setCertificationList()
                }

                else -> {}
            }
        }
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            when (status) {
                is FeatureEditorStatus.InitCerListType -> {
                    val certlist = mutableListOf<CertificateItem>()
                    val certfile = shareViewModel.getFeatureInfoTable().certificateList
                    if(certfile.isNotEmpty()){
                        certfile.forEach {
                            val itemid = it.type.id
                            ///先移除行照
                            if(it.type.id !=3){
                                val name = status.list.find { it.id == itemid }?.name ?: "認證書"
                                certlist.add(
                                    CertificateItem(
                                        it.url,
                                        GetCertificateTypesItem(itemid, name)
                                    )
                                )
                            }
                        }
                        if(certlist.isEmpty()){
                            binding.tvDetailCertificate.visibility = View.VISIBLE
                        }
                        else{
                            val adapter = DetailCertificateAdapter(
                                certlist
                            ) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                startActivity(intent)
                            }
                            binding.rvDetailCertificate.adapter = adapter
                            binding.rvDetailCertificate.layoutManager = GridLayoutManager(requireContext(),1)
                            binding.tvDetailCertificate.visibility = View.GONE
                        }


                    }
                    else{
                        binding.tvDetailCertificate.visibility = View.VISIBLE
                    }
                    shareViewModel.setCertDone()
                }

                else -> {}
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}