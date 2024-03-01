package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailAccessoriesBinding
import com.easyrent.abccarapp.abccar.ui.adapter.detail.DetailAccessoriesAdapter
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.FeatureEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel
import com.easyrent.abccarapp.abccar.viewmodel.detail.DetailAccessoriesViewModel

class DetailAccessoriesFragment : BaseFragment() {
    var _binding : FragmentDetailAccessoriesBinding? = null
    private val binding get() = _binding!!
    private val shareViewModel : CarDetailViewModel by activityViewModels()

    private val driveAdapter = DetailAccessoriesAdapter()
    private val insideAdapter = DetailAccessoriesAdapter()
    private val safetyAdapter = DetailAccessoriesAdapter()
    private val outsideAdapter = DetailAccessoriesAdapter()

    private val viewModel :DetailAccessoriesViewModel by viewModels{
        DetailAccessoriesViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailAccessoriesBinding.inflate(inflater, container, false)

        initView()
        initLiveData()

        return binding.root
    }

    private fun initView() {
        shareViewModel.getEditCarInfoResp()?.let { viewModel.initAccessoriesTable(it) }
    }
    private fun initLiveData() {
        shareViewModel.tableLiveData.observe(viewLifecycleOwner){status->
            when(status){
                //由於配件頁是navigation的初始頁,需要處理race condition
                is DetailApiStatus.InitCert->{
                    viewModel.initAccessoriesTable(status.resp)
                }
                else->{}
            }

        }
        viewModel.statusLiveData.observe(viewLifecycleOwner){status->
            when(status){
                is FeatureEditorStatus.InitEquippedTagsList->{
                    if(viewModel.driveList.isNotEmpty()){
                        binding.llDetailAccDrive.visibility = View.VISIBLE
                        val drivelayoutmanager = LinearLayoutManager(requireContext())
                        drivelayoutmanager.orientation = LinearLayoutManager.HORIZONTAL
                        driveAdapter.accessoriesList.addAll(viewModel.driveList)
                        binding.rvDetailDriveAccessories.adapter = driveAdapter
                        binding.rvDetailDriveAccessories.layoutManager = drivelayoutmanager
                    }
                    else{
                        binding.llDetailAccDrive.visibility = View.GONE
                    }

                    if(viewModel.insideList.isNotEmpty()){
                        binding.llDetailAccInside.visibility = View.VISIBLE
                        val insidelayoutmanager = LinearLayoutManager(requireContext())
                        insidelayoutmanager.orientation = LinearLayoutManager.HORIZONTAL
                        insideAdapter.accessoriesList.addAll(viewModel.insideList)
                        binding.rvDetailInsideAccessories.adapter = insideAdapter
                        binding.rvDetailInsideAccessories.layoutManager = insidelayoutmanager
                    }
                    else{
                        binding.llDetailAccInside.visibility = View.GONE
                    }

                    if(viewModel.safetyList.isNotEmpty()){
                        binding.llDetailAccSafety.visibility = View.VISIBLE
                        val safetylayoutmanager = LinearLayoutManager(requireContext())
                        safetylayoutmanager.orientation = LinearLayoutManager.HORIZONTAL
                        safetyAdapter.accessoriesList.addAll(viewModel.safetyList)
                        binding.rvDetailSafetyAccessories.adapter = safetyAdapter
                        binding.rvDetailSafetyAccessories.layoutManager = safetylayoutmanager
                    }
                    else{
                        binding.llDetailAccSafety.visibility = View.GONE
                    }

                    if(viewModel.outsideList.isNotEmpty()){
                        binding.llDetailAccOutside.visibility = View.VISIBLE
                        val outsidelayoutmanager = LinearLayoutManager(requireContext())
                        outsidelayoutmanager.orientation = LinearLayoutManager.HORIZONTAL
                        outsideAdapter.accessoriesList.addAll(viewModel.outsideList)
                        binding.rvDetailOutsideAccessories.adapter = outsideAdapter
                        binding.rvDetailOutsideAccessories.layoutManager = outsidelayoutmanager
                    }
                    else{
                        binding.llDetailAccOutside.visibility = View.GONE
                    }
                    shareViewModel.setAsseDone()
                }
                else->{

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}