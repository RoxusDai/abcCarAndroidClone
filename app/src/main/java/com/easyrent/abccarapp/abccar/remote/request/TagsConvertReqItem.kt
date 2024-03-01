package com.easyrent.abccarapp.abccar.remote.request

data class TagsConvertReqItem(
    val ParseKey: String, // 自定義名稱
    val DecimalNumber: Int, // 總和
    val Qty: Int // 集合長度
)