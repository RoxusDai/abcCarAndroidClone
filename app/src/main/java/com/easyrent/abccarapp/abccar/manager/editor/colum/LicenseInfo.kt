package com.easyrent.abccarapp.abccar.manager.editor.colum

data class LicenseInfo(
    val isImport: Boolean = false,
    val vehicleNo: String = "",
    var vehicleNoErrorCount: Int = 0,
    val hotVerifyId: String = "",

    val verifyID: Int = 0,

    var licenseFileUrl: FileInfo = FileInfo(),
    var importDocFileUrl: FileInfo = FileInfo()
) : ColumCheckInterface {
    override fun isUnqualified(): Boolean {
        return if (isImport) {
            // 平輸車必須要提供有效的進口報關資料或合格證明
            // 如果URL為空，則代表尚未選擇
            importDocFileUrl.url.isBlank()
        } else {
            // 非平輸車則要提供車身號碼
            vehicleNo.isBlank()
        }
    }
}

data class FileInfo(
    val url: String = "",
    val extension: String = ""
)
