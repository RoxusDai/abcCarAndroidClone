package com.easyrent.abccarapp.abccar.remote.response

class GetCertificateTypesResp : ArrayList<GetCertificateTypesItem>()

data class GetCertificateTypesItem(
    val id: Int,
    val name: String
)