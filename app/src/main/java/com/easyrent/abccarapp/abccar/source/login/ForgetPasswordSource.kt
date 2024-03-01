package com.easyrent.abccarapp.abccar.source.login

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.ForgetPasswordApiService
import com.easyrent.abccarapp.abccar.remote.request.ForgetPasswordReq
import com.easyrent.abccarapp.abccar.remote.response.ForgetPasswordResp
import com.easyrent.abccarapp.abccar.source.HttpResponse
import com.easyrent.abccarapp.abccar.source.HttpResponseTransfer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForgetPasswordSource(
    private val api: ForgetPasswordApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO // 預設是IO Thread
) {
        suspend fun forgetPassword(req: ForgetPasswordReq): ApiResponse<ForgetPasswordResp> =
        withContext(ioDispatcher) {
            val response = HttpResponseTransfer.transform(api.forgetPassword(req))
            when (response) {
                is HttpResponse.Success -> {
                    val baseResp = response.payload
                    ApiResponse.Success(baseResp.data)
                }
                else -> {
                    ApiResponse.ApiFailure(
                        errorCode = "error",
                        message = response.httpMessage
                    )
                }
            }
        }
}