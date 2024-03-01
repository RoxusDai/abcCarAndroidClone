package com.easyrent.abccarapp.abccar.remote.response

data class GetMemberResp(
    val address: String,
    val alliance: Int,
    val areaCode: String,
    val authorizationToken: Any,
    val brandID: Int,
    val campaignMember: List<Int>,
    val canEstimate: Boolean,
    val carDealerAddress: String,
    val carDealerAreaCode: String,
    val carDealerContactPerson: String,
    val carDealerCountryName: String,
    val carDealerDistrictName: String,
    val carDealerExt: String,
    val carDealerMobile: String,
    val carDealerName: String,
    val carDealerTelephone: String,
    val carDealerViewCount: Int,
    val category: Int,
    val companyName: String,
    val contactDay: Int,
    val contactPerson: String,
    val countryName: String,
    val customProfile: Any,
    val description: String?,
    val displayAllianceCertification: Boolean,
    val displayGuildCertificat: Boolean,
    val displaySalesCertificat: Boolean,
    val districtName: String,
    val email: String,
    val endContactTime: String,
    val ext: String,
    val guild: Int,
    val guildID: String,
    val hotMemberID: String,
    val id: Int,
    val lineID: String,
    val misName: String,
    val misPhone: String,
    val mobilePhone: String,
    val name: String,
    val principal: String,
    val resultCode: String,
    val resultMessage: String,
    val saleCategory: Int,
    val source: String,
    val sourceKey: String,
    val startContactTime: String,
    val status: Int,
    val taxID: String,
    val telephone: String,
    val uploadFiles: List<UploadFile>,
    val vipLevel: Int
)