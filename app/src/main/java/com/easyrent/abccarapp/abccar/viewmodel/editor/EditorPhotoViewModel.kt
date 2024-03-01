package com.easyrent.abccarapp.abccar.viewmodel.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.editor.photo.EditorPhotoManager
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.repository.editor.photo.EditorPhotoRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.tools.FilesTool
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.PhotoEditorStatus
import kotlinx.coroutines.launch
import java.io.File

class EditorPhotoViewModel(
    private val repository: EditorPhotoRepository
) : ViewModel() {

    private val filesTool = FilesTool()

    private val tableManager = EditorPhotoManager()

    private val _statusLiveData: MutableLiveData<PhotoEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<PhotoEditorStatus> get() = _statusLiveData

    fun setPhotoTable(table: PhotoInfoTable) {
        tableManager.setTable(table)
        _statusLiveData.value = PhotoEditorStatus.UpdateTable(tableManager.getTable())
    }

    fun getTable() = tableManager.getTable()

    fun getList(): List<PhotoItem> = tableManager.getImageUrlList()

    fun deleteItem(photoItem: PhotoItem) {
        tableManager.deleteImage(photoItem)
        _statusLiveData.value = PhotoEditorStatus.UpdateImageList(tableManager.getImageUrlList())
    }

    fun uploadImages(
        uris: List<Uri>,
        context: Context,
        onError: (String) -> Unit
    ) {
        val fileList = mutableListOf<File>()

        uris.forEach {
            val mimeType = context.contentResolver.getType(it)
            when(mimeType) {
                "image/jpeg" -> {
                    fileList.add(
                        filesTool.parseFileToAbsPath(it, "jpg", context)
                    )
                }
                "image/png" -> {
                    fileList.add(
                        filesTool.parseFileToAbsPath(it, "png", context)
                    )
                }
                else -> onError.invoke("不支援 $mimeType 格式")
            }
        }

        viewModelScope.launch {
            _statusLiveData.value = PhotoEditorStatus.Loading

            val resp = repository.uploadFiles(fileList)

            when(resp) {
                is ApiResponse.Success -> {
                    // 新增至 Manager image 清單
                    val list = resp.data.map { PhotoItem(url = it) }
                    tableManager.addImageUrls(list)
                    // 更新狀態至 UI
                    _statusLiveData.value = PhotoEditorStatus.AddImageList(
                        tableManager.getImageUrlList()
                    )
                }
                is ApiResponse.ApiFailure -> getErrorMessage(resp.errorCode, resp.message)
                is ApiResponse.NetworkError -> getErrorMessage(resp.statusCode, resp.message)
            }
        }
    }

    fun uploadVideo(
        uri: Uri,
        extension: String,
        context: Context
    ) {
        viewModelScope.launch {

            _statusLiveData.value = PhotoEditorStatus.Loading

            val file = filesTool.parseFileToAbsPath(uri, extension, context)

            val resp = repository.uploadVideo(file)

            when(resp) {
                is ApiResponse.Success -> {
                    if (resp.data.isNotEmpty()) {
                        val url = resp.data.first()
                        tableManager.setVideoUrl(url)
                        _statusLiveData.value = PhotoEditorStatus.UpdateVideoDone(url)
                    } else {
                        _statusLiveData.value = getErrorMessage("200","Error -影片回傳網址為空")
                    }
                }
                is ApiResponse.ApiFailure -> {
                    _statusLiveData.value = getErrorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    _statusLiveData.value = getErrorMessage(resp.statusCode, resp.message)
                }
            }
        }
    }


    private fun getErrorMessage(
        errorCode: String,
        message: String
    ): PhotoEditorStatus {
        return PhotoEditorStatus.Error(errorCode, message)
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val api = ApiClient.instance
                val source = EditorInfoSourceImp(api)
                val repository = EditorPhotoRepository(source)
                return EditorPhotoViewModel(repository) as T
            }
        }
    }

}