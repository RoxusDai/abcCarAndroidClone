package com.easyrent.abccarapp.abccar.remote.response

data class TagsConvertRespItem(
    val decimalNumber: Int, // 總數
    val onOff: List<Int>,
    val parseKey: String
)