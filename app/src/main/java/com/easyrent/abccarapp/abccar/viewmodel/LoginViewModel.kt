package com.easyrent.abccarapp.abccar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.MemberLoginReq
import com.easyrent.abccarapp.abccar.repository.login.LoginRepository
import com.easyrent.abccarapp.abccar.repository.login.LoginRepositoryImp
import com.easyrent.abccarapp.abccar.source.login.MemberInfoSource
import com.easyrent.abccarapp.abccar.source.preferences.PreferencesSourceImp
import com.easyrent.abccarapp.abccar.ui.activity.state.LoginMemberState
import kotlinx.coroutines.launch

/**
 *  登入畫面ViewModel
 */

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel() {
    private val _stateLiveData: MutableLiveData<LoginMemberState> = MutableLiveData()
    val stateLiveData: LiveData<LoginMemberState> get() = _stateLiveData

    fun login(
        loginId: String,
        password: String
    ) {
        viewModelScope.launch {
            // 顯示Loading
            _stateLiveData.value = LoginMemberState.Loading

            // 取得API Response，Source預設為IO Thread，可參考MemberLoginSource
            val resp = repository.memberLogin(
                MemberLoginReq(
                    loginId = loginId,
                    password = password
                )
            )

            when(resp) {
                is ApiResponse.Success -> {
                    // 登入成功
                    _stateLiveData.value = LoginMemberState.Login(resp.data.memberInfoResp.id)
                }
                is ApiResponse.ApiFailure -> {
                    // 登入失敗
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

                else -> {}
            }
        }
    }




    private fun refreshToken() {
        viewModelScope.launch {
            _stateLiveData.value = LoginMemberState.Loading

            val resp = repository.refreshToken()

            when(resp) {
                is ApiResponse.Success -> {
                    getMemberInfo()
                }
                is ApiResponse.ApiFailure -> {
                    _stateLiveData.value = LoginMemberState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    _stateLiveData.value = LoginMemberState.Error(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }

                else -> {}
            }
        }
    }

    private fun getMemberInfo() {
        viewModelScope.launch {

            val resp = repository.getMemberInfo()

            when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        _stateLiveData.value = LoginMemberState.Login(resp.data.memberInfoResp.id)
                    }
                }
                is ApiResponse.ApiFailure -> {
                    _stateLiveData.value = LoginMemberState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    _stateLiveData.value = LoginMemberState.Error(
                        errorCode = resp.statusCode,
                        resp.message
                    )
                }

                else -> {}
            }

        }
    }

    // 檢查Token是否存在，並驗證是否有效。
    // 驗證OT是否在期效內，是的話刷新Token
    fun initToken() {
        viewModelScope.launch {
            if (repository.isTokenAvailable()) {
                // 初始化Token
                refreshToken()
            }
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
                val application = checkNotNull(extras[APPLICATION_KEY])

                // 依序將各架構組合起來
                val apiClient = ApiClient.instance
                val apiSource = MemberInfoSource(apiClient)
                val preferencesSource = PreferencesSourceImp(application)
                val repository = LoginRepositoryImp(
                    apiSource = apiSource,
                    preferencesSource = preferencesSource
                )

                return LoginViewModel(
                    repository
                ) as T
            }
        }
    }

}