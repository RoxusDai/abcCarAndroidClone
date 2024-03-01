package com.easyrent.abccarapp.abccar.source.main.carList

import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.ApiService
import com.easyrent.abccarapp.abccar.remote.request.BatchUpdateCarAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetMainCarListReq
import com.easyrent.abccarapp.abccar.remote.request.UpdateMemberCarsStatusReq
import com.easyrent.abccarapp.abccar.remote.response.BatchUpdateCarAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarClassificationItem
import com.easyrent.abccarapp.abccar.repository.mainCarList.MainCarListInfo
import com.easyrent.abccarapp.abccar.source.HttpResponse
import com.easyrent.abccarapp.abccar.source.HttpResponseTransfer
import com.easyrent.abccarapp.abccar.source.TokenPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainCarListSourceImp(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MainCarListSource {

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

    override suspend fun getEditCarList(req: GetMainCarListReq): ApiResponse<MainCarListInfo> =
        withContext(ioDispatcher) {

            // 替換req的MemberId
            req.memberId = AccountInfoManager.getMemberInfo().memberId

            val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

            val response = HttpResponseTransfer.transform(
                apiService.getEditCarList(authToken, req)
            )
            
            when(response) {
                is HttpResponse.Success -> {
                    if (response.payload.code == "0") {
                        val baseResp = response.payload
                        val info = MainCarListInfo(
                            displayTitle = baseResp.data.displayTitle,
                            carEditList = baseResp.data.carEditList,
                            pagination = baseResp.data.pagination
                        )
                        ApiResponse.Success(info)
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

    override suspend fun batchUpdateCarAdvertisingBoard(
        req: BatchUpdateCarAdvertisingBoardReq
    ): ApiResponse<BatchUpdateCarAdvertisingBoardResp> = withContext(ioDispatcher){
        val auth = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        req.MemberID = AccountInfoManager.getMemberInfo().memberId

        val resp = HttpResponseTransfer.transform(
            apiService.batchUpdateCarAdvertisingBoard(authToken = auth, req = req)
        )

        when(resp){
            is HttpResponse.Success->{
                if (resp.payload.code == "0") {
                ApiResponse.Success(resp.payload.data)
                }
                else{
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

    override suspend fun updateMemberCarsStatus(
        req: UpdateMemberCarsStatusReq
    ): ApiResponse<Int> = withContext(ioDispatcher) {
        val auth = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val memberId = AccountInfoManager.getMemberInfo().memberId

        req.MemberID = memberId

        val resp = HttpResponseTransfer.transform(
            apiService.updateMemberCarsStatus(authToken = auth, req = req)
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

    override suspend fun getCarBrandSeriesCategory(req: GetCarBrandSeriesCategoryReq)
    :ApiResponse<List<CarClassificationItem>> = withContext(ioDispatcher){
        val resp = HttpResponseTransfer.transform(
            apiService.getCarBrandSeriesCategory(req)
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0") {
                    val list = resp.payload.data
                    val itemList = mutableListOf<CarClassificationItem>()

                    // 轉一層Data Class，處理null的問題
                    list.forEach { respItem ->
                        itemList.add(
                            CarClassificationItem(
                                brandID = respItem.brandID,
                                brandName = respItem.brandName,
                                categoryID = respItem.categoryID,
                                categoryName = respItem.categoryName ?: "",
                                seriesID = respItem.seriesID,
                                seriesName = respItem.seriesName ?: ""
                            )
                        )
                    }
                    ApiResponse.Success(itemList)
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
}