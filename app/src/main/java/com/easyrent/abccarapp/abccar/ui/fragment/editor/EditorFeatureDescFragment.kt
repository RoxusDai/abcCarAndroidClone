package com.easyrent.abccarapp.abccar.ui.fragment.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.FragmentEditorFeatureDescBinding
import com.easyrent.abccarapp.abccar.manager.editor.feature.CertificateItem
import com.easyrent.abccarapp.abccar.manager.editor.feature.FeatureInfoTable
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarFeatureResp
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem
import com.easyrent.abccarapp.abccar.ui.adapter.EditorCertificateItemAdapter
import com.easyrent.abccarapp.abccar.ui.component.TagsItem
import com.easyrent.abccarapp.abccar.ui.dialog.EditorCloseConfirmDialog
import com.easyrent.abccarapp.abccar.ui.dialog.EditorListSelectorDialog
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.FeatureEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorActivityViewModel
import com.easyrent.abccarapp.abccar.viewmodel.editor.FeatureDescViewModel

/**
 *
 *  Step 4 - 車輛特色
 *
 */

class EditorFeatureDescFragment : BaseFragment() {

    private var _binding: FragmentEditorFeatureDescBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel: EditorActivityViewModel by activityViewModels()

    private val viewModel: FeatureDescViewModel by viewModels {
        FeatureDescViewModel.Factory
    }

    private lateinit var cerImagePicker: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cerPdfPicker: ActivityResultLauncher<Array<String>>

    private val cerAdapter = EditorCertificateItemAdapter().apply {
        setTypeSelectorEvent {
            viewModel.getCerTypeList(it)
        }

        setFileSelectorEvent { view ->
            showAddFileMenu(view)
        }

        setOnListEmptyEvent {
            binding.tvListEmptyDesc.visibility = View.GONE
        }
    }

