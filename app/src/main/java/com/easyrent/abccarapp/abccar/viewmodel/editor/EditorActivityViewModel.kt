package com.easyrent.abccarapp.abccar.viewmodel.editor

import androidx.lifecycle.ViewModel
import com.easyrent.abccarapp.abccar.manager.editor.AccessoriesInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.contact.ContactInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.feature.FeatureInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.remote.request.AddCarAndPutOnAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.AddCarReq
import com.easyrent.abccarapp.abccar.remote.request.EditCarReq
import com.easyrent.abccarapp.abccar.remote.request.UploadFile
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp

class EditorActivityViewModel : ViewModel() {

    private var carId: Int = 0
    private var editMode :Int = 0

    private var getEditCarInfoResp: GetEditCarInfoResp? = null

    private var basicInfoTable: BasicInfoTable = BasicInfoTable()

    private var accessoriesInfoTable: AccessoriesInfoTable = AccessoriesInfoTable()

    private var photoInfoTable = PhotoInfoTable()

    private var featureInfoTable = FeatureInfoTable()

    private var contactInfoTable = ContactInfoTable()

    fun setEditCarInfoResp(resp: GetEditCarInfoResp) {
        getEditCarInfoResp = resp
    }

    fun getEditCarInfoResp() = getEditCarInfoResp

    fun getBasicInfoTable() = basicInfoTable

    fun setBasicInfoTable(table: BasicInfoTable) {
        basicInfoTable = table
    }

    fun setAccessoriesInfoTable(table: AccessoriesInfoTable) {
        accessoriesInfoTable = table
    }

    fun getAccessoriesInfoTable() = accessoriesInfoTable

    fun setPhotoInfoTable(table: PhotoInfoTable) {
        photoInfoTable = table
    }

    fun getPhotoInfoTable() = photoInfoTable

    fun setFeatureInfoTable(table: FeatureInfoTable) {
        featureInfoTable = table
    }

    fun getFeatureInfoTable() = featureInfoTable

    fun setContactInfoTable(table: ContactInfoTable) {
        contactInfoTable = table
    }

    fun getContactInfoTable() = contactInfoTable

    // 與AddCarAndPutOnAdvertisingBoardReq結構一樣
    fun getAddReq(): AddCarReq {

        val uploadFiles = mutableListOf<UploadFile>().apply {
            // 行照或進口文件
            val licenseFile = getLicenseFiles()

            val carImageFile = getCarImageFiles()

            val certificateFiles = getCertificateFiles()

            val videoFile = getVideoUrl()

            addAll(licenseFile)
            addAll(carImageFile)
            addAll(certificateFiles)
            addAll(videoFile)
        }

        return AddCarReq(
            AirBag = 0, // 填0，沒在用
            SafetyEquipment = 0, // 填0，沒在用
            Equipped = 0, // 填0，沒在用
            EquippedDrive = getAccessoriesInfoTable().equippedDrive, // 車輛配備(Step2)
            EquippedInside = getAccessoriesInfoTable().equippedInside, // 內裝配備(Step2)
            EquippedOutside = getAccessoriesInfoTable().equippedOutside, // 外觀配備(Step2)
            EquippedEnsure = getFeatureInfoTable().ensureTagsSum, // 保證項目(Step4)
            EquippedSafety = getAccessoriesInfoTable().equippedSafety, // 安全配備(Step2)
            EquippedFeature = getAccessoriesInfoTable().spacial, // 特殊配備(Step2)
            Feature = getFeatureInfoTable().featureTagsSum, // 特色標籤(Step4)
            Description = getFeatureInfoTable().templateContent, // 車輛說明(Step4)
            DescriptionTitle = getFeatureInfoTable().featureTitle, // 特色標題(Step4)
            IsShowDiscountNotice = 0, // 填0，沒在用
            WarningCategory = 0, // 填0，沒在用
            CreateUserID = 0, // 後續API呼叫填上，暫時給 0
            ZoneID = 0, // 填0，沒在用
            ZoneReason = 0, // 填0，沒在用
            CarSourceType = if (getBasicInfoTable().licenseInfo.isImport) 2 else 1, // 1.一般 2.平輸 3.原廠 4.新車 (3、4 目前沒在用)
            RentData = emptyList(), // 租賃資料，目前用不到給空List
            LinkCARNO = "", // 目前用不到，給空字串

            CarID = 0, // 新增車輛為0
            MemberID = 0, // 後續API呼叫填上，暫時給 0
            BrandID = getBasicInfoTable().brand.id,
            SeriesID = getBasicInfoTable().series.id,
            CategoryID = getBasicInfoTable().category.id,
            Color = getBasicInfoTable().descInfo.colorType.id,
            GasTypeID = getBasicInfoTable().specification.fuelType.id,
            GearTypeID = getBasicInfoTable().specification.gearType.id,
            ManufactureYear = getBasicInfoTable().manufacture.year,
            ManufactureMonth = getBasicInfoTable().manufacture.month,
            Mileage = getBasicInfoTable().descInfo.mileage,
            Passenger = getBasicInfoTable().specification.passenger.id,
            TransmissionID = getBasicInfoTable().specification.transmission.id,
            EngineDisplacement = getBasicInfoTable().specification.engineDisplacement.id,
            Price = getBasicInfoTable().priceInfo.basePrice,
            PriceMin = getBasicInfoTable().priceInfo.basePrice,
            PriceMax = getBasicInfoTable().priceInfo.ceilingPrice,
            HOTCERNO = getBasicInfoTable().licenseInfo.hotVerifyId,
            MemberContactIDs = getContactInfoTable().memberContactIDs,
            VehicleNo = getBasicInfoTable().licenseInfo.vehicleNo,
            VerifyID = getBasicInfoTable().licenseInfo.verifyID,

            UploadFiles = uploadFiles
        )
    }

