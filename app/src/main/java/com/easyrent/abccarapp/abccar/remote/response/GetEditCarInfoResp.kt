package com.easyrent.abccarapp.abccar.remote.response

data class GetEditCarInfoResp(
    val carInfo: CarInfo
)

data class CarInfo(
    val airBag: Int,
    val brandID: Int,
    val campaignData: List<Any>,
    val campaignMember: List<Int>,
    val carSourceType: Int,
    val categoryID: Int,
    val color: Int,
    val countryName: Any,
    val description: String,
    val descriptionTitle: String,
    val displayName: String,
    val endDate: String,
    val engineDisplacement: Int,
    val equipped: Int,
    val equippedDrive: Int,
    val equippedEnsure: Int,
    val equippedFeature: String,
    val equippedInside: Int,
    val equippedOutside: Int,
    val equippedSafety: Int,
    val feature: Int,
    val gasTypeID: Int,
    val gearTypeID: Int,
    val gridFixed: Int,
    val hotcerno: String?,
    val id: Int,
    val isChecked: Int,
    val isOrdered: Int,
    val isPriceWarning: Boolean,
    val isShowDiscountNotice: Boolean,
    val manufactureDate: String,
    val memberContactIDs: List<Int>,
    val memberID: Int,
    val mileage: String,
    val modifyDate: String,
    val modifyUser: Int,
    val motorVehicleTypeName: Any,
    val passenger: Int,
    val price: Double,
    val priceMax: Double,
    val priceMin: Double,
    val rentData: Any,
    val safetyEquipment: Int,
    val seriesID: Int,
    val source: Any,
    val startDate: String,
    val status: Int,
    val transferCheckId: Int,
    val transmissionID: Int,
    val uploadFiles: List<EditUploadFile>,
    val vehicleNo: String?,
    val vehicleNoCheckStatus: Int,
    val vehicleNoReview: Int,
    val verifyID: Int,
    val warningCategory: Any,
    val warningValue: Double,
    val zoneID: Int,
    val zoneReason: Int
)

data class EditUploadFile(
    val carFileType: Int,
    val status: Int,
    val url: String
)