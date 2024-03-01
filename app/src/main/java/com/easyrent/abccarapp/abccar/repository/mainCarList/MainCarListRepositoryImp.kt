package com.easyrent.abccarapp.abccar.repository.mainCarList

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.BatchUpdateCarAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetMainCarListReq
import com.easyrent.abccarapp.abccar.remote.request.UpdateMemberCarsStatusReq
import com.easyrent.abccarapp.abccar.remote.response.BatchUpdateCarAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarClassificationItem
import com.easyrent.abccarapp.abccar.source.main.carList.MainCarListSource

class MainCarListRepositoryImp(
    private val apiSource: MainCarListSource
) : MainCarListRepository {
    override suspend fun getCarInfoList(req: GetMainCarListReq): ApiResponse<MainCarListInfo> {
        return apiSource.getEditCarList(req)
    }
    override suspend fun updateMemberCarsStatus(req: UpdateMemberCarsStatusReq): ApiResponse<Int> {
        return apiSource.updateMemberCarsStatus(req)
    }

    override suspend fun getCarBrandSeriesCategory(req: GetCarBrandSeriesCategoryReq): ApiResponse<List<CarClassificationItem>> {
        return apiSource.getCarBrandSeriesCategory(req)
    }

    override suspend fun batchUpdateCarAdvertisingBoard(req:BatchUpdateCarAdvertisingBoardReq): ApiResponse<BatchUpdateCarAdvertisingBoardResp>{
        return apiSource.batchUpdateCarAdvertisingBoard(req)
    }
}