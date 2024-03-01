package com.easyrent.abccarapp.abccar.manager.editor.basic

import com.easyrent.abccarapp.abccar.manager.editor.colum.CarDescInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarInfoPair
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarSpecificationInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.DatePair
import com.easyrent.abccarapp.abccar.manager.editor.colum.LicenseInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.PriceInfo

data class BasicInfoTable(
    var isInit: Boolean = false,
    val carId: Int = 0,
    val brand: CarInfoPair = CarInfoPair(),
    val series: CarInfoPair = CarInfoPair(),
    val category: CarInfoPair = CarInfoPair(),
    val manufacture: DatePair = DatePair(),
    val specification: CarSpecificationInfo = CarSpecificationInfo(),
    val descInfo: CarDescInfo = CarDescInfo(),
    val priceInfo: PriceInfo = PriceInfo(),
    val licenseInfo: LicenseInfo = LicenseInfo(),
)
