package com.easyrent.abccarapp.abccar.remote.response

data class GetLastPoolPointsHistoryResp(
    val discountPoints: Int,
    val discountTotalPoints: Int,
    val holdPoints: Int,
    val holdTotalPoints: Int,
    val id: Int,
    val poolPoints: Int,
    val poolTotalPoints: Int,
    val realPoolPoints: Int,
    val success: Boolean,
    val usePoints: Int,
    val useTotalPoints: Int
)