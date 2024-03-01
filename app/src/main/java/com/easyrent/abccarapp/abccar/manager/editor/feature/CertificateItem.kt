package com.easyrent.abccarapp.abccar.manager.editor.feature

import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem

data class CertificateItem(
    var url: String = "",
    var type: GetCertificateTypesItem = GetCertificateTypesItem(-1, "請選擇檔案"),
)
