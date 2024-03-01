package com.easyrent.abccarapp.abccar.viewmodel.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicTableManager
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarSpecificationInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.UnqualifiedResponse
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarCategoryByManufactureReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarModelOtherInputDataByCategoryIdReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckHotCerNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetCheckVehicleNoResultReq
import com.easyrent.abccarapp.abccar.remote.request.GetManufactureYearByCarSeriesReq
import com.easyrent.abccarapp.abccar.repository.editor.basic.EditorBasicInfoRepository
import com.easyrent.abccarapp.abccar.repository.editor.basic.EditorBasicInfoRepositoryImp
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.manager.editor.basic.SpecTypesManager
import com.easyrent.abccarapp.abccar.manager.editor.colum.PriceInfo
import com.easyrent.abccarapp.abccar.remote.response.CarInfo
import com.easyrent.abccarapp.abccar.remote.response.ColorType
import com.easyrent.abccarapp.abccar.remote.response.EngineDisplacementType
import com.easyrent.abccarapp.abccar.remote.response.GasType
import com.easyrent.abccarapp.abccar.remote.response.GearType
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.remote.response.Passenger
import com.easyrent.abccarapp.abccar.remote.response.TransmissionType
import com.easyrent.abccarapp.abccar.tools.FilesTool
import com.easyrent.abccarapp.abccar.tools.DateFormatTool
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 *  這個ViewModel負責 Step 1 的條件檢查與邏輯處理
 */

