package com.easyrent.abccarapp.abccar.remote


import com.easyrent.abccarapp.abccar.remote.request.AddCarAndPutOnAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.AddCarReq
import com.easyrent.abccarapp.abccar.remote.request.AddContactPersonReq
import com.easyrent.abccarapp.abccar.remote.request.BatchUpdateCarAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.EditCarReq
import com.easyrent.abccarapp.abccar.remote.request.EditPasswordReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarCategoryByManufactureReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarModelOtherInputDataByCategoryIdReq
import com.easyrent.abccarapp.abccar.remote.request.GetMainCarListReq
import com.easyrent.abccarapp.abccar.remote.request.GetManufactureYearByCarSeriesReq
import com.easyrent.abccarapp.abccar.remote.request.MemberLoginReq
import com.easyrent.abccarapp.abccar.remote.response.BaseResponse
import com.easyrent.abccarapp.abccar.remote.response.EditCarListResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarCategoryByManufactureResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarClassificationResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarModelOtherInputDataByCategoryIdResp
import com.easyrent.abccarapp.abccar.remote.request.GetCheckHotCerNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckVehicleNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetEditCarInfoReq
import com.easyrent.abccarapp.abccar.remote.request.GetLastPoolPointsHistoryReq
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.request.UpdateMemberCarsStatusReq
import com.easyrent.abccarapp.abccar.remote.response.AddCarAndPutOnAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.remote.response.AddCarResp
import com.easyrent.abccarapp.abccar.remote.response.BatchUpdateCarAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.remote.response.EditCarResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarFeatureResp
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesResp
import com.easyrent.abccarapp.abccar.remote.response.GetCheckHotCerNoResultResp
import com.easyrent.abccarapp.abccar.remote.response.GetCheckVehicleNoResultResp
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp
import com.easyrent.abccarapp.abccar.remote.response.GetCountryAndDistrictListResp
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.remote.response.GetEditorTemplateItem
import com.easyrent.abccarapp.abccar.remote.response.GetLastPoolPointsHistoryResp
import com.easyrent.abccarapp.abccar.remote.response.GetManufactureYearByCarSeriesResp
import com.easyrent.abccarapp.abccar.remote.response.GetMemberResp
import com.easyrent.abccarapp.abccar.remote.response.GetMemberLoginResp
import com.easyrent.abccarapp.abccar.remote.response.GetSpecificationResp
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("member/EditMemberPassword")
    fun editMemberPassword(
        @Header("AuthorizationToken") authToken: String,
        @Body req: EditPasswordReq
    ): Call<BaseResponse<Any>>

    @POST("member/Login")
    fun memberLogin(
        @Body req: MemberLoginReq
    ): Call<BaseResponse<GetMemberLoginResp>>

    @GET("member/RefreshAuthorizationToken")
    fun refreshAuthorizationToken(
        @Header("OriginalToken") originalToken: String
    ): Call<BaseResponse<Any>>

    @POST("member/GetMember")
    fun getMemberInfo(
        @Header("AuthorizationToken") authToken: String
    ): Call<BaseResponse<GetMemberResp>>

    @POST("order/GetLastPoolPointsHistory")
    fun getPointsHistory(
        @Header("AuthorizationToken") authToken: String,
        @Body req: GetLastPoolPointsHistoryReq
    ): Call<BaseResponse<GetLastPoolPointsHistoryResp>>

    @POST("car/GetEditCarInfo")
    fun getEditCarInfo(
        @Header("AuthorizationToken") authToken: String,
        @Body req: GetEditCarInfoReq
    ): Call<BaseResponse<GetEditCarInfoResp>>

    @POST("car/UpdateMemberCarsStatus")
    fun updateMemberCarsStatus(
        @Header("AuthorizationToken") authToken: String,
        @Body req: UpdateMemberCarsStatusReq
    ): Call<BaseResponse<Int>>

    @POST("car/BatchUpdateCarAdvertisingBoard")
    fun batchUpdateCarAdvertisingBoard(
        @Header("AuthorizationToken") authToken: String,
        @Body req: BatchUpdateCarAdvertisingBoardReq
    ):Call<BaseResponse<BatchUpdateCarAdvertisingBoardResp>>

    @POST("car/EditCar")
    fun editCar(
        @Header("AuthorizationToken") authToken: String,
        @Body req: EditCarReq
    ): Call<BaseResponse<EditCarResp>>

    @POST("car/GetEditCarList")
    fun getEditCarList(
        @Header("AuthorizationToken") authToken: String,
        @Body req: GetMainCarListReq
    ): Call<BaseResponse<EditCarListResp>>

    // 選擇廠牌、選擇車型
    @POST("car/GetCarBrandSeriesCategory")
    fun getCarBrandSeriesCategory(
        @Body req: GetCarBrandSeriesCategoryReq
    ): Call<BaseResponse<GetCarClassificationResp>>

    // 選擇出廠年月
    @POST("car/GetManufactureYearByCarSeries")
    fun getManufactureYearByCarSeries(
        @Body req: GetManufactureYearByCarSeriesReq
    ): Call<BaseResponse<GetManufactureYearByCarSeriesResp>>

    // 選擇車款
    @POST("car/GetCarCategoryByManufacture")
    fun getCarCategoryByManufacture(
        @Body req: GetCarCategoryByManufactureReq
    ): Call<BaseResponse<GetCarCategoryByManufactureResp>>

    // 自動帶入剩下欄位(傳動、變速、燃油種類、排氣量、乘坐人數)
    @POST("car/GetCarModelOtherInputDataByCategoryID")
    fun getCarModelOtherInputDataByCategoryId(
        @Body req: GetCarModelOtherInputDataByCategoryIdReq
    ): Call<BaseResponse<GetCarModelOtherInputDataByCategoryIdResp>>

    // 檢查Hot認證編號
    @POST("car/CheckHotcerno")
    fun getCheckHotCerNoResult(
        @Header("AuthorizationToken") authToken: String,
        @Body req: GetCheckHotCerNoResultReq
    ): Call<BaseResponse<GetCheckHotCerNoResultResp>>

    // 檢查車身號碼
    @POST("car/CheckVehicleNo")
    fun getCheckVehicleNoResult(
        @Header("AuthorizationToken") authToken: String,
        @Body req: GetCheckVehicleNoResultReq
    ): Call<BaseResponse<GetCheckVehicleNoResultResp>>

    @Multipart
    @POST("Upload/UploadFiles")
    fun uploadFile(
        @Header("AuthorizationToken") authToken: String,
        @Part req: MultipartBody.Part
    ): Call<BaseResponse<List<String>>>

    @Multipart
    @POST("Upload/UploadFiles")
    fun uploadMultiFiles(
        @Header("AuthorizationToken") authToken: String,
        @Part req: List<MultipartBody.Part>
    ): Call<BaseResponse<List<String>>>

    // 聯絡人清單
    @GET("member/GetContactPersonList")
    fun getContactPersonList(
        @Header("AuthorizationToken") authToken: String,
        @Query("memberid") memberid: Int
    ): Call<BaseResponse<GetContactPersonListResp>>

    @POST("member/AddContactPerson")
    fun addContactPerson(
        @Header("AuthorizationToken") authToken: String,
        @Body req: AddContactPersonReq
    ): Call<BaseResponse<Boolean>>

    @GET("member/GetEditorTemplate")
    fun getEditorTemplate(
        @Header("AuthorizationToken") authToken: String,
        @Query("memberid") memberId: Int
    ): Call<BaseResponse<List<GetEditorTemplateItem>>>

    @POST("car/addcar")
    fun addCar(
        @Header("AuthorizationToken") authToken: String,
        @Body req: AddCarReq
    ): Call<BaseResponse<AddCarResp>>

    @POST("car/AddCarAndPutOnAdvertisingBoard")
    fun addCarAndPutOnAdvertisingBoard(
        @Header("AuthorizationToken") authToken: String,
        @Body req: AddCarAndPutOnAdvertisingBoardReq
    ): Call<BaseResponse<AddCarAndPutOnAdvertisingBoardResp>>

    // 取得清單
    @GET("car/GetSpecification")
    fun getSpecification(): Call<BaseResponse<GetSpecificationResp>>

    @GET("car/GetEquipped")
    fun getCarEquippedTags(): Call<BaseResponse<GetCarEquippedTagsResp>>

    @GET("car/GetCarFeature")
    fun getCarFeature(): Call<BaseResponse<GetCarFeatureResp>>

    @GET("car/GetCertificateTypes")
    fun getCertificateTypes(): Call<BaseResponse<GetCertificateTypesResp>>

    @GET("member/GetCountryAndDistrictList")
    fun getCountryAndDistrictList(
        @Header("AuthorizationToken") token: String
    ): Call<BaseResponse<GetCountryAndDistrictListResp>>

    @POST("Utilities/Tens2Two")
    fun getTagsConvertResult(
        @Body req: List<TagsConvertReqItem>
    ): Call<BaseResponse<List<TagsConvertRespItem>>>
}