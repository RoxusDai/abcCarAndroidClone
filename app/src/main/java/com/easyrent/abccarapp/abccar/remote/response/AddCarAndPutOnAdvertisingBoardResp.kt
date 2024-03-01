package com.easyrent.abccarapp.abccar.remote.response

data class AddCarAndPutOnAdvertisingBoardResp(
    val carID: Int,
    val orderItemID: Int,
    val resultCode: String,
    val resultMessage: String,
    val vehicleNoCheckExtra: Int,
    val vehicleNoCheckStatus: Int
)