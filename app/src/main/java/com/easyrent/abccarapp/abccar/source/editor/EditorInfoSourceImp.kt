package com.easyrent.abccarapp.abccar.source.editor

import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.ApiService
import com.easyrent.abccarapp.abccar.remote.request.AddCarAndPutOnAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.AddCarReq
import com.easyrent.abccarapp.abccar.remote.request.AddContactPersonReq
import com.easyrent.abccarapp.abccar.remote.request.EditCarReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarCategoryByManufactureReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarModelOtherInputDataByCategoryIdReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckHotCerNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckVehicleNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetEditCarInfoReq
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
import com.easyrent.abccarapp.abccar.source.HttpResponse
import com.easyrent.abccarapp.abccar.source.HttpResponseTransfer
import com.easyrent.abccarapp.abccar.source.TokenPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class EditorInfoSourceImp(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : EditorInfoSource {

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

    override suspend fun getEditCarInfo(
        carId: Int
    ): ApiResponse<GetEditCarInfoResp> = withContext(ioDispatcher) {
        val memberId = AccountInfoManager.getMemberInfo().memberId
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val resp = HttpResponseTransfer.transform(
            apiService.getEditCarInfo(
                authToken = authToken,
                req = GetEditCarInfoReq(carId, memberId)
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

    // car/GetCarBrandSeriesCategory
    override suspend fun getCarClassification(
        req: GetCarBrandSeriesCategoryReq
    ): ApiResponse<List<CarClassificationItem>> = withContext(ioDispatcher) {
        val response = HttpResponseTransfer.transform(
            apiService.getCarBrandSeriesCategory(req)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    val list = response.payload.data
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

    // car/GetManufactureYearByCarSeries
    override suspend fun getManufactureListByCarSeries(
        req: GetManufactureYearByCarSeriesReq
    ): ApiResponse<List<String>> = withContext(ioDispatcher) {
        val response = HttpResponseTransfer.transform(
            apiService.getManufactureYearByCarSeries(req)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    ApiResponse.Success(
                        response.payload.data.manufactureYear
                    )
                } else {
                    ApiResponse.ApiFailure(
                        errorCode = response.payload.code,
                        message = response.httpMessage
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

    // car/GetCarCategoryByManufacture
    override suspend fun getCarCategoryByManufacture(
        req: GetCarCategoryByManufactureReq
    ): ApiResponse<List<CarCategoryInfo>> = withContext(ioDispatcher) {
        val response = HttpResponseTransfer.transform(
            apiService.getCarCategoryByManufacture(req)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    val list = mutableListOf<CarCategoryInfo>().apply {
                        response.payload.data.forEach {
                            add(
                                CarCategoryInfo(
                                    categoryId = it.id,
                                    name = it.name,
                                    parentID = it.parentID,
                                    shownOnDropdown = it.shownOnDropdown,
                                    type = it.type
                                )
                            )
                        }
                    }
                    ApiResponse.Success(list)
                } else {
                    ApiResponse.ApiFailure(
                        errorCode = response.payload.code,
                        message = response.httpMessage
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

    override suspend fun getCarModelOtherInputDataByCategoryId(
        req: GetCarModelOtherInputDataByCategoryIdReq
    ): ApiResponse<List<CarModelOtherInputDataByCategoryIdItem>> = withContext(ioDispatcher) {
        val response = HttpResponseTransfer.transform(
            apiService.getCarModelOtherInputDataByCategoryId(req)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    ApiResponse.Success(
                        response.payload.data
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

    override suspend fun getSpecification(): ApiResponse<GetSpecificationResp> =
        withContext(ioDispatcher) {

            val resp = HttpResponseTransfer.transform(
                apiService.getSpecification()
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

    override suspend fun getCheckHotCerNoResult(
        req: GetCheckHotCerNoResultReq
    ): ApiResponse<Boolean> =
        withContext(ioDispatcher) {

            val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

            val resp = HttpResponseTransfer.transform(
                apiService.getCheckHotCerNoResult(authToken, req)
            )

            when(resp){
                is HttpResponse.Success -> {
                    if (resp.payload.code == "0") {
                        ApiResponse.Success(resp.payload.data.boolean)
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

    override suspend fun getCheckVehicleNoResult(
        req: GetCheckVehicleNoResultReq
    ): ApiResponse<Int> = withContext(ioDispatcher) {

        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val resp = HttpResponseTransfer.transform(
            apiService.getCheckVehicleNoResult(authToken, req)
        )

        when(resp) {
            is HttpResponse.Success -> {
                // 9999才是成功
                if (resp.payload.code == "9999") {
                    ApiResponse.Success(
                        resp.payload.data.verifyID
                    )
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

    override suspend fun uploadImageFile(
        file: File
    ): ApiResponse<List<String>> = withContext(ioDispatcher) {

        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val req = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(
            "upload",
            "${file.absolutePath}.${file.extension}",
            req
        )

        val response = HttpResponseTransfer.transform(
            apiService.uploadFile(authToken, body)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    ApiResponse.Success(
                        response.payload.data
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

    override suspend fun uploadPdfFile(
        file: File
    ): ApiResponse<List<String>> = withContext(ioDispatcher) {

        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val req = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(
            "upload",
            "${file.absolutePath}.${file.extension}",
            req
        )

        val response = HttpResponseTransfer.transform(
            apiService.uploadFile(authToken, body)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    ApiResponse.Success(
                        response.payload.data
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

    override suspend fun uploadVideoFile(
        file: File
    ): ApiResponse<List<String>> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val req = file.asRequestBody("video/mp4".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(
            "upload",
            "${file.absolutePath}.${file.extension}",
            req
        )

        val response = HttpResponseTransfer.transform(
            apiService.uploadFile(authToken, body)
        )

        when(response) {
            is HttpResponse.Success -> {
                if (response.payload.code == "0") {
                    ApiResponse.Success(
                        response.payload.data
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

    override suspend fun uploadMultiImages(
        fileList: List<File>
    ): ApiResponse<List<String>> = withContext(ioDispatcher) {

        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        val reqList = mutableListOf<MultipartBody.Part>()

        fileList.forEach { file ->
            val body = file.asRequestBody("image/*".toMediaTypeOrNull())
            reqList.add(
                MultipartBody.Part.createFormData(
                    "upload",
                    "${file.absolutePath}.${file.extension}",
                    body
                )
            )
        }

        val resp = HttpResponseTransfer.transform(
            apiService.uploadMultiFiles(authToken, reqList)
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0") {
                    ApiResponse.Success(
                        resp.payload.data
                    )
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

    override suspend fun getCarEquippedTags(): ApiResponse<GetCarEquippedTagsResp> = withContext(ioDispatcher) {
        val resp = HttpResponseTransfer.transform(
            apiService.getCarEquippedTags()
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0") {
                    ApiResponse.Success(
                        resp.payload.data
                    )
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

    override suspend fun getCarFeatureTags(): ApiResponse<GetCarFeatureResp> = withContext(ioDispatcher) {

        val resp = HttpResponseTransfer.transform(
            apiService.getCarFeature()
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


    override suspend fun getCertificateTypeTags(): ApiResponse<GetCertificateTypesResp> = withContext(ioDispatcher) {

        val resp = HttpResponseTransfer.transform(
            apiService.getCertificateTypes()
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

    override suspend fun getContactPersonList(
        memberId: Int
    ): ApiResponse<GetContactPersonListResp> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val resp = HttpResponseTransfer.transform(
            apiService.getContactPersonList(authToken, memberId)
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

    override suspend fun addContactPerson(
        req: AddContactPersonReq
    ): ApiResponse<Boolean> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val resp = HttpResponseTransfer.transform(
            apiService.addContactPerson(authToken, req)
        )

        when(resp) {
            is HttpResponse.Success -> {
                if (resp.payload.code == "0"){
                    ApiResponse.Success(true)
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

    override suspend fun getCountryAndDistrictList(

    ): ApiResponse<GetCountryAndDistrictListResp> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val resp = HttpResponseTransfer.transform(
            apiService.getCountryAndDistrictList(authToken)
        )

        when(resp) {
            is HttpResponse.Success -> {
                ApiResponse.Success(resp.payload.data)
            }
            is HttpResponse.Failure -> {
                ApiResponse.NetworkError(
                    statusCode = resp.statusCode,
                    message = resp.httpMessage
                )
            }
        }
    }

    override suspend fun getEditorTemplate(): ApiResponse<List<GetEditorTemplateItem>> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val memberId = AccountInfoManager.getMemberInfo().memberId
        val resp = HttpResponseTransfer.transform(
            apiService.getEditorTemplate(authToken, memberId)
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

    override suspend fun addCar(
        req: AddCarReq
    ): ApiResponse<AddCarResp> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val memberId = AccountInfoManager.getMemberInfo().memberId
        req.apply {
            MemberID = memberId
            CreateUserID = memberId
        }
        val resp = HttpResponseTransfer.transform(
            apiService.addCar(authToken, req)
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

    override suspend fun addCarAndPutOnAdvertisingBoard(
        req: AddCarAndPutOnAdvertisingBoardReq
    ): ApiResponse<AddCarAndPutOnAdvertisingBoardResp> = withContext(ioDispatcher) {
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken
        val memberId = AccountInfoManager.getMemberInfo().memberId
        req.apply {
            MemberID = memberId
            CreateUserID = memberId
        }
        val resp = HttpResponseTransfer.transform(
            apiService.addCarAndPutOnAdvertisingBoard(authToken, req)
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

    override suspend fun getTagsConvertResult(
        req: List<TagsConvertReqItem>
    ): ApiResponse<List<TagsConvertRespItem>> = withContext(ioDispatcher) {
        val resp = HttpResponseTransfer.transform(
            apiService.getTagsConvertResult(req)
        )

        when(resp) {
            is HttpResponse.Success -> {
                ApiResponse.Success(resp.payload.data)
            }
            is HttpResponse.Failure -> {
                ApiResponse.NetworkError(
                    statusCode = resp.statusCode,
                    message = resp.httpMessage
                )
            }
        }
    }

    override suspend fun editCar(req: EditCarReq): ApiResponse<EditCarResp> = withContext(ioDispatcher) {
        val memberId = AccountInfoManager.getMemberInfo().memberId
        val authToken = AccountInfoManager.checkToken(updateTokenEvent).authorizationToken

        req.apply {
            MemberID = memberId
            CreateUserID = memberId
        }
        val resp = HttpResponseTransfer.transform(
            apiService.editCar(
                authToken = authToken,
                req = req
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

}