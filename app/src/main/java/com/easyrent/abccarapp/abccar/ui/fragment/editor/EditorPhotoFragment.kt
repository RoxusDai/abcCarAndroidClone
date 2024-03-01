package com.easyrent.abccarapp.abccar.ui.fragment.editor

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.easyrent.abccarapp.abccar.databinding.FragmentEditorPhotoBinding
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.ui.adapter.EditorPhotoAdapter
import com.easyrent.abccarapp.abccar.ui.adapter.PhotoItemTouchHelperCallback
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem
import com.easyrent.abccarapp.abccar.ui.dialog.EditorCloseConfirmDialog
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.PhotoEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorActivityViewModel
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorPhotoViewModel

/**
 *
 *  Step 3 - 車輛照片
 *
 *  照片至少6張最多20張，封面照為陣列第一張圖片
 *
 *  點選v設定為封面，點選x刪除照片
 *
 *  上傳車輛影片為MP4或MOV檔案
 *
 */
class EditorPhotoFragment : BaseFragment() {

    private var _binding: FragmentEditorPhotoBinding? = null
    private val binding get() = _binding!!

    private fun getToolbar() = binding.topToolbar

    private val photoAdapter = EditorPhotoAdapter(20).apply {
        setOnEmptyEvent {
            setEmptyDesc(it)
        }
        setOnDeleteEvent {
            viewModel.deleteItem(it)
        }
    }

    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var videoPicker: ActivityResultLauncher<PickVisualMediaRequest>

    private val shareViewModel: EditorActivityViewModel by activityViewModels()

    private val viewModel: EditorPhotoViewModel by viewModels {
        EditorPhotoViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePickerRegister()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorPhotoBinding.inflate(inflater, container, false)

        initView()
        initLiveData()
        initTable()

        return binding.root
    }

    private fun initTable() {
        val table = shareViewModel.getPhotoInfoTable()
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
        val table = shareViewModel.getPhotoInfoTable()
        viewModel.setPhotoTable(table) // 復原狀態
    }

