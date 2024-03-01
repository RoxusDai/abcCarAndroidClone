package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

// GetCarBrandSeriesCategory req
data class GetCarBrandSeriesCategoryReq(
    @SerializedName("BrandID") val brandID: Int,
    @SerializedName("SeriesID") val seriesID: Int
)