package com.easyrent.abccarapp.abccar.remote.response

class GetCarClassificationResp : ArrayList<CarBrandSeriesCategoryResp>()

data class CarBrandSeriesCategoryResp(
    val brandID: Int,
    val brandName: String,
    val categoryID: Int,
    val categoryName: String?,
    val seriesID: Int,
    val seriesName: String?
)