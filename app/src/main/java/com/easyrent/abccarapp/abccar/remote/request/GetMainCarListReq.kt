package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class GetMainCarListReq(
    @SerializedName("MemberID") var memberId: Int = 0,
    @SerializedName("Event") val event: Int,
    @SerializedName("PageSize") val pageSize: Int,
    @SerializedName("PageIndex") val pageIndex: Int
)
