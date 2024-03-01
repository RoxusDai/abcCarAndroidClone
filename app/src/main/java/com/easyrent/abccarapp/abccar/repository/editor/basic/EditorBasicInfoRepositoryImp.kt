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
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSource
import java.io.File

class EditorBasicInfoRepositoryImp(
    val source: EditorInfoSource
) : EditorBasicInfoRepository {
    override suspend fun getCarEditInfo(carId: Int): ApiResponse<GetEditCarInfoResp> {
        return source.getEditCarInfo(carId)
    }

    override suspend fun getCarBrandSeriesCategory(req: GetCarBrandSeriesCategoryReq): ApiResponse<List<CarClassificationItem>> {
        return source.getCarClassification(req)
    }

    override suspend fun getManufactureListByCarSeries(req: GetManufactureYearByCarSeriesReq): ApiResponse<List<String>> {
        return source.getManufactureListByCarSeries(req)
    }

    override suspend fun getCarCategoryByManufacture(req: GetCarCategoryByManufactureReq): ApiResponse<List<CarCategoryInfo>> {
        return source.getCarCategoryByManufacture(req)
    }

    override suspend fun getCarModelOtherInputDataByCategoryId(req: GetCarModelOtherInputDataByCategoryIdReq): ApiResponse<List<CarModelOtherInputDataByCategoryIdItem>> {
        return source.getCarModelOtherInputDataByCategoryId(req)
    }

    override suspend fun getSpecification(): ApiResponse<GetSpecificationResp> {
        return source.getSpecification()
    }

    override suspend fun getCheckHotCerNoResult(req: GetCheckHotCerNoResultReq): ApiResponse<Boolean> {
        return source.getCheckHotCerNoResult(req)
    }

    override suspend fun getCheckVehicleNoResult(req: GetCheckVehicleNoResultReq): ApiResponse<Int> {
        return source.getCheckVehicleNoResult(req)
    }

    override suspend fun uploadImageFile(file: File): ApiResponse<List<String>> {
        return source.uploadImageFile(file)
    }

    override suspend fun uploadPdfFile(file: File): ApiResponse<List<String>> {
        return source.uploadPdfFile(file)
    }
}