package com.easyrent.abccarapp.abccar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.EditPasswordReq
import com.easyrent.abccarapp.abccar.repository.login.LoginRepository
import com.easyrent.abccarapp.abccar.repository.login.LoginRepositoryImp
import com.easyrent.abccarapp.abccar.source.TokenPackage
import com.easyrent.abccarapp.abccar.source.login.MemberInfoSource
import com.easyrent.abccarapp.abccar.source.preferences.PreferencesSourceImp
import kotlinx.coroutines.launch

class ChangePasswordViewModel  (
private val repository: LoginRepository
) : ViewModel() {

    private val _stateLiveData: MutableLiveData<ApiResponse<TokenPackage>> = MutableLiveData()
    val stateLiveData: LiveData<ApiResponse<TokenPackage>> get() = _stateLiveData

    fun changePassword(req : EditPasswordReq){
        viewModelScope.launch {
            val resp = repository.changePassword(req)
            _stateLiveData.value = resp
        }
    }

    // ViewModel有建構子引數，因此需要提供Factory給框架產生對應的實例
    // 可參考：https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                // 依序將各架構組合起來
                val apiClient = ApiClient.instance
                val apiSource = MemberInfoSource(apiClient)
                val preferencesSource = PreferencesSourceImp(application)
                val repository = LoginRepositoryImp(
                    apiSource = apiSource,
                    preferencesSource = preferencesSource
                )

                return ChangePasswordViewModel(
                    repository
                ) as T
            }
        }
    }
}