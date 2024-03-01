package com.easyrent.abccarapp.abccar.remote

import com.easyrent.abccarapp.abccar.remote.request.ForgetPasswordReq
import com.easyrent.abccarapp.abccar.remote.response.BaseResponse
import com.easyrent.abccarapp.abccar.remote.response.ForgetPasswordResp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ForgetPasswordApiService {

    @POST("member/ForgetPassword")
    fun forgetPassword(
        @Body req: ForgetPasswordReq
    ): Call<BaseResponse<ForgetPasswordResp>>

}