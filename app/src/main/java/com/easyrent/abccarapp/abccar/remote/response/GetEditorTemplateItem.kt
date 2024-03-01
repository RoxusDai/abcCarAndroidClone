package com.easyrent.abccarapp.abccar.remote.response


data class GetEditorTemplateItem(
    val memberID: Int,
    val templateCode: String,
    val templateContent: String,
    val templateType: Int
)