package com.easyrent.abccarapp.abccar.ui.fragment.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.easyrent.abccarapp.abccar.databinding.FragmentEditorAccessoriesBinding
import com.easyrent.abccarapp.abccar.manager.editor.AccessoriesInfoTable
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem
import com.easyrent.abccarapp.abccar.ui.component.TagsItem
import com.easyrent.abccarapp.abccar.ui.dialog.EditorCloseConfirmDialog
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.AccessoriesEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.editor.AccessoriesInfoViewModel
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorActivityViewModel

/**
 *
 *  Step 2 - 車輛配備
 *
 */

class EditorAccessoriesFragment : BaseFragment() {

    private var _binding: FragmentEditorAccessoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccessoriesInfoViewModel by viewModels {
        AccessoriesInfoViewModel.Factory
    }

    private val shareViewModel : EditorActivityViewModel by activityViewModels()

    private fun getToolbar() = binding.topToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorAccessoriesBinding.inflate(inflater, container, false)

        initView()
        initLiveData()

        return binding.root
    }

    private fun initLiveData() {
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            // 如果不是Loading，隱藏LoadingDialog
            if (status !is AccessoriesEditorStatus.Loading) {
                hideLoadingDialog()
            }

            when(status) {
                AccessoriesEditorStatus.Loading -> {
                    showLoadingDialog()
                }
                is AccessoriesEditorStatus.InitTagsList -> {
                    initTagsSelector(status.resp)
                    initTagsStatus()
                }
                is AccessoriesEditorStatus.InitTagsCheckStatus -> {
                    setTagsStatus(status.resp)
                }

                is AccessoriesEditorStatus.ErrorMessage -> {
                    checkErrorCode(status.errorCode, status.message)
                }
            }
        }
    }

    private fun setTagsStatus(resp: List<TagsConvertRespItem>) {
        resp.find { it.parseKey == Driver }?.let { item ->
            binding.equippedDriveSelector.setCheckState(item.onOff)
        }
        resp.find { it.parseKey == Inside }?.let { item ->
            binding.equippedInsideSelector.setCheckState(item.onOff)
        }
        resp.find { it.parseKey == Outside }?.let { item ->
            binding.equippedOutsideSelector.setCheckState(item.onOff)
        }
        resp.find { it.parseKey == Safety }?.let { item ->
            binding.equippedSafetySelector.setCheckState(item.onOff)
        }
    }

    private fun initTagsStatus() {
        val table = shareViewModel.getAccessoriesInfoTable()

        if (shareViewModel.getCarId() > 0) {
            if (table.isInit) {
                initTableFromLocal()
            } else {
                initTableFromApi()
            }
        } else {
            initTableFromLocal()
        }
    }

    private fun initTableFromLocal() {
        val info = shareViewModel.getAccessoriesInfoTable()
        val driver = binding.equippedDriveSelector.getTagsList()
        val inside = binding.equippedInsideSelector.getTagsList()
        val safety = binding.equippedSafetySelector.getTagsList()
        val outside = binding.equippedOutsideSelector.getTagsList()
        val req = arrayListOf(
            (TagsConvertReqItem(ParseKey = Driver, DecimalNumber = info.equippedDrive, Qty = driver.size)),
            (TagsConvertReqItem(ParseKey = Inside, DecimalNumber = info.equippedInside, Qty = inside.size)),
            (TagsConvertReqItem(ParseKey = Safety, DecimalNumber = info.equippedSafety, Qty = safety.size)),
            (TagsConvertReqItem(ParseKey = Outside, DecimalNumber = info.equippedOutside, Qty = outside.size)),
        )

        // 初始化特殊配備欄位字串
        binding.etSpacialAccessories.setText(info.spacial)

        viewModel.getTagsConvertResult(req)
    }

    private fun initTableFromApi() {
        shareViewModel.getEditCarInfoResp()?.carInfo?.let { info ->
            val driver = binding.equippedDriveSelector.getTagsList()
            val inside = binding.equippedInsideSelector.getTagsList()
            val safety = binding.equippedSafetySelector.getTagsList()
            val outside = binding.equippedOutsideSelector.getTagsList()
            val req = arrayListOf(
                (TagsConvertReqItem(ParseKey = Driver, DecimalNumber = info.equippedDrive, Qty = driver.size)),
                (TagsConvertReqItem(ParseKey = Inside, DecimalNumber = info.equippedInside, Qty = inside.size)),
                (TagsConvertReqItem(ParseKey = Safety, DecimalNumber = info.equippedSafety, Qty = safety.size)),
                (TagsConvertReqItem(ParseKey = Outside, DecimalNumber = info.equippedOutside, Qty = outside.size)),
            )

            // 初始化特殊配備欄位字串
            binding.etSpacialAccessories.setText(info.equippedFeature)

            viewModel.getTagsConvertResult(req)
        } ?: run {
            Toast.makeText(requireContext(), "編輯資料初始化異常", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initTagsSelector(resp: GetCarEquippedTagsResp) {
        binding.equippedDriveSelector.apply {
            val itemList = resp.equippedDriveList?.map { equipped ->
                TagsItem(id = equipped.id, name = equipped.name)
            } ?: mutableListOf()
            setList(itemList)
        }

        binding.equippedInsideSelector.apply {
            val itemList = resp.equippedInsideList?.map { equipped ->
                TagsItem(id = equipped.id, name = equipped.name)
            } ?: mutableListOf()
            setList(itemList)
        }

        binding.equippedSafetySelector.apply {
            val itemList = resp.equippedSafetyList?.map { equipped ->
                TagsItem(id = equipped.id, name = equipped.name)
            } ?: mutableListOf()
            setList(itemList)
        }

        binding.equippedOutsideSelector.apply {
            val itemList = resp.equippedOutsideList?.map { equipped ->
                TagsItem(id = equipped.id, name = equipped.name)
            } ?: mutableListOf()
            setList(itemList)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        viewModel.initTagsList()
    }

    private fun initView() {

        // 設定標題
        getToolbar().tvTitle.text = "Step 2- 車輛配件"

        // 離開建立物件程序顯示提示Dialog
        getToolbar().ivCloseButton.setOnClickListener {
            EditorCloseConfirmDialog().show(childFragmentManager, "CloseConfirmDialog")
        }

        getToolbar().llBackButtonGroup.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.btNext.setOnClickListener {

            val driverSum = binding.equippedDriveSelector.getCheckedSum()
            val insideSum = binding.equippedInsideSelector.getCheckedSum()
            val safetySum = binding.equippedSafetySelector.getCheckedSum()
            val outsideSum = binding.equippedOutsideSelector.getCheckedSum()
            val spacial = binding.etSpacialAccessories.text.toString()

            val table = AccessoriesInfoTable(
                isInit = true,
                equippedDrive = driverSum,
                equippedInside = insideSum,
                equippedSafety = safetySum,
                equippedOutside = outsideSum,
                spacial = spacial
            )

            shareViewModel.setAccessoriesInfoTable(table)

            val mixpanel = initialMixpanel()
            mixpanel.track("step 2 passed")//step2 to 3
            findNavController().navigate(
                EditorAccessoriesFragmentDirections.actionEditorAccessoriesFragmentToEditorPhotoFragment()
            )
        }

    }

    companion object {
        private const val Driver = "driver"
        private const val Outside = "outside"
        private const val Inside = "inside"
        private const val Safety = "safety"
    }

}