package com.easyrent.abccarapp.abccar.remote.request

data class UpdateMemberCarsStatusReq(
    val CarID: List<Int>,
    var MemberID: Int = 0,
    val PullReason: Int,
    val Status: Int
)