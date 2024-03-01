package com.easyrent.abccarapp.abccar.source.editor

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.AddCarAndPutOnAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.AddCarReq
import com.easyrent.abccarapp.abccar.remote.request.AddContactPersonReq
import com.easyrent.abccarapp.abccar.remote.request.EditCarReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarCategoryByManufactureReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarModelOtherInputDataByCategoryIdReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckHotCerNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckVehicleNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetManufactureYearByCarSeriesReq
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.response.AddCarAndPutOnAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.remote.response.AddCarResp
import com.easyrent.abccarapp.abccar.remote.response.CarModelOtherInputDataByCategoryIdItem
import com.easyrent.abccarapp.abccar.remote.response.EditCarResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarFeatureResp
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesResp
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp
import com.easyrent.abccarapp.abccar.remote.response.GetCountryAndDistrictListResp
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.remote.response.GetEditorTemplateItem
import com.easyrent.abccarapp.abccar.remote.response.GetSpecificationResp
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarCategoryInfo
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarClassificationItem
import java.io.File

interface EditorInfoSource {
    suspend fun getEditCarInfo(
        carId: Int
    ): ApiResponse<GetEditCarInfoResp>
    suspend fun getCarClassification(
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

    suspend fun uploadVideoFile(
        file: File
    ): ApiResponse<List<String>>

    suspend fun uploadMultiImages(
        fileList: List<File>
    ): ApiResponse<List<String>>

    suspend fun getContactPersonList(
        memberId: Int
    ): ApiResponse<GetContactPersonListResp>

    suspend fun addContactPerson(
        req: AddContactPersonReq
    ): ApiResponse<Boolean>

    suspend fun getSpecification(): ApiResponse<GetSpecificationResp>
    suspend fun getCarEquippedTags(): ApiResponse<GetCarEquippedTagsResp>
    suspend fun getCarFeatureTags(): ApiResponse<GetCarFeatureResp>
    suspend fun getCertificateTypeTags(): ApiResponse<GetCertificateTypesResp>
    suspend fun getCountryAndDistrictList(): ApiResponse<GetCountryAndDistrictListResp>
    suspend fun getEditorTemplate(): ApiResponse<List<GetEditorTemplateItem>>
    suspend fun addCar(req: AddCarReq): ApiResponse<AddCarResp>
    suspend fun addCarAndPutOnAdvertisingBoard(
        req: AddCarAndPutOnAdvertisingBoardReq
    ): ApiResponse<AddCarAndPutOnAdvertisingBoardResp>

    suspend fun getTagsConvertResult(
        req: List<TagsConvertReqItem>
    ): ApiResponse<List<TagsConvertRespItem>>

    suspend fun editCar(
        req: EditCarReq
    ): ApiResponse<EditCarResp>
}