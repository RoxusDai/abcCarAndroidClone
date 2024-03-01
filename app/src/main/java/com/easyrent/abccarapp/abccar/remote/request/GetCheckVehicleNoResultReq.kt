package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class GetCheckVehicleNoResultReq(
    @SerializedName("BrandID") val brandID: Int,
    @SerializedName("EngineDisplacement") val engineDisplacement: Int,
    @SerializedName("GasType") val gasType: Int,
    @SerializedName("ManufactureMonth") val manufactureMonth: Int,
    @SerializedName("ManufactureYear") val manufactureYear: Int,
    @SerializedName("MotorVehicleTypeName") val motorVehicleTypeName: String,
    @SerializedName("SeriesId") val seriesId: Int,
    @SerializedName("VehicleNo") val vehicleNo: String
)