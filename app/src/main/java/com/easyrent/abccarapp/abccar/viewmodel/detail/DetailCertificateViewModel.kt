package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem
import com.easyrent.abccarapp.abccar.repository.editor.FeatureDescRepository

import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.FeatureEditorStatus
import kotlinx.coroutines.launch

class DetailCertificateViewModel(
    val repository: FeatureDescRepository
) : ViewModel() {
//api:GetCertificateTypes

    private val _statusLiveData: MutableLiveData<FeatureEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<FeatureEditorStatus> get() = _statusLiveData

    private val certificationTypeList = mutableListOf<GetCertificateTypesItem>()


    fun setCertificationList() {
        if (certificationTypeList.isEmpty()) {
            viewModelScope.launch {
                val resp = repository.getCertificateTypeTags()

                when(resp) {
                    is ApiResponse.Success -> {
                        val list = resp.data
                        certificationTypeList.addAll(list)
                        _statusLiveData.value = FeatureEditorStatus.InitCerListType(certificationTypeList)
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
            _statusLiveData.value = FeatureEditorStatus.InitCerListType(certificationTypeList)
        }
    }
    fun getCertificationList() : MutableList<GetCertificateTypesItem>{
        return certificationTypeList
    }

    private fun errorMessage(errorCode: String, message: String): FeatureEditorStatus {
        return FeatureEditorStatus.ErrorMessage(errorCode, message)
    }


    companion object {
        ///設定資料來源
        ///想了一下,還是從編輯呼叫
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = EditorInfoSourceImp(apiClient)
                val repository = FeatureDescRepository(apiSource)
                return DetailCertificateViewModel(repository) as T
            }
        }
    }
}