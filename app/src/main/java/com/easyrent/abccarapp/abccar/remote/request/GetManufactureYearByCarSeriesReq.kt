package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class GetManufactureYearByCarSeriesReq(
    @SerializedName("ManufactureMonth") val manufactureMonth: Int,
    @SerializedName("ManufactureYear") val manufactureYear: Int,
    @SerializedName("SeriesID") val seriesID: Int
)