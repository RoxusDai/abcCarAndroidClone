package com.easyrent.abccarapp.abccar.source

data class TokenPackage(
    val code :Int = 0,
    val message : String = "",
    var originalToken: String = "",
    var authorizationToken: String = ""
)