    private fun showAddFileMenu(view: View) {
        PopupMenu(requireActivity(), view).apply {
            menuInflater.inflate(R.menu.editor_file_picker_menu, menu)
            setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.editor_picker_menu_image -> {
                        cerImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    R.id.editor_picker_menu_pdf -> {
                        cerPdfPicker.launch(arrayOf("application/pdf"))
                    }
                    R.id.editor_picker_menu_camera -> {
                        Toast.makeText(requireContext(), getString(R.string.implement_yet_desc), Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }.show()
    }

    private fun getToolbar() = binding.topToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePicker()
    }

    private fun initFilePicker() {
        cerImagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(uri)
                when(mimeType) {
                    "image/jpeg" -> {
                        viewModel.uploadCerFile(it, "jpg", requireContext())
                    }
                    "image/png" -> {
                        viewModel.uploadCerFile(it, "jpg", requireContext())
                    }
                    else -> Toast.makeText(requireContext(), "不支援 $mimeType 格式", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cerPdfPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                if (mimeType == "application/pdf") {
                    viewModel.uploadCerFile(it, "pdf", requireContext())
                } else {
                    Toast.makeText(requireContext(), "不支援 $mimeType 格式", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorFeatureDescBinding.inflate(inflater, container, false)

        initView()
        initLiveData()
        viewModel.initTagsList()
        return binding.root
    }

    private fun initTable() {

        val table = shareViewModel.getFeatureInfoTable()
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
        val table = shareViewModel.getFeatureInfoTable()

        binding.edFeatureDescInput.setText(table.featureTitle)
        binding.etTemplateInput.setText(table.templateContent)
        if (table.certificateList.isNotEmpty()) {
            binding.tvListEmptyDesc.visibility = View.GONE
        }
        cerAdapter.setItemList(table.certificateList)
    }

    private fun initTableFromApi() {
        shareViewModel.getEditCarInfoResp()?.carInfo?.let { carInfo ->
            binding.edFeatureDescInput.setText(carInfo.descriptionTitle)
            binding.etTemplateInput.setText(carInfo.description)

            val arrayList = arrayListOf<TagsConvertReqItem>()

            val featureSum = carInfo.feature
            val ensureSum = carInfo.equippedEnsure

            if (featureSum > 0) {
                val featureList = binding.featureDescSelector.getTagsList()
                arrayList.add(TagsConvertReqItem(Feature, featureSum, featureList.size))
            }

            if (ensureSum > 0) {
                val ensureList = binding.equippedEnsureSelector.getTagsList()
                arrayList.add(TagsConvertReqItem(Ensure, ensureSum, ensureList.size))
            }

            viewModel.getTagsConvertResult(arrayList)
            viewModel.getCarTypeListToInit()

        } ?: run {
            Toast.makeText(requireContext(), "編輯資料初始化異常", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {

        // 設定標題
        getToolbar().tvTitle.text = "Step 4 - 車輛特色"

        // 離開建立物件程序顯示提示Dialog
        getToolbar().ivCloseButton.setOnClickListener {
            EditorCloseConfirmDialog().show(childFragmentManager, "CloseConfirmDialog")
        }

        getToolbar().llBackButtonGroup.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.apply {

            rvCerList.apply {
                adapter = cerAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }

            btAddCerItem.setOnClickListener {
                binding.tvListEmptyDesc.visibility = View.GONE

                if (cerAdapter.getListSize() < 5) {
                    cerAdapter.addItemList()
                } else {
                    Toast.makeText(requireContext(), "認證書最多五個", Toast.LENGTH_SHORT).show()
                }

            }

            tvGetTemp.setOnClickListener {
                viewModel.getTemplateContent()
            }

            btNext.setOnClickListener {
                checkTable()
            }

            btRetry.setOnClickListener {
                viewModel.initTagsList()
                it.visibility = View.GONE
            }
        }

    }

    private fun checkTable() {
        val list = cerAdapter.getList()
            .filter { it.type.id == -1 || it.url.isBlank() }
        
        if (list.isEmpty()) {
            gotoNext()
        } else {
            Toast.makeText(requireActivity(), "請檢查證書類別並確實上傳檔案", Toast.LENGTH_SHORT).show()
        }
    }

    private fun gotoNext() {

        val table = FeatureInfoTable(
            isInit = true,
            featureTitle = binding.edFeatureDescInput.text.toString(),
            certificateList = cerAdapter.getList(),
            featureTagsSum = binding.featureDescSelector.getCheckedSum(),
            ensureTagsSum = binding.equippedEnsureSelector.getCheckedSum(),
            templateContent = binding.etTemplateInput.text.toString()
        )

        shareViewModel.setFeatureInfoTable(table)

        val mixpanel = initialMixpanel()
        mixpanel.track("step 4 passed")//step4 to 5

        findNavController().navigate(
            EditorFeatureDescFragmentDirections.actionEditorFeatureDescFragmentToEditorContactInfoFragment()
        )
    }

    private fun initLiveData() {
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->

            if (status != FeatureEditorStatus.Loading) {
                hideLoadingDialog()
            }

            when(status) {
                FeatureEditorStatus.Loading -> showLoadingDialog()

                FeatureEditorStatus.InitDone -> {
                    initTable()
                }

                is FeatureEditorStatus.InitEquippedTagsList -> {
                    initEquippedTags(status.resp)
                }

                is FeatureEditorStatus.InitFeatureTagsList -> {
                    initFeatureTags(status.resp)
                }

                is FeatureEditorStatus.TemplateContent -> {
                    val origin = binding.etTemplateInput.text.toString()
                    val temp = StringBuilder().apply {
                        if (origin.isBlank()) {
                            append(status.temp) // 如果原本沒內容
                        } else {
                            append(origin)
                            append("\n") // 有內容則換行
                            append(status.temp)
                        }
                    }.toString()

                    binding.etTemplateInput.setText(temp)
                }

                is FeatureEditorStatus.GetCerTypeList -> {
                    val index = status.index
                    val cerList = status.list
                    EditorListSelectorDialog(
                        title = "請選擇認證類型",
                        list = status.list.map { it.name }
                    ) {
                        val item = cerList[it]
                        cerAdapter.setItemCertificateType(index, item)
                    }.show(childFragmentManager, "EditorListSelectorDialog")
                }

                is FeatureEditorStatus.ErrorMessage -> {
                    binding.btRetry.visibility = View.VISIBLE // 顯示重試按鈕

                    checkErrorCode(
                        errorCode = status.errorCode,
                        message = status.message
                    )

                }

                is FeatureEditorStatus.GetUploadUrl -> {
                    cerAdapter.setItemUrl(status.url)
                }

                is FeatureEditorStatus.InitTagsStatus -> {
                    status.list.find { it.parseKey == Ensure }?.let { item ->
                        binding.equippedEnsureSelector.setCheckState(item.onOff)
                    }
                    status.list.find { it.parseKey == Feature}?.let { item ->
                        binding.featureDescSelector.setCheckState(item.onOff)
                    }
                }

                is FeatureEditorStatus.InitCerListType -> {
                    val typeList = status.list

                    shareViewModel.getEditCarInfoResp()?.carInfo?.let { carInfo ->
                        val cerList = carInfo.uploadFiles.filter {
                            it.carFileType in 4 .. 10 || it.carFileType == 1
                        }.map { file ->
                            CertificateItem(
                                url = file.url,
                                type = typeList.find { type ->
                                    file.carFileType == type.id
                                } ?: GetCertificateTypesItem(-1, "請選擇檔案")
                            )
                        }

                        if (cerList.isNotEmpty()) {
                            binding.tvListEmptyDesc.visibility = View.GONE
                        }

                        cerAdapter.setList(cerList)
                    }
                }
            }
        }
    }

    private fun initEquippedTags(resp: GetCarEquippedTagsResp) {
        binding.equippedEnsureSelector.apply {
            val itemList = resp.equippedEnsureList?.map { equipped ->
                TagsItem(id = equipped.id, name = equipped.name)
            } ?: mutableListOf()
            setList(itemList)
        }

        val ensureSum = shareViewModel.getFeatureInfoTable().ensureTagsSum

        if (ensureSum > 0) {
            val ensureList = binding.equippedEnsureSelector.getTagsList()

            viewModel.getTagsConvertResult(
                arrayListOf(
                    TagsConvertReqItem(Ensure, ensureSum, ensureList.size)
                )
            )
        }
    }

    private fun initFeatureTags(resp: GetCarFeatureResp) {
        binding.featureDescSelector.apply {
            val itemList = resp.map { tag ->
                TagsItem(id = tag.id, name = tag.name)
            }
            setList(itemList)
        }

        val featureSum = shareViewModel.getFeatureInfoTable().featureTagsSum

        if (featureSum > 0) {
            val featureList = binding.featureDescSelector.getTagsList()

            viewModel.getTagsConvertResult(
                arrayListOf(
                    TagsConvertReqItem(Feature, featureSum, featureList.size)
                )
            )
        }
    }


    companion object {
        private const val Ensure = "ensure"
        private const val Feature = "feature"
    }

}