class EditorBasicInfoViewModel(
    private val repository: EditorBasicInfoRepository
) : ViewModel() {

    private val manager = BasicTableManager()
    private val specTypesManager = SpecTypesManager()

    private val filesTool = FilesTool()
    private val dateTool = DateFormatTool()

    // 刷新Table
    private val _tableLiveData: MutableLiveData<BasicInfoTable> = MutableLiveData()
    val tableLiveData: LiveData<BasicInfoTable> = _tableLiveData

    private val _statusLiveData: MutableLiveData<BasicEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<BasicEditorStatus> = _statusLiveData

    // 欄位檢查狀態
    private val _columnCheckLiveData: MutableLiveData<List<UnqualifiedResponse>> = MutableLiveData()
    val columnCheckLiveData: LiveData<List<UnqualifiedResponse>> get() = _columnCheckLiveData

    fun initSpecificationTypesManager() {
        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getSpecification()

            when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        specTypesManager.setAllTypes(it)
                        _statusLiveData.value = BasicEditorStatus.DataInitDone
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getRespMessage("Spec types error - ${resp.message}")
                }

                is ApiResponse.NetworkError -> {
                    getRespMessage("${resp.statusCode} - ${resp.message}")
                }
            }
        }
    }

    fun setTable(table: BasicInfoTable) {
        manager.setTable(table)
        refreshTable()
    }

    // 廠牌
    fun setBrand(id: Int, name: String) {
        manager.setBrandInfo(id, name)
    }

    // 車型
    fun setSeries(id: Int, name: String) {
        manager.setSeriesInfo(id, name)
    }

    // 選擇出廠年份
    fun setManufactureYear(year: Int) {
        manager.setManufactureYear(year)
        manager.setManufactureMonth(1) // 直接帶入月份
        // 取得第一筆車款，並帶入規格
        getCarCategoryAutoFill()
    }

    // 選擇月份
    fun setManufactureMonth(month: Int) {
        manager.setManufactureMonth(month)
    }

    // 選擇車款
    fun setCategory(id: Int, name: String) {
        manager.setCategory(id, name)
        getCategorySpec() // 直接帶入規格
    }

    // 傳動系統
    fun setTransmissionType(type: TransmissionType) {
        val spec = manager.getSpecification().copy(
            transmission = type
        )
        manager.setSpecification(spec)
    }

    // 變速系統
    fun setGearType(type: GearType) {
        val spec = manager.getSpecification().copy(
            gearType = type
        )
        manager.setSpecification(spec)
    }

    // 燃油種類
    fun setFuelType(type: GasType) {
        val spec = manager.getSpecification().copy(
            fuelType = type
        )
        manager.setSpecification(spec)
    }

    // 排氣量
    fun setEngineDisplacement(type: EngineDisplacementType) {
        val spec = manager.getSpecification().copy(
            engineDisplacement = type
        )
        manager.setSpecification(spec)
    }

    // 乘坐人數
    fun setPassenger(pas: Passenger) {
        val spec = manager.getSpecification().copy(
            passenger = pas
        )
        manager.setSpecification(spec)
    }

    fun setColorType(type: ColorType) {
        manager.setColor(type)
    }

    fun setVehicleNo(number: String) {
        if (number == manager.getLicenseInfo().vehicleNo) return
        manager.setVehicleNo(number)
    }

    fun setHotVerifyNo(number: String) {
        manager.setHotVerify(number)
    }

    fun setMileage(mileage: String) {
        if (mileage == manager.getDescInfo().mileage) return
        manager.setMileage(mileage)
    }

    fun setBasePrice(price: Double) {
        // 避免重複觸發
        if (price == manager.getPriceInfo().basePrice) return
        val info = manager.getPriceInfo().copy(
            basePrice = price
        )
        manager.setPriceInfo(info)
        // 不刷新Table，避免重複觸發TextWatcher的問題
    }

    fun setCeilingPrice(price: Double) {
        // 避免重複觸發
        if (price == manager.getPriceInfo().ceilingPrice) return
        // 如果是價格區間模式，填入最高價格，反之設為0.0
        val info = if (manager.isPriceRangeMode()) {
            manager.getPriceInfo().copy(ceilingPrice = price)
        } else {
            manager.getPriceInfo().copy(ceilingPrice = 0.0)
        }

        manager.setPriceInfo(info)
        // 不刷新Table，避免重複觸發TextWatcher的問題
    }

    // 設置選取檔案URI
    fun uploadLicenseFile(uri: Uri, extension: String, context: Context) {
        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val file = filesTool.parseFileToAbsPath(uri, extension, context)
            val licenseResp = when(extension) {
                "jpg" -> repository.uploadImageFile(file)
                "png" -> repository.uploadImageFile(file)
                else ->  repository.uploadPdfFile(file)
            }

            when(licenseResp) {
                is ApiResponse.Success -> {
                    licenseResp.data.let { list ->
                        if (list.isEmpty()) {
                            _statusLiveData.value = BasicEditorStatus.FileUploadError("上傳行照回應為空，請稍後再試")
                        } else {
                            val url = list.first()
                            manager.setLicenseFile(url, extension)
                            refreshTable()
                            _statusLiveData.value = BasicEditorStatus.LoadingFinish
                        }
                    }
                }
                is ApiResponse.ApiFailure -> {
                    _statusLiveData.value = BasicEditorStatus.FileUploadError(licenseResp.message ?: "行照上傳失敗")
                }

                is ApiResponse.NetworkError -> {
                    getErrorMessage(licenseResp.statusCode, licenseResp.message)
                }
            }

            file.delete()
        }

    }

    // 設置選取檔案URI
    fun uploadImportFile(uri: Uri, extension: String, context: Context) {

        viewModelScope.launch {

            _statusLiveData.value = BasicEditorStatus.Loading

            val file = filesTool.parseFileToAbsPath(uri, extension, context)
            val importDocResp = when(extension) {
                "jpg" -> repository.uploadImageFile(file)
                "png" -> repository.uploadImageFile(file)
                else ->  repository.uploadPdfFile(file)
            }

            when(importDocResp) {
                is ApiResponse.Success -> {
                    importDocResp.data.let { list ->
                        if (list.isEmpty()) {
                            _statusLiveData.value = BasicEditorStatus.FileUploadError("上傳證明資料回應為空，請稍後再試")
                        } else {
                            val url = list.first()
                            manager.setImportDocFile(url = url, file.extension)
                            refreshTable()
                            _statusLiveData.value = BasicEditorStatus.LoadingFinish
                        }
                    }
                }
                is ApiResponse.ApiFailure -> {
                    _statusLiveData.value = BasicEditorStatus.FileUploadError(importDocResp.message)
                }

                is ApiResponse.NetworkError -> {
                    getErrorMessage(importDocResp.statusCode, importDocResp.message)
                }
            }

            file.delete() // API回應後刪除暫存檔案
        }
    }

    fun clearFiles() {
        manager.clearFiles()
    }

    fun getEditCarInfo(carId: Int) {
        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getCarEditInfo(carId)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    BasicEditorStatus.InitTableInfo(resp.data)
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }
        }
    }


    fun checkPriceInfoInput() {
        val info = manager.getPriceInfo()
        val isLowerBasic = isCeilingPriceUnqualified()
        val isOverBasic = isCeilingOverBasicRange()

        _statusLiveData.value = BasicEditorStatus.PriceErrorDesc(
            isShowLowerError = isLowerBasic,
            isShowRangeError = isOverBasic,
            maxPrice = (info.basePrice * 1.2).toString()
        )
    }

    private fun isCeilingPriceUnqualified(): Boolean {
        val info = manager.getPriceInfo()
        return info.ceilingPrice <= info.basePrice
    }

    private fun isCeilingOverBasicRange(): Boolean {
        val info = manager.getPriceInfo()
        return info.ceilingPrice > (info.basePrice * 1.2)
    }

    fun getTableInfo() = manager.getTable()

    // API
    fun getBrandList() {
        viewModelScope.launch {

            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getCarBrandSeriesCategory(
                GetCarBrandSeriesCategoryReq(0, 0)
            )

            val status = when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        BasicEditorStatus.Brand(it)
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }

                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }

            _statusLiveData.value = status
        }
    }

    fun getSeries() {
        val info = manager.getBrandInfo()

        if (info.id == 0) {
            _statusLiveData.value = BasicEditorStatus.ResponseMessage("請先選擇廠牌")
            return
        }

        viewModelScope.launch {

            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getCarBrandSeriesCategory(
                GetCarBrandSeriesCategoryReq(info.id, 0)
            )

            val status = when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        BasicEditorStatus.Series(it)
                    }
                }

                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }

                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }

            _statusLiveData.value = status
        }
    }

    // 取得出廠年份
    fun getManufactureYearsList() {
        val brandInfo = manager.getBrandInfo()
        val seriesInfo = manager.getSeriesInfo()

        if (brandInfo.isDefault() || seriesInfo.isDefault()) {
            _statusLiveData.value = getRespMessage("請先選擇廠牌、車型")
            return
        }

        viewModelScope.launch {

            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getManufactureListByCarSeries(
                GetManufactureYearByCarSeriesReq(
                    seriesID = seriesInfo.id,
                    manufactureYear = 0,
                    manufactureMonth = 0
                )
            )

            val status = when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        BasicEditorStatus.ManufactureYear(it.reversed())
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }

                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }
            _statusLiveData.value = status

        }
    }

    // 取得月份 ( 不需要API )
    fun getManufactureMonthList() {
        val brandInfo = manager.getBrandInfo()
        val seriesInfo = manager.getSeriesInfo()

        if (brandInfo.isDefault() || seriesInfo.isDefault()) {
            _statusLiveData.value = getRespMessage("請先選擇廠牌、車型")
            return
        }

        val list = mutableListOf<String>().apply {
            for (m in 1..12) {
                add("$m 月")
            }
        }

        _statusLiveData.value = BasicEditorStatus.ManufactureMonth(list)
    }

    // 選擇年份後自動帶入
    private fun getCarCategoryAutoFill() {
        val seriesInfo = manager.getSeriesInfo()
        val manufactureInfo = manager.getManufacture()

        if (seriesInfo.isDefault()) {
            _statusLiveData.value = getRespMessage("請先選擇車型")
            return
        }

        if (manufactureInfo.isYearDefault()) {
            _statusLiveData.value = getRespMessage("請先選擇出廠年份與月份")
            return
        }


        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val categoryResp = repository.getCarCategoryByManufacture(
                GetCarCategoryByManufactureReq(
                    seriesID = seriesInfo.id,
                    manufactureYear = manufactureInfo.year,
                    manufactureMonth = if (manufactureInfo.isMonthDefault()) 1 else manufactureInfo.month.toInt()
                )
            )

            when(categoryResp) {
                is ApiResponse.Success -> {
                    categoryResp.data?.let { list ->
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            manager.setCategory(first.categoryId, first.name)
                        }
                    }
                }

                is ApiResponse.ApiFailure -> {
                    getErrorMessage(categoryResp.errorCode, categoryResp.message)
                }

                is ApiResponse.NetworkError -> {
                    getErrorMessage(categoryResp.statusCode, categoryResp.message)
                }
            }

            val categoryInfo = manager.getCategory()

            val specResp = repository.getCarModelOtherInputDataByCategoryId(
                GetCarModelOtherInputDataByCategoryIdReq(categoryInfo.id)
            )

            val status = when(specResp) {
                is ApiResponse.Success -> {
                    specResp.data.let { list ->
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            val spec = CarSpecificationInfo(
                                transmission = specTypesManager.parseTransmissionType(first.transmission),
                                gearType = specTypesManager.parseGearType(first.gearTypeID),
                                fuelType = specTypesManager.parseGasType(first.gasTypeID),
                                engineDisplacement = specTypesManager.parseEngineDisplacementType(first.displacement),
                                passenger = specTypesManager.parsePassengerType(first.passenger)
                            )

                            manager.setSpecification(spec)

                            BasicEditorStatus.CategoryAndSpecification(
                                categoryName = manager.getCategory().name,
                                specItem = spec
                            )
                        } else {
                            getRespMessage("規格清單為空")
                        }
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(specResp.errorCode, specResp.message)
                }
                is ApiResponse.NetworkError -> {
                    getErrorMessage(specResp.statusCode, specResp.message)
                }
            }

            _statusLiveData.value = status
        }
    }

    // 選擇車款
    fun getCarCategory() {
        val seriesInfo = manager.getSeriesInfo()
        val manufactureInfo = manager.getManufacture()

        if (seriesInfo.isDefault()) {
            _statusLiveData.value = getRespMessage("請先選擇車型")
            return
        }

        if (manufactureInfo.isYearDefault() || manufactureInfo.isMonthDefault()) {
            _statusLiveData.value = getRespMessage("請先選擇出廠年份與月份")
            return
        }

        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getCarCategoryByManufacture(
                GetCarCategoryByManufactureReq(
                    seriesID = seriesInfo.id,
                    manufactureYear = manufactureInfo.year.toInt(),
                    manufactureMonth = manufactureInfo.month
                )
            )

            val status = when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let { list ->
                        BasicEditorStatus.Category(list)
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }

            _statusLiveData.value = status
        }
    }

    private fun getCategorySpec() {
        val categoryInfo = manager.getCategory()

        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getCarModelOtherInputDataByCategoryId(
                GetCarModelOtherInputDataByCategoryIdReq(categoryID = categoryInfo.id)
            )

            val status = when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let { list ->
                        if (list.isNotEmpty()) {
                            val item = list.first()
                            val spec = CarSpecificationInfo(
                                transmission = specTypesManager.parseTransmissionType(item.transmission),
                                gearType = specTypesManager.parseGearType(item.gearTypeID),
                                fuelType = specTypesManager.parseGasType(item.gasTypeID),
                                engineDisplacement = specTypesManager.parseEngineDisplacementType(item.displacement),
                                passenger = specTypesManager.parsePassengerType(item.passenger)
                            )
                            manager.setSpecification(spec)
                            BasicEditorStatus.CategoryAndSpecification(categoryInfo.name ,spec)
                        } else {
                            BasicEditorStatus.ResponseMessage("規格清單為")
                        }
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }
            _statusLiveData.value = status
        }
    }

    // 檢查Hot認證書編號
    fun checkHotCertificateNo(
        hotCerId: String
    ) {
        viewModelScope.launch {

            // 要加上欄位檢查
            _statusLiveData.value = BasicEditorStatus.Loading

            val brandId = manager.getBrandInfo().id
            val hotMemberId = AccountInfoManager.getMemberInfo().hotMemberId
            val manufacture = "${manager.getManufacture().year}-${manager.getManufacture().month}-01"
            val resp = repository.getCheckHotCerNoResult(
                GetCheckHotCerNoResultReq(
                    brandID = brandId,
                    hotCerNo = hotCerId,
                    hotMemberId= hotMemberId,
                    manufactureDate = manufacture
                )
            )

            _statusLiveData.value = when(resp){
                is ApiResponse.Success -> {
                    resp.data.let {
                        // 檢查成功則帶入 HotCerId
                        if (it) { setHotVerifyNo(hotCerId) }
                        BasicEditorStatus.HotCerNoResult(it)
                    }
                }
                is ApiResponse.ApiFailure -> {
                    if (resp.errorCode == "1") {
                        BasicEditorStatus.HotCerNoResult(
                            boolean = false,
                            message = resp.message
                        )
                    } else {
                        getRespMessage(resp.message)
                    }
                }
                is ApiResponse.NetworkError -> {
                    getRespMessage("${resp.statusCode} - ${resp.message}")
                }
            }
        }
    }

    fun getTransmissionTypeList() {
        _statusLiveData.value = BasicEditorStatus.TransmissionTypeList(
            specTypesManager.getTransmissionTypes()
        )
    }

    fun getGearTypeList() {
        _statusLiveData.value = BasicEditorStatus.GearTypeList(
            specTypesManager.getGearTypes()
        )
    }

    fun getFuelTypeList() {
        _statusLiveData.value = BasicEditorStatus.FuelTypeList(
            specTypesManager.getGasTypes()
        )
    }

    fun getEngineDisplacementItemList() {
        _statusLiveData.value = BasicEditorStatus.EngineDisplacementItemList(
            specTypesManager.getEngineDisplacementTypes()
        )
    }

    fun getPassengerItemList() {
        _statusLiveData.value = BasicEditorStatus.PassengerItemList(
            specTypesManager.getPassengerType()
        )
    }

    fun getColorTypeList() {
        _statusLiveData.value = BasicEditorStatus.ColorTypeList(
            specTypesManager.getColorTypes()
        )
    }

    fun isShowPriceRangeGroup(): Boolean {
        val status = manager.getLicenseInfo()
        val info = manager.getPriceInfo()
        return info.basePrice >= 150 && status.isImport
    }

    fun refreshTable() {
        _tableLiveData.value = manager.getTable()
    }

    private fun getRespMessage(message: String): BasicEditorStatus.ResponseMessage {
        return BasicEditorStatus.ResponseMessage(message)
    }

    private fun getErrorMessage(errorCode: String, message: String): BasicEditorStatus.ErrorMessage {
        return BasicEditorStatus.ErrorMessage(errorCode, message)
    }

    fun setIsImport(status: Boolean) {
        manager.setIsImport(status)
    }

    // 點擊下一步後開始表單檢查與檔案上傳
    fun startTableCheckProcess(checkVehicleNo: Boolean) {
        val list = manager.checkColumUnqualified()
        _columnCheckLiveData.value = list

        // 錯誤清單為空，則開始檢查車身號碼、上傳對應文件等程序
        if (list.isEmpty()) {
            prepareData(checkVehicleNo)
        }
    }

    private fun prepareData(checkVehicleNo: Boolean) {
        viewModelScope.launch {

            // 編輯模式不檢查車身號碼
            if (manager.getCarId() > 0) {
                if (!checkVehicleNo) { // 如果不給修改車身號碼，則不需要再檢查一次
                    _statusLiveData.value = BasicEditorStatus.Pass
                    return@launch
                }
            }

            if (manager.isNormalSource()) {
                // 非平輸車 and 錯誤達兩次以上 -> 強制上傳行照前面已經檢查過了
                if (manager.needCheckVehicleNo()) {
                    _statusLiveData.value = BasicEditorStatus.Loading

                    // 檢查車身號碼
                    val spec = manager.getSpecification()
                    val vehicleNoResp = repository.getCheckVehicleNoResult(
                        GetCheckVehicleNoResultReq(
                            brandID = manager.getBrandInfo().id,
                            seriesId = manager.getSeriesInfo().id,
                            engineDisplacement = spec.engineDisplacement.id,
                            gasType = spec.fuelType.id,
                            manufactureYear = manager.getManufacture().year,
                            manufactureMonth = manager.getManufacture().month,
                            motorVehicleTypeName = "",
                            vehicleNo = manager.getLicenseInfo().vehicleNo
                        )
                    )

                    when(vehicleNoResp) {
                        is ApiResponse.Success -> {
                            vehicleNoResp.data.let {
                                manager.setVerifyId(it) // 驗證通過取得驗證號碼
                            }
                        }
                        is ApiResponse.ApiFailure -> {
                            parseVehicleNoErrorCode(vehicleNoResp)
                        }
                        is ApiResponse.NetworkError -> {
                            getRespMessage("${vehicleNoResp.statusCode} - ${vehicleNoResp.message}")
                        }
                    }
                } else {
                    _statusLiveData.value = BasicEditorStatus.Pass
                }
            } else {
                _statusLiveData.value = BasicEditorStatus.Pass
            }

        }
    }

    private fun parseVehicleNoErrorCode(resp: ApiResponse.ApiFailure<Int>) {
        // 這裡要判斷 9998 9996 狀態
        when(resp.errorCode) {
            "9998" -> {
                // 修改鈕停留於 Step 1，仍要下一步進入 Step 2
                _statusLiveData.value = BasicEditorStatus.VehicleNumberEdit(resp.message)
            }
            "9996" -> {
                // 顯示確認按鈕，無法進入 Step 2
                _statusLiveData.value = BasicEditorStatus.VehicleNumberBlock(resp.message)
            }
            else -> {
                // 比照錯誤兩次上傳行照
                manager.getLicenseInfo().vehicleNoErrorCount ++ // 驗證失敗計算
                // 讓manager自己判斷次數限制
                val isNeedUpload = manager.isVehicleNoUnqualified()
                _statusLiveData.value = BasicEditorStatus.VehicleNo(
                    errorMessage = resp.message,
                    showUploadDesc = isNeedUpload
                )
            }
        }
    }

    fun isHotMember(): Boolean {
        return AccountInfoManager.isHotMember()
    }

    fun initBasicTable(resp: GetEditCarInfoResp) {
        viewModelScope.launch {

            val carInfo = resp.carInfo

            manager.setCarId(carInfo.id) // 設定CarId

            dateTool.formatterToDate(carInfo.manufactureDate)

            val calendar = Calendar.getInstance()

            dateTool.formatterToDate(carInfo.manufactureDate)?.let {
                calendar.time = it
            } ?: run  {
                getRespMessage("初始化資料Manufacture錯誤")
                return@launch
            }

            val info = resp.carInfo
            val brandSeriesResp = repository.getCarBrandSeriesCategory(
                GetCarBrandSeriesCategoryReq(
                    info.brandID,
                    info.seriesID
                )
            )

            when(brandSeriesResp) {
                is ApiResponse.Success -> {
                    if (brandSeriesResp.data.isEmpty()) {
                        getRespMessage("資料初始化錯誤")
                        return@launch
                    } else {
                        val brandSeriesItem = brandSeriesResp.data.first()
                        manager.setBrandInfo(brandSeriesItem.brandID, brandSeriesItem.brandName)
                        manager.setSeriesInfo(brandSeriesItem.seriesID, brandSeriesItem.seriesName)
                        manager.setCategory(brandSeriesItem.categoryID, brandSeriesItem.categoryName)

                        manager.setManufactureYear(calendar.get(Calendar.YEAR))
                        manager.setManufactureMonth(calendar.get(Calendar.MONTH) + 1) // 月份陣列是從0開始
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getRespMessage(brandSeriesResp.message)
                    return@launch
                }
                is ApiResponse.NetworkError -> {
                    getRespMessage(brandSeriesResp.message)
                    return@launch
                }
            }

            val specResp = repository.getSpecification()

            when(specResp) {
                is ApiResponse.Success -> {
                    specTypesManager.setAllTypes(specResp.data)
                    parseSpecType(carInfo)
                }
                is ApiResponse.ApiFailure -> {
                    getRespMessage(specResp.message)
                    return@launch
                }
                is ApiResponse.NetworkError -> {
                    getRespMessage(specResp.message)
                    return@launch
                }
            }

            manager.setMileage(carInfo.mileage)
            manager.setIsImport(carInfo.carSourceType == 2) // 2為平輸車
            manager.setHotVerify(carInfo.hotcerno ?: "")
            manager.setVehicleNo(carInfo.vehicleNo ?: "")
            manager.setVerifyId(carInfo.verifyID)

            if (manager.isImportSource()) {
                manager.setImportDocFile(
                    url = carInfo.uploadFiles.find { it.carFileType == 3}?.url ?: "",
                    extension = carInfo.uploadFiles.find { it.carFileType == 3 }?.url?.substringAfterLast(".")  ?: ""
                )
            } else {
                manager.setLicenseFile(
                    url = carInfo.uploadFiles.find { it.carFileType == 3}?.url ?: "",
                    extension = carInfo.uploadFiles.find { it.carFileType == 3 }?.url?.substringAfterLast(".")  ?: ""
                )
            }

            manager.setPriceInfo(
                PriceInfo(
                    basePrice = carInfo.price,
                    ceilingPrice = carInfo.priceMax
                )
            )

            refreshTable()

        }
    }

    private fun parseSpecType(carInfo: CarInfo) {
        val transmissionType = specTypesManager.parseTransmissionType(carInfo.transmissionID)
        val gearType = specTypesManager.parseGearType(carInfo.gearTypeID)
        val gasType = specTypesManager.parseGasType(carInfo.gasTypeID)
        val engineDisplacementType = specTypesManager.parseEngineDisplacementType(carInfo.engineDisplacement)
        val passenger = specTypesManager.parsePassengerType(carInfo.passenger)
        val color = specTypesManager.parseColorType(carInfo.color)

        manager.setSpecification(
            CarSpecificationInfo(
                transmission = transmissionType,
                gearType = gearType,
                fuelType = gasType,
                engineDisplacement = engineDisplacementType,
                passenger = passenger,
            )
        )
        manager.setColor(color)
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = EditorInfoSourceImp(apiClient)
                val repository = EditorBasicInfoRepositoryImp(apiSource)
                return EditorBasicInfoViewModel(repository) as T
            }
        }
    }

}