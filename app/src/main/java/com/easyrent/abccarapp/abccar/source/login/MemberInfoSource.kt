package com.easyrent.abccarapp.abccar.source.login

import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.ApiService
import com.easyrent.abccarapp.abccar.remote.request.EditPasswordReq
import com.easyrent.abccarapp.abccar.remote.request.MemberLoginReq
import com.easyrent.abccarapp.abccar.repository.login.MemberLoginInfo
import com.easyrent.abccarapp.abccar.source.HttpResponse
import com.easyrent.abccarapp.abccar.source.HttpResponseTransfer
import com.easyrent.abccarapp.abccar.source.TokenPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemberInfoSource(
    private val api: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO // 預設是IO Thread
) {

    private val updateTokenEvent: (String) -> TokenPackage = { original ->
        val resp = HttpResponseTransfer.transform(
            api.refreshAuthorizationToken(original)
        )
        if (resp is HttpResponse.Success) {
            TokenPackage(
                originalToken = resp.payload.originalToken ?: "",
                authorizationToken = resp.payload.authorizationToken ?: ""
            )
        } else {
            TokenPackage()
        }
    }

    suspend fun changePassword(req: EditPasswordReq): ApiResponse<TokenPackage> =
        withContext(ioDispatcher) {

            val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
            val response = HttpResponseTransfer.transform(
                api.editMemberPassword(
                    authToken = authToken,
                    req = req
                )
            )
            when(response){
                is HttpResponse.Success -> {
                    if (response.payload.code == "0") {
                        val baseResp = response.payload
                        //回傳更新密碼結果
                        val tokenPackage = TokenPackage(
                            code = response.payload.code.toInt(),
                            message = response.payload.message,
                            originalToken = response.payload.originalToken ?: "",
                            authorizationToken = response.payload.authorizationToken ?: ""
                        )

                        //更新token?
                        if(response.payload.code.toInt() == 0){
                            response.payload.authorizationToken?.let {
                                AccountInfoManager.setAuthToken(it)
                            }
                        }

                        ApiResponse.Success(tokenPackage)
                    } else {
                        ApiResponse.ApiFailure(
                            errorCode = response.payload.code,
                            message = response.payload.message
                        )
                    }
                }
                is HttpResponse.Failure -> {
                    ApiResponse.NetworkError(
                        statusCode = response.statusCode,
                        message = response.httpMessage
                    )
                }
            }
        }
        suspend fun memberLogin(req: MemberLoginReq): ApiResponse<MemberLoginInfo> =
        withContext(ioDispatcher) {

            val response = HttpResponseTransfer.transform(
                api.memberLogin(req)
            )

            when(response) {
                is HttpResponse.Success -> {
                    if (response.payload.code == "0") {
                        val baseResp = response.payload
                        val memberInfo = MemberLoginInfo(
                            memberInfoResp = baseResp.data.memberInfoResp,
                            originalToken = baseResp.originalToken?: "",
                            authorizationToken = baseResp.authorizationToken?: ""
                        )
                        ApiResponse.Success(memberInfo)
                    } else {
                        ApiResponse.ApiFailure(
                            errorCode = response.payload.code,
                            message = response.payload.message
                        )
                    }
                }
                is HttpResponse.Failure -> {
                    ApiResponse.NetworkError(
                        statusCode = response.statusCode,
                        message = response.httpMessage
                    )
                }
            }
        }

    suspend fun refreshAuthToken(): ApiResponse<TokenPackage> =
        withContext(ioDispatcher) {

            val originalToken = AccountInfoManager.checkToken(updateTokenEvent).originalToken

            val response = HttpResponseTransfer.transform(
                api.refreshAuthorizationToken(originalToken)
            )

            when (response) {
                is HttpResponse.Success -> {
                    if (response.payload.code == "0") {
                        val tokenPackage = TokenPackage(
                            originalToken = response.payload.originalToken ?: "",
                            authorizationToken = response.payload.authorizationToken ?: ""
                        )
                        ApiResponse.Success(tokenPackage)
                    } else {
                        ApiResponse.ApiFailure(
                            errorCode = response.payload.code,
                            message = response.payload.message
                        )
                    }
                }
                is HttpResponse.Failure -> {
                    ApiResponse.NetworkError(
                        statusCode = response.statusCode,
                        message = response.httpMessage
                    )
                }
            }
        }

    suspend fun getMemberInfo(): ApiResponse<MemberLoginInfo> =
        withContext(ioDispatcher) {

            val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

            val response = HttpResponseTransfer.transform(
                api.getMemberInfo(authToken)
            )

            when(response) {
                is HttpResponse.Success -> {
                    if (response.payload.code == "0") {
                        val info = MemberLoginInfo(
                            memberInfoResp = response.payload.data,
                            originalToken = "",
                            authorizationToken = response.payload.authorizationToken ?: ""
                        )

                        ApiResponse.Success(
                            data = info
                        )
                    } else {
                        ApiResponse.ApiFailure(
                            errorCode = response.payload.code,
                            message = response.payload.message
                        )
                    }
                }
                is HttpResponse.Failure -> {
                    ApiResponse.NetworkError(
                        statusCode = response.statusCode,
                        message = response.httpMessage
                    )
                }
            }
        }
}