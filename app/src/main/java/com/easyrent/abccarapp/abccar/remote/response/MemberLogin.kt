package com.easyrent.abccarapp.abccar.remote.response

import com.google.gson.annotations.SerializedName

data class GetMemberLoginResp(
    @SerializedName("memberInfo") val memberInfoResp: GetMemberResp
)

data class UploadFile(
    val memberFileType: Int,
    val taxID: String,
    val url: String
)