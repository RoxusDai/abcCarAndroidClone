package com.easyrent.abccarapp.abccar.manager.editor.basic

import com.easyrent.abccarapp.abccar.manager.editor.colum.CarDescInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarInfoPair
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarSpecificationInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.DatePair
import com.easyrent.abccarapp.abccar.manager.editor.colum.FileInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.PriceInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.UnqualifiedResponse
import com.easyrent.abccarapp.abccar.remote.response.ColorType

class BasicTableManager {

    private var basicTable = BasicInfoTable()

    // 獨立CarId設定，避免填表過程被刷新
    private var carId: Int = 0

    fun setCarId(id: Int) {
        carId = id
    }

    fun getCarId() = carId

    fun getBrandInfo() = basicTable.brand
    fun getSeriesInfo() = basicTable.series
    fun getCategory() = basicTable.category
    fun getManufacture() = basicTable.manufacture
    fun getSpecification() = basicTable.specification
    fun getDescInfo() = basicTable.descInfo
    fun getLicenseInfo() = basicTable.licenseInfo
    fun getPriceInfo() = basicTable.priceInfo

    // 依序檢查欄位狀態，Specification各參數是API帶入，所以合併為一個狀態
    fun checkColumUnqualified(): List<UnqualifiedResponse> {
        return mutableListOf<UnqualifiedResponse>().apply {
            // 廠牌
            if (getBrandInfo().isUnqualified()) add(UnqualifiedResponse.Brand)
            // 車型
            if (getSeriesInfo().isUnqualified()) add(UnqualifiedResponse.Series)
            // 車款
            if (getCategory().isUnqualified()) add(UnqualifiedResponse.Category)
            // 年份
            if (getManufacture().isUnqualified()) add(UnqualifiedResponse.Manufacture)
            // 顏色
            if (getDescInfo().isColorUnqualified()) add(UnqualifiedResponse.Color)
            // 里程
            if (getDescInfo().isMileageUnqualified()) add(UnqualifiedResponse.Mileage)
            // 平輸車文件檢查
            if (isImportDocUnqualified()) add(UnqualifiedResponse.ImportDoc)

            // 基本價格不可為0
            if (checkBasePriceZeroUnqualified()) add(UnqualifiedResponse.BasePriceZero)
            // 檢查基本價格不可高於9999.9
            if (isBasePriceOverMaxUnqualified()) add(UnqualifiedResponse.BasePriceOverMax)
            // 檢查最高價格不可高於9999.9
            if (isCeilingPriceOverMaxUnqualified()) add(UnqualifiedResponse.CeilingPriceOverMax)
            // 檢查最高價格是否超過基底兩成
            if (checkCeilingRangeUnqualified()) add(UnqualifiedResponse.CeilingPriceOverRange)
            // 檢查最高價格是否低於最低價格
            if (isCeilingIsBelowBase()) add(UnqualifiedResponse.CeilingPriceBelow)

            // 檢查車身號碼
            if (checkVehicleNo()) add(UnqualifiedResponse.VehicleNo)
            // 車身號碼錯誤三次以上，強制上傳行照
            if (isVehicleNoUnqualified()) add((UnqualifiedResponse.VehicleRegistration))
        }
    }

    private fun checkVehicleNo(): Boolean {
        //  新增模式、不是平輸車、未填寫車身號碼
        return basicTable.carId == 0 && !getLicenseInfo().isImport && getLicenseInfo().vehicleNo.isBlank()
    }

    // 如果是平輸車，則必須上傳證明文件
    private fun isImportDocUnqualified(): Boolean {
        return getLicenseInfo().isImport && getLicenseInfo().importDocFileUrl.url.isBlank()
    }

    // 不是平輸車 且 車身號碼錯誤二次以上 且 行照URL不存在
    fun isVehicleNoUnqualified(): Boolean {
        val isLicenseExist = getLicenseInfo().licenseFileUrl.url.isBlank()
        val needUpload = getLicenseInfo().vehicleNoErrorCount > 1
        return !getLicenseInfo().isImport && needUpload && isLicenseExist
    }

    fun needCheckVehicleNo(): Boolean {
        val errorCount = getLicenseInfo().vehicleNoErrorCount < 2
        return isNormalSource() && errorCount
    }

    fun isImportSource(): Boolean = getLicenseInfo().isImport
    fun isNormalSource(): Boolean = !getLicenseInfo().isImport

    fun isPriceRangeMode(): Boolean {
        return getPriceInfo().basePrice >= 150 && getLicenseInfo().isImport
    }

    // 基底價格不可為０
    private fun checkBasePriceZeroUnqualified(): Boolean {
        return getPriceInfo().isBaseZero()
    }

    // 基底價格不可高於9999.9
    private fun isBasePriceOverMaxUnqualified(): Boolean {
        return getPriceInfo().isBaseOverMax()
    }

    // 最高價格不可高於9999.9
    private fun isCeilingPriceOverMaxUnqualified(): Boolean {
        // 如果不是平輸車則返回False
        if (!getLicenseInfo().isImport) return false

        return getPriceInfo().isCeilingOverMax() // 檢查基底價格
    }

