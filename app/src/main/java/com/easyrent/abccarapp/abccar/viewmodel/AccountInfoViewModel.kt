package com.easyrent.abccarapp.abccar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.repository.AccountInfoRepository
import com.easyrent.abccarapp.abccar.source.main.account.AccountInfoSource
import com.easyrent.abccarapp.abccar.ui.fragment.state.AccountInfoState
import kotlinx.coroutines.launch

class AccountInfoViewModel(
    private val repository: AccountInfoRepository
) : ViewModel() {

    private val _statusLiveData: MutableLiveData<AccountInfoState> = MutableLiveData()
    val statLiveData: LiveData<AccountInfoState> get() = _statusLiveData

    fun getAccountInfo() {
        viewModelScope.launch {

            _statusLiveData.value = AccountInfoState.Loading

            val accountResp = repository.getAccountInfo()

            _statusLiveData.value = when(accountResp) {
                is ApiResponse.Success -> {
                    AccountInfoState.AccountInfo(accountResp.data)
                }
                is ApiResponse.ApiFailure -> errorMessage(accountResp.errorCode, accountResp.message)
                is ApiResponse.NetworkError -> errorMessage(accountResp.statusCode, accountResp.message)
            }

            _statusLiveData.value = AccountInfoState.Loading

            val pointResp = repository.getPointHistory()

            _statusLiveData.value = when(pointResp) {
                is ApiResponse.Success -> {
                    AccountInfoState.PointInfo(pointResp.data)
                }
                is ApiResponse.ApiFailure -> errorMessage(pointResp.errorCode, pointResp.message)
                is ApiResponse.NetworkError -> errorMessage(pointResp.statusCode, pointResp.message)
            }

            _statusLiveData.value = AccountInfoState.Loading

            val memberInfoResp = repository.getMemberInfo()

            _statusLiveData.value = when(memberInfoResp) {
                is ApiResponse.Success -> {
                    AccountInfoState.MemberInfo(memberInfoResp.data)
                }
                is ApiResponse.ApiFailure -> errorMessage(memberInfoResp.errorCode, memberInfoResp.message)
                is ApiResponse.NetworkError -> errorMessage(memberInfoResp.statusCode, memberInfoResp.message)
            }
        }
    }

    private fun errorMessage(
        errorCode: String,
        message: String
    ): AccountInfoState.Error {
        return AccountInfoState.Error(errorCode, message)
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val api = ApiClient.instance
                val source = AccountInfoSource(api)
                val repository = AccountInfoRepository(source)
                return AccountInfoViewModel(repository) as T
            }
        }
    }

}