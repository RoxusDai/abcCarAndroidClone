package com.easyrent.abccarapp.abccar.remote.response

data class ForgetPasswordResp(
    val success : Boolean,
    val code : Int,
    val message : String,
    val datas : List<dataelement>,
    val originalToken : String,
    val authorizationToken : String
)

data class dataelement(
    val provider :String
)