    // 與AddCarReq結構一樣
    fun getAddCarAndPutOnAdvertisingBoardReq(): AddCarAndPutOnAdvertisingBoardReq {

        val uploadFiles = mutableListOf<UploadFile>().apply {
            // 行照或進口文件
            val licenseFile = getLicenseFiles()

            val carImageFile = getCarImageFiles()

            val certificateFiles = getCertificateFiles()

            val videoFile = getVideoUrl()

            addAll(licenseFile)
            addAll(carImageFile)
            addAll(certificateFiles)
            addAll(videoFile)
        }

        return AddCarAndPutOnAdvertisingBoardReq(
            AirBag = 0, // 填0，沒在用
            SafetyEquipment = 0, // 填0，沒在用
            Equipped = 0, // 填0，沒在用
            EquippedDrive = getAccessoriesInfoTable().equippedDrive, // 車輛配備(Step2)
            EquippedInside = getAccessoriesInfoTable().equippedInside, // 內裝配備(Step2)
            EquippedOutside = getAccessoriesInfoTable().equippedOutside, // 外觀配備(Step2)
            EquippedEnsure = getFeatureInfoTable().ensureTagsSum, // 保證項目(Step4)
            EquippedSafety = getAccessoriesInfoTable().equippedSafety, // 安全配備(Step2)
            EquippedFeature = getAccessoriesInfoTable().spacial, // 特殊配備(Step2)
            Feature = getFeatureInfoTable().featureTagsSum, // 特色標籤(Step4)
            Description = getFeatureInfoTable().templateContent, // 車輛說明(Step4)
            DescriptionTitle = getFeatureInfoTable().featureTitle, // 特色標題(Step4)
            IsShowDiscountNotice = 0, // 填0，沒在用
            WarningCategory = 0, // 填0，沒在用
            CreateUserID = 0, // 後續API呼叫填上，暫時給 0
            ZoneID = 0, // 填0，沒在用
            ZoneReason = 0, // 填0，沒在用
            CarSourceType = if (getBasicInfoTable().licenseInfo.isImport) 2 else 1, // 1.一般 2.平輸 3.原廠 4.新車 (3、4 目前沒在用)
            RentData = emptyList(), // 租賃資料，目前用不到給空List
            LinkCARNO = "", // 目前用不到，給空字串

            CarID = 0, // 新增車輛為0
            MemberID = 0, // 後續API呼叫填上，暫時給 0
            BrandID = getBasicInfoTable().brand.id,
            SeriesID = getBasicInfoTable().series.id,
            CategoryID = getBasicInfoTable().category.id,
            Color = getBasicInfoTable().descInfo.colorType.id,
            GasTypeID = getBasicInfoTable().specification.fuelType.id,
            GearTypeID = getBasicInfoTable().specification.gearType.id,
            ManufactureYear = getBasicInfoTable().manufacture.year,
            ManufactureMonth = getBasicInfoTable().manufacture.month,
            Mileage = getBasicInfoTable().descInfo.mileage,
            Passenger = getBasicInfoTable().specification.passenger.id,
            TransmissionID = getBasicInfoTable().specification.transmission.id,
            EngineDisplacement = getBasicInfoTable().specification.engineDisplacement.id,
            Price = getBasicInfoTable().priceInfo.basePrice,
            PriceMin = getBasicInfoTable().priceInfo.basePrice,
            PriceMax = getBasicInfoTable().priceInfo.ceilingPrice,
            HOTCERNO = getBasicInfoTable().licenseInfo.hotVerifyId,
            MemberContactIDs = getContactInfoTable().memberContactIDs,
            VehicleNo = getBasicInfoTable().licenseInfo.vehicleNo,
            VerifyID = getBasicInfoTable().licenseInfo.verifyID,

            UploadFiles = uploadFiles
        )
    }

