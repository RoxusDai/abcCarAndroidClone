package com.easyrent.abccarapp.abccar.source.main.account

import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.ApiService
import com.easyrent.abccarapp.abccar.remote.request.GetLastPoolPointsHistoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetMainCarListReq
import com.easyrent.abccarapp.abccar.remote.response.DisplayTitle
import com.easyrent.abccarapp.abccar.remote.response.GetLastPoolPointsHistoryResp
import com.easyrent.abccarapp.abccar.remote.response.GetMemberResp
import com.easyrent.abccarapp.abccar.source.HttpResponse
import com.easyrent.abccarapp.abccar.source.HttpResponseTransfer
import com.easyrent.abccarapp.abccar.source.TokenPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountInfoSource(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val updateTokenEvent: (String) -> TokenPackage = { original ->
        val resp = HttpResponseTransfer.transform(
            apiService.refreshAuthorizationToken(original)
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

    suspend fun getAccountInfo(): ApiResponse<DisplayTitle> = withContext(ioDispatcher) {
        val memberId = AccountInfoManager.getMemberInfo().memberId
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val resp = HttpResponseTransfer.transform(
            apiService.getEditCarList(
                authToken = authToken,
                req = GetMainCarListReq(
                    memberId = memberId,
                    event = 0,
                    pageIndex = 1,
                    pageSize = 1
                )
            )
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0") {
                    ApiResponse.Success(resp.payload.data.displayTitle)
                } else {
                    ApiResponse.ApiFailure(
                        errorCode = resp.payload.code,
                        message = resp.payload.message
                    )
                }
            }
            is HttpResponse.Failure -> {
                ApiResponse.NetworkError(
                    statusCode = resp.statusCode,
                    message = resp.httpMessage
                )
            }
        }
    }

    suspend fun getPointHistory(): ApiResponse<GetLastPoolPointsHistoryResp> = withContext(ioDispatcher) {
        val memberId = AccountInfoManager.getMemberInfo().memberId
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val resp = HttpResponseTransfer.transform(
            apiService.getPointsHistory(
                authToken = authToken,
                req = GetLastPoolPointsHistoryReq(memberId)
            )
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0") {
                    ApiResponse.Success(resp.payload.data)
                } else {
                    ApiResponse.ApiFailure(
                        errorCode = resp.payload.code,
                        message = resp.payload.message
                    )
                }
            }
            is HttpResponse.Failure -> {
                ApiResponse.NetworkError(
                    statusCode = resp.statusCode,
                    message = resp.httpMessage
                )
            }
        }
    }

    suspend fun getMemberInfo(): ApiResponse<GetMemberResp> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val resp = HttpResponseTransfer.transform(
            apiService.getMemberInfo(authToken = authToken)
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0") {
                    ApiResponse.Success(resp.payload.data)
                } else {
                    ApiResponse.ApiFailure(
                        errorCode = resp.payload.code,
                        message = resp.httpMessage
                    )
                }
            }
            is HttpResponse.Failure -> {
                ApiResponse.NetworkError(
                    statusCode = resp.statusCode,
                    message = resp.httpMessage
                )
            }
        }
    }

}