package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailContactBinding
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListRespItem
import com.easyrent.abccarapp.abccar.ui.adapter.detail.DetailContactAdapter
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.ContactEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel
import com.easyrent.abccarapp.abccar.viewmodel.detail.DetailContactViewModel

class DetailContactFragment : BaseFragment() {
    var _binding : FragmentDetailContactBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel : CarDetailViewModel by activityViewModels()
    private val viewModel : DetailContactViewModel by viewModels {
        DetailContactViewModel.Factory
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailContactBinding.inflate(inflater, container, false)

        initLiveData()
        initView()
        return binding.root
    }

    private fun initView() {
        ///取得聯絡人ID
        viewModel.initContactList(shareViewModel.getEditCarInfoResp()?.carInfo?.memberID ?: 0)
    }

    private fun initLiveData() {
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            when (status) {
                is ContactEditorStatus.DetailContactList -> {
                    val contactlist = mutableListOf<GetContactPersonListRespItem>()
                    val totallist = status.list[0]
                    shareViewModel.getEditCarInfoResp()?.carInfo?.memberContactIDs?.forEach {totalid->
                        totallist.find{it.id == totalid}?.let { contactlist.add(it) }
                    }

                    val adapter = DetailContactAdapter(contactlist,requireContext())
                    binding.lvDetailContact.adapter = adapter
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