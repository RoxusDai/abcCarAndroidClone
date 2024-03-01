package com.easyrent.abccarapp.abccar.ui.fragment.detail


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.activityViewModels
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailVideoBinding
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel

class DetailVideoFragment : BaseFragment() {
    var _binding: FragmentDetailVideoBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel: CarDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailVideoBinding.inflate(inflater, container, false)

        initLiveData()
        return binding.root
    }

    fun initLiveData() {
        shareViewModel.tableLiveData.observe(viewLifecycleOwner) { status ->
            when (status) {
                is DetailApiStatus.InitCert -> {
                    val videoUrl = shareViewModel.videoUrl
                    if(videoUrl.isNotEmpty()){
//                        binding.vvDetail.setVideoURI(Uri.parse(videoUrl))
////                        val mediaController = MediaController(requireContext())
////                        mediaController.setMediaPlayer(binding.vvDetail)
////                        binding.vvDetail.setMediaController(mediaController)
//                        binding.vvDetail.setOnClickListener{it as VideoView
//                            if(it.isActivated){
//                                it.stopPlayback()
//                            }
//                            else{binding.vvDetail.start()}
//                        }
                        binding.tvDetailVideo.text = "點擊播放"
                        binding.tvDetailVideo.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                            startActivity(intent)
                        }
                    }
                    else{
//                        binding.vvDetail.visibility = View.GONE
                        binding.tvDetailVideo.text = "尚未新增影片"
                    }
                }

                else -> {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}