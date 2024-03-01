package com.easyrent.abccarapp.abccar.remote.response

import com.google.gson.annotations.SerializedName


/**
 *
 *  主頁車輛清單Resp
 *
 *  PS. 測試用API無回應或null的資料類型會變成Any
 */

data class EditCarListResp(
    val carEditList: List<CarEdit>,
    val displayTitle: DisplayTitle,
    val pagination: Pagination,
    val resultCode: String,
    val resultMessage: String
)


data class CarEdit(
    val brandID: Int,
    val brandName: String,
    val campaignData: List<CampaignData>,
    val campaignDataEx: List<Any>,
    val campaignID: Any,
    val campaignItemID: Any,
    val carCertificationApplication: List<Any>,
    val carID: Int,
    val carSourceType: Int,
    val categoryID: Int,
    val categoryName: String,
    val descriptionTitle: String,
    val displayName: Any,
    val endDate: String,
    val favoriteCount: Int,
    val gridCreateDate: Any,
    val gridEndDate: Any,
    val gridFixed: Int,
    val gridModifyDate: Any,
    val hasCertification: Boolean,
    val hasVideo: Int,
    val imageUrl: String,
    val inStoreDate: String,
    val inStoreStatus: Int,
    val isChecked: Int,
    val isOnCarAdvertisingPlacement: Boolean,
    val isShowDiscountNotice: Boolean,
    val lastStartDate: Any,
    val lastTimePrice: Double,
    val light: Any,
    val manufactureDate: String,
    val manufactureMonth: String,
    val manufactureYear: String,
    val modifyDate: String,
    val onlineDescription: Int,
    val price: Double,
    val rowID: Int,
    val score: Any,
    val searchBrandName: String,
    val searchCategoryName: String,
    val seriesID: Int,
    val seriesName: String,
    val source: Any,
    val sourceKey: Any,
    val startDate: String,
    val status: Int,
    val transferCheckId: Int,
    val url: Any,
    val vehicleNoCheckStatus: Int,
    val vehicleNoLen: Int,
    val vehicleNoReview: Int,
    val viewCount: Int,
    val zoneID: Int,
    val zoneReason: Int
)

data class DisplayTitle(
    val abcCommissionPrice: Int,
    val carADBoardRemainingQuantity: Int,
    val carADBoardTotalCount: Int,
    val carDealDoneCount: Int,
    val carFixedPublishingCount: Int,
    val carForSaleCount: Int,
    val carInGarageTotalCount: Int,
    val carPublishingCount: Int,
    val multiAreaCount: Int,
    val price: Int,
    val usedCarADBoardCount: Int
)

data class Pagination(
    @SerializedName("next_page") val nextPage: Int,
    val page: Int,
    @SerializedName("prev_page") val prevPage: Int,
    @SerializedName("total_count")val totalCount: Int,
    @SerializedName("total_page")val totalPage: Int
)

data class CampaignData(
    val campaignID: Int,
    val campaignItemID: Int,
    val carID: Int,
    val carIfRefund: Int,
    val createDate: String,
    val createUser: Any,
    val liveStream: Int,
    val liveStreamEndDate: String,
    val liveStreamStartDate: String,
    val memberID: Int,
    val modifyDate: String,
    val modifyUser: Any,
    val status: Int,
    val url: String
)