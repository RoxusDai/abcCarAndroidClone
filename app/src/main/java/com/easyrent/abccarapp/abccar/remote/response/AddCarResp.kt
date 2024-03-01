package com.easyrent.abccarapp.abccar.remote.response

data class AddCarResp(
    val carID: Int,
    val resultCode: String,
    val resultMessage: String,
    val vehicleNoCheckExtra: Int,
    val vehicleNoCheckStatus: Int
)