package com.easyrent.abccarapp.abccar.remote.response

import com.google.gson.annotations.SerializedName


/**
 *  API Response格式
 */
data class BaseResponse<T>(
    val success: Boolean,
    val code: String, // API 回應的狀態碼，不是Https status code
    val message: String,
    @SerializedName("datas") val data: T,
    val authorizationToken: String?,
    val originalToken: String?,
)