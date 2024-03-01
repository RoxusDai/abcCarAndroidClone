package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class AddContactPersonReq(
    @SerializedName("Address") val address: String,
    @SerializedName("AreaCode") val areaCode: String,
    @SerializedName("CellPhone") val cellPhone: String,
    @SerializedName("CountryName") val countryName: String,
    @SerializedName("CreateDate") val createDate: String,
    @SerializedName("DistrictName") val districtName: String,
    @SerializedName("Ext") val ext: String,
    @SerializedName("LineID") val lineID: String,
    @SerializedName("LineUrl") val lineUrl: String,
    @SerializedName("MemberID") val memberID: Int,
    @SerializedName("ModifyDate") val modifyDate: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Telephone") val telephone: String
)