    // 檢查最高價格是否低於最低價格
    private fun isCeilingIsBelowBase(): Boolean {
        // 如果不是平輸車則返回False
        if (!getLicenseInfo().isImport) return false

        return if (getPriceInfo().basePrice >= 150) {
            getPriceInfo().isCeilingBelowBase()
        } else {
            false
        }
    }

    // 檢查最高售價是否超過基底售價兩成
    private fun checkCeilingRangeUnqualified(): Boolean {
        // 如果不是平輸車則返回False
        if (!getLicenseInfo().isImport) return false

        return if (getPriceInfo().basePrice >= 150) {
            getPriceInfo().isCeilingOverRange()
        } else {
            false
        }
    }

    fun getTable(): BasicInfoTable {
        return BasicInfoTable(
            carId = getCarId(),
            brand = basicTable.brand,
            series = basicTable.series,
            category = basicTable.category,
            manufacture = basicTable.manufacture,
            specification = basicTable.specification,
            descInfo = basicTable.descInfo,
            priceInfo = basicTable.priceInfo,
            licenseInfo = basicTable.licenseInfo
        )
    }

    // 設定廠牌，後續設定全數清空
    fun setBrandInfo(
        id: Int,
        name: String
    ) {
        basicTable = BasicInfoTable(
            brand = CarInfoPair(id = id, name = name)
        )
    }

    // 設定車型
    fun setSeriesInfo(
        id: Int,
        name: String
    ) {
        basicTable = basicTable.copy(
            series = CarInfoPair(id = id, name = name),
            category = CarInfoPair(),
            manufacture = DatePair(),
            specification = CarSpecificationInfo(),
            descInfo = CarDescInfo()
        )
    }

    // 設定車款
    fun setCategory(id: Int, name: String) {
        basicTable = basicTable.copy(
            category = CarInfoPair(id = id, name = name),
            specification = CarSpecificationInfo(),
            descInfo = CarDescInfo()
        )
    }

    fun setManufactureYear(year: Int) {
        val manufacture = basicTable.manufacture.copy(
            year = year
        )
        basicTable = basicTable.copy(
            manufacture = manufacture,
            specification = CarSpecificationInfo(),
            descInfo = CarDescInfo()
        )
    }

    // 月份沒有條件連動
    fun setManufactureMonth(month: Int) {
        val manufacture = basicTable.manufacture.copy(
            month = month
        )
        basicTable = basicTable.copy(
            manufacture = manufacture,
            specification = CarSpecificationInfo(),
            descInfo = CarDescInfo()
        )
    }

    fun setSpecification(
        spec: CarSpecificationInfo
    ) {
        basicTable = basicTable.copy(
            specification = spec,
        )
    }

    fun setColor(
        color: ColorType
    ) {
        val desc = basicTable.descInfo.copy(
            colorType = color
        )

        basicTable = basicTable.copy(
            descInfo = desc
        )
    }

    fun setMileage(
        mileage: String
    ) {

        val desc = basicTable.descInfo.copy(
            mileage = mileage
        )

        basicTable = basicTable.copy(
            descInfo = desc
        )
    }

    fun setIsImport(status: Boolean) {
        val licenseInfo = basicTable.licenseInfo.copy(
            isImport = status
        )

        basicTable = basicTable.copy(
            licenseInfo = licenseInfo
        )
    }

    fun setVehicleNo(vehicleNo: String) {
        val licenseInfo = basicTable.licenseInfo.copy(
            vehicleNo = vehicleNo
        )
        basicTable = basicTable.copy(
            licenseInfo = licenseInfo
        )
    }

    fun setVerifyId(id: Int) {
        val licenseInfo = basicTable.licenseInfo.copy(
            verifyID = id
        )
        basicTable = basicTable.copy(
            licenseInfo = licenseInfo
        )
    }

    fun setLicenseFile(url: String, extension: String) {
        val info = basicTable.licenseInfo.copy(
            importDocFileUrl = FileInfo(), // 清空
            licenseFileUrl = FileInfo(url, extension)
        )

        basicTable = basicTable.copy(
            licenseInfo = info
        )
    }

    fun setImportDocFile(url: String, extension: String) {
        val info = basicTable.licenseInfo.copy(
            importDocFileUrl = FileInfo(url, extension),
            licenseFileUrl = FileInfo() // 清空
        )

        basicTable = basicTable.copy(
            licenseInfo = info
        )
    }

    fun clearFiles() {
        basicTable.licenseInfo.apply {
            licenseFileUrl = FileInfo()
            importDocFileUrl = FileInfo()
        }
    }

    fun setHotVerify(number: String) {
        val info = basicTable.licenseInfo.copy(
            hotVerifyId = number
        )
        basicTable = basicTable.copy(
            licenseInfo = info
        )
    }

    fun setPriceInfo(price: PriceInfo) {
        basicTable = basicTable.copy(
            priceInfo = price
        )
    }

    fun clear() {
        basicTable = BasicInfoTable()
    }

    fun setTable(table: BasicInfoTable) {
        basicTable = table
    }

}