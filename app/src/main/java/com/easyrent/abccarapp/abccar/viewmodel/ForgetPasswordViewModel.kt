package com.easyrent.abccarapp.abccar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.ForgetPasswordReq
import com.easyrent.abccarapp.abccar.source.login.ForgetPasswordRepository
import com.easyrent.abccarapp.abccar.source.login.ForgetPasswordRepositoryImp
import com.easyrent.abccarapp.abccar.source.login.ForgetPasswordSource
import com.easyrent.abccarapp.abccar.ui.activity.state.LoginMemberState
import kotlinx.coroutines.launch

class ForgetPasswordViewModel (
    private val repository: ForgetPasswordRepository
) : ViewModel() {

    private val _stateLiveData: MutableLiveData<LoginMemberState> = MutableLiveData()
    val stateLiveData: LiveData<LoginMemberState> get() = _stateLiveData

    fun retrivePassword(phone: String){
        viewModelScope.launch {
            val resp = repository.forgetPassword(ForgetPasswordReq(phone))

            when(resp) {
                is ApiResponse.Success -> {
                        try{
                            val provider = resp.data.datas.last().provider
                            _stateLiveData.value = LoginMemberState.ForgetPassword(provider)
                        }
                        catch(e:Exception){
                            _stateLiveData.value = LoginMemberState.ForgetPassword()
                        }
                }
                is ApiResponse.ApiFailure -> {
                    _stateLiveData.value = LoginMemberState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }

                is ApiResponse.NetworkError -> {

                    when(resp.statusCode) {
                        "-1" -> {
                            _stateLiveData.value = LoginMemberState.Error(
                                errorCode = resp.statusCode,
                                message = "請檢查您的裝置網路是否正常"
                            )
                        }
                        else -> {
                            _stateLiveData.value = LoginMemberState.Error(
                                errorCode =  resp.statusCode,
                                message = resp.message
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                // 依序將各架構組合起來
                val apiClient = ApiClient.forgetPasswordInstance
                val apiSource = ForgetPasswordSource(apiClient)
                val repository = ForgetPasswordRepositoryImp(
                    api = apiSource
                )

                return ForgetPasswordViewModel(
                    repository
                ) as T
            }
        }
    }
}