    fun getEditCarReq(): EditCarReq {
        val uploadFiles = mutableListOf<UploadFile>().apply {
            // 行照或進口文件
            val licenseFile = getLicenseFiles()

            val carImageFile = getCarImageFiles()

            val certificateFiles = getCertificateFiles()

            val videoFile = getVideoUrl()

            addAll(licenseFile)
            addAll(carImageFile)
            addAll(certificateFiles)
            addAll(videoFile)
        }

        return EditCarReq(
            AirBag = 0, // 填0，沒在用
            SafetyEquipment = 0, // 填0，沒在用
            Equipped = 0, // 填0，沒在用
            EquippedDrive = getAccessoriesInfoTable().equippedDrive, // 車輛配備(Step2)
            EquippedInside = getAccessoriesInfoTable().equippedInside, // 內裝配備(Step2)
            EquippedOutside = getAccessoriesInfoTable().equippedOutside, // 外觀配備(Step2)
            EquippedEnsure = getFeatureInfoTable().ensureTagsSum, // 保證項目(Step4)
            EquippedSafety = getAccessoriesInfoTable().equippedSafety, // 安全配備(Step2)
            EquippedFeature = getAccessoriesInfoTable().spacial, // 特殊配備(Step2)
            Feature = getFeatureInfoTable().featureTagsSum, // 特色標籤(Step4)
            Description = getFeatureInfoTable().templateContent, // 車輛說明(Step4)
            DescriptionTitle = getFeatureInfoTable().featureTitle, // 特色標題(Step4)
            IsShowDiscountNotice = 0, // 填0，沒在用
            WarningCategory = 0, // 填0，沒在用
            CreateUserID = 0, // 後續API呼叫填上，暫時給 0
            ZoneID = 0, // 填0，沒在用
            ZoneReason = 0, // 填0，沒在用
            CarSourceType = if (getBasicInfoTable().licenseInfo.isImport) 2 else 1, // 1.一般 2.平輸 3.原廠 4.新車 (3、4 目前沒在用)
            RentData = emptyList(), // 租賃資料，目前用不到給空List
            LinkCARNO = "", // 目前用不到，給空字串

            ID = carId, // 填入物件ID
            MemberID = 0, // 後續API呼叫填上，暫時給 0
            BrandID = getBasicInfoTable().brand.id,
            SeriesID = getBasicInfoTable().series.id,
            CategoryID = getBasicInfoTable().category.id,
            Color = getBasicInfoTable().descInfo.colorType.id,
            GasTypeID = getBasicInfoTable().specification.fuelType.id,
            GearTypeID = getBasicInfoTable().specification.gearType.id,
            ManufactureYear = getBasicInfoTable().manufacture.year,
            ManufactureMonth = getBasicInfoTable().manufacture.month,
            Mileage = getBasicInfoTable().descInfo.mileage,
            Passenger = getBasicInfoTable().specification.passenger.id,
            TransmissionID = getBasicInfoTable().specification.transmission.id,
            EngineDisplacement = getBasicInfoTable().specification.engineDisplacement.id,
            Price = getBasicInfoTable().priceInfo.basePrice,
            PriceMin = getBasicInfoTable().priceInfo.basePrice,
            PriceMax = getBasicInfoTable().priceInfo.ceilingPrice,
            HOTCERNO = getBasicInfoTable().licenseInfo.hotVerifyId,
            MemberContactIDs = getContactInfoTable().memberContactIDs,
            VehicleNo = getBasicInfoTable().licenseInfo.vehicleNo,

            UploadFiles = uploadFiles
        )
    }

    private fun getLicenseFiles(): MutableList<UploadFile> {
        return mutableListOf<UploadFile>().apply {
            if (getBasicInfoTable().licenseInfo.isImport) {
                add(UploadFile(3, basicInfoTable.licenseInfo.importDocFileUrl.url))
            } else if (basicInfoTable.licenseInfo.licenseFileUrl.url.isNotBlank()) {
                add(UploadFile(3, basicInfoTable.licenseInfo.licenseFileUrl.url))
            }
        }
    }

    private fun getCarImageFiles(): List<UploadFile> {
        return getPhotoInfoTable()
            .imageUrlList.sortedBy { it.isCover } // 排序封面照片
            .map { UploadFile(0, it.url) }
    }

    private fun getCertificateFiles(): List<UploadFile> {
        return getFeatureInfoTable().certificateList.map {
            UploadFile(it.type.id, it.url)
        }
    }

    private fun getVideoUrl(): List<UploadFile> {
        return if (getPhotoInfoTable().videoUrl.isNotBlank()) {
            mutableListOf(
                UploadFile(11, getPhotoInfoTable().videoUrl)
            )
        } else {
            emptyList()
        }
    }

    fun setCarId(id: Int) {
        carId = id
    }

    fun getCarId(): Int {
        return carId
    }

    fun setEditMode(em: Int) {
        editMode = em
    }

    fun getEditMode(): Int = editMode
}