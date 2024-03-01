package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.easyrent.abccarapp.abccar.databinding.FragmentDetailMediaBinding
import com.easyrent.abccarapp.abccar.manager.editor.feature.CertificateItem
import com.easyrent.abccarapp.abccar.manager.editor.feature.FeatureInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp

import com.easyrent.abccarapp.abccar.ui.adapter.detail.DetailMediaAdapter
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.PhotoEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel
import com.easyrent.abccarapp.abccar.viewmodel.detail.DetailMediaViewModel

class DetailMediaFragment : BaseFragment() {
    var _binding : FragmentDetailMediaBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel : CarDetailViewModel by activityViewModels()
    private val viewModel :DetailMediaViewModel by viewModels{
        DetailMediaViewModel.Factory
    }
    private val mediaAdapter :DetailMediaAdapter = DetailMediaAdapter{
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        startActivity(intent)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailMediaBinding.inflate(inflater, container, false)

        initView()
        initLiveData()


        return binding.root
    }
    private fun initLiveData() {
        shareViewModel.tableLiveData.observe(viewLifecycleOwner){status->
            when(status){
                is DetailApiStatus.InitCarInfo->{
                    initTable(status.resp)
                }
                else->{}
            }

        }
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            when(status){
                is PhotoEditorStatus.UpdateTable->{
                    binding.rvDetailMedia.adapter = mediaAdapter
                    mediaAdapter.setImageList(viewModel.getList())
                    binding.rvDetailMedia.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
                    shareViewModel.setMediaDone()
                }
                else->{}
            }
        }
    }

    private fun initView() {

    }

    private fun initTable(resp: GetEditCarInfoResp) {
        if (resp.carInfo.id > 0) {
            initTableFromApi(resp)
        }
    }


    private fun initTableFromApi(resp: GetEditCarInfoResp) {
        resp.carInfo?.let { info ->
            ///設定圖片url list
            val list = info.uploadFiles.filter { it.carFileType == 0 }.map {
                PhotoItem(
                    url = it.url,
                    false
                )

            }
            //設定影片Url
            val videoUrl = info.uploadFiles.find { it.carFileType == 11 }?.url ?: ""
            viewModel.setPhotoTable(
                PhotoInfoTable(
                    isInit = true,
                    imageUrlList = list,
                    videoUrl = videoUrl
                )
            )
            //需要傳遞video Url
            shareViewModel.videoUrl = videoUrl

            //設定認證Url
            val certUrl = info.uploadFiles.filter{it.carFileType >0 && it.carFileType != 11 && it.url.isNotEmpty()}.map{
                    CertificateItem(
                        url = it.url,
                        type = GetCertificateTypesItem(it.carFileType, "認證檔")
                    )
            }
            shareViewModel.setFeatureInfoTable(
                FeatureInfoTable(
                    isInit = true,
                    featureTitle = info.descriptionTitle ?: "",
                    certificateList = certUrl,
                    featureTagsSum = info.feature,
                    templateContent = info.description ?: ""
                )
            )
        } ?: run {
            Toast.makeText(requireContext(), "編輯資料初始化異常", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}