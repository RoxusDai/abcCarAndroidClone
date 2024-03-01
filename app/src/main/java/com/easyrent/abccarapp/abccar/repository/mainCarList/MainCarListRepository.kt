package com.easyrent.abccarapp.abccar.repository.mainCarList

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.BatchUpdateCarAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetMainCarListReq
import com.easyrent.abccarapp.abccar.remote.request.UpdateMemberCarsStatusReq
import com.easyrent.abccarapp.abccar.remote.response.BatchUpdateCarAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.remote.response.GetSpecificationResp
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarClassificationItem

interface MainCarListRepository {

    suspend fun getCarInfoList(req: GetMainCarListReq): ApiResponse<MainCarListInfo>

    suspend fun updateMemberCarsStatus(req: UpdateMemberCarsStatusReq): ApiResponse<Int>

    suspend fun getCarBrandSeriesCategory(
        req: GetCarBrandSeriesCategoryReq
    ): ApiResponse<List<CarClassificationItem>>

    suspend fun batchUpdateCarAdvertisingBoard(req: BatchUpdateCarAdvertisingBoardReq):ApiResponse<BatchUpdateCarAdvertisingBoardResp>

}