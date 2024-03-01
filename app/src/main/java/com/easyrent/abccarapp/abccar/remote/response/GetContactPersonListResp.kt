package com.easyrent.abccarapp.abccar.remote.response

class GetContactPersonListResp : ArrayList<GetContactPersonListRespItem>()

data class GetContactPersonListRespItem(
    var isChecked: Boolean = false,
    val address: String,
    val cellPhone: String,
    val id: Int,
    val lineID: String,
    val lineUrl: String,
    val name: String,
    val telephone: String,
    val virtualMobile: String
)