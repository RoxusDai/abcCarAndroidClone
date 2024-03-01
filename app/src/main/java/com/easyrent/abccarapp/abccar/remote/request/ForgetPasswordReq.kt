package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class ForgetPasswordReq(
    @SerializedName("cellphone") val cellphone: String,
)
