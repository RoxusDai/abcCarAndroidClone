package com.easyrent.abccarapp.abccar.repository.editor.basic

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarCategoryByManufactureReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarModelOtherInputDataByCategoryIdReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckHotCerNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckVehicleNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetManufactureYearByCarSeriesReq
import com.easyrent.abccarapp.abccar.remote.response.CarModelOtherInputDataByCategoryIdItem
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.remote.response.GetSpecificationResp
import java.io.File

interface EditorBasicInfoRepository {

    suspend fun getCarEditInfo(
        carId: Int
    ): ApiResponse<GetEditCarInfoResp>
    suspend fun getCarBrandSeriesCategory(
        req: GetCarBrandSeriesCategoryReq
    ): ApiResponse<List<CarClassificationItem>>

    suspend fun getManufactureListByCarSeries(
        req: GetManufactureYearByCarSeriesReq
    ): ApiResponse<List<String>>

    suspend fun getCarCategoryByManufacture(
        req: GetCarCategoryByManufactureReq
    ): ApiResponse<List<CarCategoryInfo>>

    suspend fun getCarModelOtherInputDataByCategoryId(
        req: GetCarModelOtherInputDataByCategoryIdReq
    ): ApiResponse<List<CarModelOtherInputDataByCategoryIdItem>>

    suspend fun getSpecification(): ApiResponse<GetSpecificationResp>

    suspend fun getCheckHotCerNoResult(
        req: GetCheckHotCerNoResultReq
    ): ApiResponse<Boolean>

    suspend fun getCheckVehicleNoResult(
        req: GetCheckVehicleNoResultReq
    ): ApiResponse<Int>

    suspend fun uploadImageFile(
        file: File
    ): ApiResponse<List<String>>

    suspend fun uploadPdfFile(
        file: File
    ): ApiResponse<List<String>>
}