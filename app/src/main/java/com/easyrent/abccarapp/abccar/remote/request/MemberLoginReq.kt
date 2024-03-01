package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

/**
 *  MemberLogin Request data class
 */
data class MemberLoginReq(
    @SerializedName("LoginID") val loginId: String,
    @SerializedName("Password") val password: String
)
