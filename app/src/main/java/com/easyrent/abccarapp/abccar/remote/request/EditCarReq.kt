package com.easyrent.abccarapp.abccar.remote.request

data class EditCarReq(

    // Unuseful
    val AirBag: Int,
    val Equipped: Int,
    val IsShowDiscountNotice: Int,
    val WarningCategory: Int,
    val ZoneID: Int,
    val ZoneReason: Int ,
    val SafetyEquipment: Int,

    // Init
    val ID: Int, // CarID
    var MemberID: Int,
    var CreateUserID: Int, // 創建紀錄者ID 同 MemberID

    // Step 1
    val BrandID: Int,
    val SeriesID: Int,
    val ManufactureMonth: Int,
    val ManufactureYear: Int,
    val CategoryID: Int,
    val TransmissionID: Int, // 傳動系統
    val GasTypeID: Int, // 燃油種類
    val GearTypeID: Int, // 變速系統
    val EngineDisplacement: Int, // 排氣量

    val Color: Int, // 顏色
    val HOTCERNO: String,
    val CarSourceType: Int, // 1.一般 2.平輸 3.原廠 4.新車 (3、4 目前沒在用)

    val Mileage: String, // 里程
    val Passenger: Int, // 乘坐人數

    val VehicleNo: String, // 車身號碼
//    val VerifyID: Int, // 編輯不需要車身驗證ID

    val Price: Double, // 價格
    val PriceMax: Double, // 最高價格
    val PriceMin: Double, // 最低價格


// Step 2
    val EquippedInside: Int, // 內裝標籤
    val EquippedOutside: Int, // 外觀標籤
    val EquippedSafety: Int, // 安全標籤
    val EquippedFeature: String, // 特殊
    val EquippedDrive: Int, // 車輛配備

    // Step 4
    val Description: String, // 車輛說明
    val DescriptionTitle: String, // 特色標題
    val EquippedEnsure: Int, // 保證標籤
    val Feature: Int, // 特色標籤

    // Step 5 - 聯絡人
    val MemberContactIDs: List<Int>,

    // Unknown
    val UploadFiles: List<UploadFile>,
    val LinkCARNO: String,
    val RentData: List<String> = emptyList(),
)