    private fun initTableFromApi() {
        shareViewModel.getEditCarInfoResp()?.carInfo?.let { info ->

            val list = info.uploadFiles.filter { it.carFileType == 0 }.map {
                PhotoItem(
                    url = it.url,
                    false
                )
            }

            val videoUrl = info.uploadFiles.find { it.carFileType == 11 }?.url ?: ""

            viewModel.setPhotoTable(
                PhotoInfoTable(
                    isInit = true,
                    imageUrlList = list,
                    videoUrl = videoUrl
                )
            )

        } ?: run {
            Toast.makeText(requireContext(), "編輯資料初始化異常", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initLiveData() {
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            if (status !is PhotoEditorStatus.Loading) hideLoadingDialog()

            when(status) {
                PhotoEditorStatus.Loading -> showLoadingDialog()
                is PhotoEditorStatus.AddImageList -> {
                    setEmptyDesc(status.urls.isEmpty())
                    photoAdapter.updateList(status.urls)
                }

                is PhotoEditorStatus.UpdateImageList -> {
                    setEmptyDesc(status.urls.isEmpty())
                    photoAdapter.updateList(status.urls)
                }

                is PhotoEditorStatus.Error -> {
                    Toast.makeText(
                        requireActivity(),
                        status.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is PhotoEditorStatus.UpdateTable -> {
                    setEmptyDesc(status.table.imageUrlList.isEmpty())
                    photoAdapter.updateList(status.table.imageUrlList)
                    initRadioButton(status.table)
                }

                is PhotoEditorStatus.UpdateVideoDone -> {
                    binding.tvUploadVideoDesc.text = status.url
                }

                else -> {}
            }
        }
    }

    private fun initRadioButton(table: PhotoInfoTable) {
        if (table.videoUrl.contains("www.youtube.com")) {
            binding.rbYoutubeUrl.isChecked = true
            binding.rbUploadVideo.isChecked = false

            binding.rbYoutubeUrl.performClick()

            binding.etUrlInput.setText(table.videoUrl)
        } else {
            binding.rbYoutubeUrl.isChecked = false
            binding.rbUploadVideo.isChecked = true

            binding.rbUploadVideo.performClick()

            binding.tvUploadVideoDesc.text = table.videoUrl
        }
    }

    private fun setEmptyDesc(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvImageEmptyDesc.visibility = View.VISIBLE
            binding.rvCarImages.visibility = View.GONE
        } else {
            binding.tvImageEmptyDesc.visibility = View.GONE
            binding.rvCarImages.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {

        // 設定標題
        getToolbar().tvTitle.text = "Step 3 - 車輛照片"

        // 離開建立物件程序顯示提示Dialog
        getToolbar().ivCloseButton.setOnClickListener {
            EditorCloseConfirmDialog().show(childFragmentManager, "CloseConfirmDialog")
        }

        getToolbar().llBackButtonGroup.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.rvCarImages.apply {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            val callback = PhotoItemTouchHelperCallback(photoAdapter)
            val touchHelper = ItemTouchHelper(callback)
            try{
                photoAdapter.setHasStableIds(true)
            }
            catch (e:Exception){}
            adapter = photoAdapter
            touchHelper.attachToRecyclerView(this)
        }

        binding.rbUploadVideo.setOnClickListener {
            binding.rbYoutubeUrl.isChecked = false
            binding.etUrlInput.visibility = View.GONE
            binding.llPickupVideoGroup.visibility = View.VISIBLE
        }

        binding.rbYoutubeUrl.setOnClickListener {
            binding.rbUploadVideo.isChecked = false
            binding.etUrlInput.visibility = View.VISIBLE
            binding.llPickupVideoGroup.visibility = View.GONE
        }

        binding.btPickupImage.setOnClickListener {
            if (photoAdapter.getLeftSize() > 0) {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        binding.btPickupVideo.setOnClickListener {
            videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

        binding.btNext.setOnClickListener {
            checkTable()
        }
    }

    private fun checkTable() {
        val imageList = photoAdapter.getList()

        if (imageList.size < 6 || imageList.size > 20) {
            binding.tvImageListSizeErrorDesc.visibility = View.VISIBLE
        } else {

            val url = when {
                binding.rbUploadVideo.isChecked -> viewModel.getTable().videoUrl
                binding.rbYoutubeUrl.isChecked -> binding.etUrlInput.text.toString()
                else -> ""
            }

            val list = photoAdapter.getList().toMutableList()
            val coverpic = list.find{it.isCover}

            if(coverpic !=null){
                coverpic.isCover = false
                list.remove(coverpic)
                list.add(0,coverpic)
            }

            val table = PhotoInfoTable(
                isInit = true,
                videoUrl = url,
                imageUrlList = list
            )

            viewModel.setPhotoTable(table)
            shareViewModel.setPhotoInfoTable(table)

            val mixpanel = initialMixpanel()
            mixpanel.track("step 3 passed")//step3 to 4
            findNavController().navigate(
                EditorPhotoFragmentDirections.actionEditorPhotoFragmentToEditorFeatureDescFragment()
            )
        }
    }

    // ImagePicker
    private fun getImagePickerRegister(
        onCallback: (List<Uri>) -> Unit
    ): ActivityResultLauncher<PickVisualMediaRequest> {
        val leftImageCount = photoAdapter.getLeftSize()
        return registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(leftImageCount)) { uris ->
            onCallback(uris)
        }
    }

    private fun getVideoPickerRegister() : ActivityResultLauncher<PickVisualMediaRequest> {
        return registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                if (mimeType == "video/mp4") {
                    viewModel.uploadVideo(it, "mp4", requireActivity())
                } else {
                    Toast.makeText(requireContext(), "不支援 $mimeType 格式", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initFilePickerRegister() {
        imagePicker = getImagePickerRegister {  uris ->

            if (uris.size > photoAdapter.getLeftSize()) {
                Toast.makeText(requireContext(), "相片數量超過上限，請挑選${photoAdapter.getLeftSize()}張以內的相片", Toast.LENGTH_SHORT).show()
                return@getImagePickerRegister
            }

            if (uris.isNotEmpty()) {
                viewModel.uploadImages(uris, requireContext()) { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        videoPicker = getVideoPickerRegister()
    }



}