package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class GetCheckHotCerNoResultReq(
    @SerializedName("BrandID") val brandID: Int,
    @SerializedName("HOTCERNO") val hotCerNo: String,
    @SerializedName("HOTMemberID") val hotMemberId: String,
    @SerializedName("ManufactureDate") val manufactureDate: String
)