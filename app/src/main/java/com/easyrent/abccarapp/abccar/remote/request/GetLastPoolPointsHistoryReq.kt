package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class GetLastPoolPointsHistoryReq(
    @SerializedName("MemberID") val memberID: Int
)