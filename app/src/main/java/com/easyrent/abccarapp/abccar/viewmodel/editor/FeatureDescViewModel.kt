package com.easyrent.abccarapp.abccar.viewmodel.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem
import com.easyrent.abccarapp.abccar.repository.editor.FeatureDescRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.tools.FilesTool
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.FeatureEditorStatus
import kotlinx.coroutines.launch

class FeatureDescViewModel(
    val repository: FeatureDescRepository
) : ViewModel() {

    private val filesTool = FilesTool()

    private val _statusLiveData: MutableLiveData<FeatureEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<FeatureEditorStatus> get() = _statusLiveData

    private val cerTypeList: MutableList<GetCertificateTypesItem> = mutableListOf()

    fun initTagsList() {
        viewModelScope.launch {

            _statusLiveData.value = FeatureEditorStatus.Loading

            val accessoriesResp = repository.getAccessoriesTagsList()

            _statusLiveData.value = when(accessoriesResp) {
                is ApiResponse.Success -> {
                    accessoriesResp.data.let {
                         if (it.resultCode == "0") {
                             FeatureEditorStatus.InitEquippedTagsList(it)
                         } else {
                             errorMessage(it.resultCode, it.resultMessage)
                         }
                    }
                }
                is ApiResponse.ApiFailure -> errorMessage(accessoriesResp.errorCode, accessoriesResp.message)

                is ApiResponse.NetworkError -> {
                    errorMessage(accessoriesResp.statusCode, accessoriesResp.message)
                }
            }

            _statusLiveData.value = FeatureEditorStatus.Loading

            val featureResp = repository.getCarFeatureTagsList()

            _statusLiveData.value = when(featureResp) {
                is ApiResponse.Success -> {
                    featureResp.data.let {
                        FeatureEditorStatus.InitFeatureTagsList(it)
                    }
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(featureResp.errorCode, featureResp.message)
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(featureResp.statusCode, featureResp.message)
                }
            }

            _statusLiveData.value = FeatureEditorStatus.InitDone
        }
    }

    fun getTemplateContent() {
        viewModelScope.launch {
            _statusLiveData.value = FeatureEditorStatus.Loading

            val resp = repository.getEditorTemplate()

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    val stringBuilder = StringBuilder()
                    // 將Type 2的字串彙整
                    resp.data.filter { it.templateType == 2 }
                        .map { it.templateContent }
                        .forEach {
                            stringBuilder.append(it)
                        }
                    FeatureEditorStatus.TemplateContent(stringBuilder.toString())
                }
                is ApiResponse.ApiFailure -> errorMessage(resp.errorCode, resp.message)
                is ApiResponse.NetworkError -> errorMessage(resp.statusCode, resp.message)
            }
        }
    }

    fun getCerTypeList(index: Int) {
        if (cerTypeList.isEmpty()) {
            viewModelScope.launch {

                _statusLiveData.value = FeatureEditorStatus.Loading
                val resp = repository.getCertificateTypeTags()

                when(resp) {
                    is ApiResponse.Success -> {
                         cerTypeList.addAll(resp.data)
                        _statusLiveData.value = FeatureEditorStatus.GetCerTypeList(index, cerTypeList)
                    }
                    is ApiResponse.ApiFailure -> {
                        _statusLiveData.value = errorMessage(resp.errorCode, resp.message)
                    }
                    is ApiResponse.NetworkError -> {
                        _statusLiveData.value = errorMessage(resp.statusCode, resp.message)
                    }
                }
            }
        } else {
            _statusLiveData.value = FeatureEditorStatus.GetCerTypeList(index, cerTypeList)
        }
    }

    fun getCarTypeListToInit() {
        if (cerTypeList.isEmpty()) {
            viewModelScope.launch {

                _statusLiveData.value = FeatureEditorStatus.Loading
                val resp = repository.getCertificateTypeTags()

                when(resp) {
                    is ApiResponse.Success -> {
                        cerTypeList.addAll(resp.data)
                        _statusLiveData.value = FeatureEditorStatus.InitCerListType(cerTypeList)
                    }
                    is ApiResponse.ApiFailure -> {
                        _statusLiveData.value = errorMessage(resp.errorCode, resp.message)
                    }
                    is ApiResponse.NetworkError -> {
                        _statusLiveData.value = errorMessage(resp.statusCode, resp.message)
                    }
                }
            }
        } else {
            _statusLiveData.value = FeatureEditorStatus.InitCerListType(cerTypeList)
        }
    }

    fun uploadCerFile(uri: Uri, extension: String, context: Context) {
        viewModelScope.launch {
            _statusLiveData.value = FeatureEditorStatus.Loading

            val file = filesTool.parseFileToAbsPath(uri, extension, context)
            val resp = when(extension) {
                "jpg" -> repository.uploadImageFile(file)
                "png" -> repository.uploadImageFile(file)
                else ->  repository.uploadPdfFile(file)
            }

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    val list = resp.data
                    if (list.isEmpty()) {
                        errorMessage("0" ,"回應位置為Empty")
                    } else {
                        FeatureEditorStatus.GetUploadUrl(resp.data.first())
                    }
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> errorMessage(resp.statusCode ,resp.message)
            }
        }
    }

    fun getTagsConvertResult(req: ArrayList<TagsConvertReqItem>) {
        viewModelScope.launch {
            _statusLiveData.value = FeatureEditorStatus.Loading

            val resp = repository.convertTags(req)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    FeatureEditorStatus.InitTagsStatus(resp.data)
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(resp.statusCode, resp.message)
                }
            }
        }
    }

    private fun errorMessage(errorCode: String, message: String): FeatureEditorStatus {
        return FeatureEditorStatus.ErrorMessage(errorCode, message)
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val api = ApiClient.instance
                val source = EditorInfoSourceImp(api)
                val repository = FeatureDescRepository(source)

                return FeatureDescViewModel(repository) as T
            }
        }
    }
}