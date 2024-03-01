package com.easyrent.abccarapp.abccar.viewmodel.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.repository.editor.accessories.AccessoriesInfoRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.AccessoriesEditorStatus
import kotlinx.coroutines.launch

class AccessoriesInfoViewModel(
    val repository: AccessoriesInfoRepository
) : ViewModel() {

    private val _statusLiveData: MutableLiveData<AccessoriesEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<AccessoriesEditorStatus> = _statusLiveData

    fun initTagsList() {
        viewModelScope.launch {
            _statusLiveData.value = AccessoriesEditorStatus.Loading

            val resp = repository.getAccessoriesTagsList()

            when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        if (it.resultCode == "0") {
                            _statusLiveData.value = AccessoriesEditorStatus.InitTagsList(it)
                        } else {
                            getMessageStatus(it.resultCode, it.resultMessage)
                        }
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getMessageStatus(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    getMessageStatus(resp.statusCode, resp.message)
                }
            }
        }
    }

    fun getTagsConvertResult(req: ArrayList<TagsConvertReqItem>) {
        viewModelScope.launch {
            _statusLiveData.value = AccessoriesEditorStatus.Loading

            val resp =  repository.convertTags(req)

            when(resp) {
                is ApiResponse.Success -> {
                    _statusLiveData.value = AccessoriesEditorStatus.InitTagsCheckStatus(resp.data)
                }
                is ApiResponse.ApiFailure -> {
                    getMessageStatus(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    getMessageStatus(resp.statusCode, resp.message)
                }
            }
        }

    }

    private fun getMessageStatus(
        errorCode: String,
        message: String
    ): AccessoriesEditorStatus {
        return AccessoriesEditorStatus.ErrorMessage(errorCode, message)
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val api = ApiClient.instance
                val source = EditorInfoSourceImp(api)
                val repository = AccessoriesInfoRepository(source)
                return AccessoriesInfoViewModel(repository) as T
            }
        }
    }

}