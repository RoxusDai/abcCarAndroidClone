package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp
import com.easyrent.abccarapp.abccar.repository.editor.contact.ContactInfoRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.ContactEditorStatus
import kotlinx.coroutines.launch

class DetailContactViewModel(
    val repository: ContactInfoRepository
) :ViewModel() {

    val contactList  = mutableListOf<GetContactPersonListResp>()

    private val _statusLiveData : MutableLiveData<ContactEditorStatus>  = MutableLiveData()
    val statusLiveData : LiveData<ContactEditorStatus> = _statusLiveData
    fun initContactList(id : Int){

        viewModelScope.launch {

                val resp = repository.getContactPersonList(id)

                 when(resp) {
                    is ApiResponse.Success -> {
                        resp.data.let {
                            contactList.add(it)
                            _statusLiveData.value = ContactEditorStatus.DetailContactList(contactList)
                        }
                    }
                    is ApiResponse.ApiFailure -> {
                        errorMessage(
                            errorCode = resp.errorCode,
                            message = resp.message
                        )
                    }
                    is ApiResponse.NetworkError -> {
                        errorMessage(
                            errorCode = resp.statusCode,
                            message = resp.message
                        )
                    }
                }
        }
    }

    private fun errorMessage(
        errorCode: String,
        message: String
    ): ContactEditorStatus.ErrorMessage {
        return ContactEditorStatus.ErrorMessage(errorCode, message)
    }


    companion object {
        ///設定資料來源
        ///想了一下,還是從編輯呼叫
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = EditorInfoSourceImp(apiClient)
                val repository = ContactInfoRepository(apiSource)
                return DetailContactViewModel(repository) as T
            }
        }
    }
}