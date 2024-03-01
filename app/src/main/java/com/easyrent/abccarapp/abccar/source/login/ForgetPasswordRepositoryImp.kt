package com.easyrent.abccarapp.abccar.source.login

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.ForgetPasswordReq
import com.easyrent.abccarapp.abccar.remote.response.ForgetPasswordResp


class ForgetPasswordRepositoryImp (
    val api: ForgetPasswordSource
): ForgetPasswordRepository {

    override suspend fun forgetPassword(req: ForgetPasswordReq): ApiResponse<ForgetPasswordResp>{
        return api.forgetPassword(req)
    }
}