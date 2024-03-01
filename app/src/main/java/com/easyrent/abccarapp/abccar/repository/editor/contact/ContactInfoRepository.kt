package com.easyrent.abccarapp.abccar.repository.editor.contact

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.AddCarAndPutOnAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.AddCarReq
import com.easyrent.abccarapp.abccar.remote.request.AddContactPersonReq
import com.easyrent.abccarapp.abccar.remote.request.EditCarReq
import com.easyrent.abccarapp.abccar.remote.response.AddCarAndPutOnAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.remote.response.AddCarResp
import com.easyrent.abccarapp.abccar.remote.response.BaseResponse
import com.easyrent.abccarapp.abccar.remote.response.EditCarResp
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp
import com.easyrent.abccarapp.abccar.remote.response.GetCountryAndDistrictListResp
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSource

class ContactInfoRepository(
    val source: EditorInfoSource
) {
    suspend fun getContactPersonList(memberId: Int): ApiResponse<GetContactPersonListResp> {
        return source.getContactPersonList(memberId)
    }

    suspend fun addContactPerson(req: AddContactPersonReq): ApiResponse<Boolean> {
        return source.addContactPerson(req)
    }

    suspend fun getCountyAndDistrictList(): ApiResponse<GetCountryAndDistrictListResp> {
        return source.getCountryAndDistrictList()
    }

    suspend fun addCar(req: AddCarReq): ApiResponse<AddCarResp> {
        return source.addCar(req)
    }

    suspend fun addCarAndPutOnAdvertisingBoard(
        req: AddCarAndPutOnAdvertisingBoardReq
    ): ApiResponse<AddCarAndPutOnAdvertisingBoardResp> {
        return source.addCarAndPutOnAdvertisingBoard(req)
    }

    suspend fun editCar(
        req: EditCarReq
    ): ApiResponse<EditCarResp> {
        return source.editCar(req)
    }

}