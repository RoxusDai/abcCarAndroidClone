package com.easyrent.abccarapp.abccar.manager.editor.colum

sealed class UnqualifiedResponse(
    val desc: String
) {
    data object Brand : UnqualifiedResponse("請選擇廠牌")
    data object Series : UnqualifiedResponse("請選擇車型")
    data object Category : UnqualifiedResponse("請選擇車款")
    data object Manufacture : UnqualifiedResponse("請選擇出廠日期")
    data object Mileage : UnqualifiedResponse("請輸入里程")
    data object Color : UnqualifiedResponse("請選擇顏色")
    data object VehicleRegistration : UnqualifiedResponse("因車身號碼驗證錯誤達兩次，請選擇行照檔案")
    data object ImportDoc : UnqualifiedResponse("請選擇有效的進口報關資料或交通部合格證明")
    data object VehicleNo : UnqualifiedResponse("請輸入車身號碼")
    data object BasePriceZero : UnqualifiedResponse("請輸入售價")
    data object BasePriceOverMax: UnqualifiedResponse("基本售價不可高於9999.9萬")
    data object CeilingPriceOverMax: UnqualifiedResponse("最高售價不可超過9999.9萬")
    data object CeilingPriceOverRange: UnqualifiedResponse("最高售價不可高於基底售價兩成")
    data object CeilingPriceBelow: UnqualifiedResponse("最高售價不可低於基底售價")
}
