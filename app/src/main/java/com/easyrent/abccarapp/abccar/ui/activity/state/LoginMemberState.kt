package com.easyrent.abccarapp.abccar.ui.activity.state

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.response.ForgetPasswordResp

sealed class LoginMemberState {

    data object Loading : LoginMemberState()
    data class Login(val id : Int) : LoginMemberState()
    data class ForgetPassword(val provider: String = "") : LoginMemberState()
    class Error(
        val errorCode: String,
        val message: String
    ) : LoginMemberState()

}

