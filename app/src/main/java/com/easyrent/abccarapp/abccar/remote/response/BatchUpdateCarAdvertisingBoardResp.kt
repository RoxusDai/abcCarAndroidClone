package com.easyrent.abccarapp.abccar.remote.response

data class BatchUpdateCarAdvertisingBoardResp(
    val status : Int,
    val successCarId : List<Int>,
    val orderGuid: String,
    val orderPay: Boolean
)
