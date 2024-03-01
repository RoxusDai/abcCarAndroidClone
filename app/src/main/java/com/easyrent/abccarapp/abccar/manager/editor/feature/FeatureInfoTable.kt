package com.easyrent.abccarapp.abccar.manager.editor.feature

data class FeatureInfoTable(
    var isInit: Boolean = false,
    val featureTitle: String = "",
    val featureTagsSum: Int = 0,
    val ensureTagsSum: Int = 0,
    val certificateList: List<CertificateItem> = mutableListOf(),
    val templateContent: String = ""
)
