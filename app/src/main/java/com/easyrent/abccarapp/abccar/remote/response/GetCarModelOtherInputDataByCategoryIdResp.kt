package com.easyrent.abccarapp.abccar.remote.response

class GetCarModelOtherInputDataByCategoryIdResp : ArrayList<CarModelOtherInputDataByCategoryIdItem>()

data class CarModelOtherInputDataByCategoryIdItem(
    val bodyTypeID: Int,
    val brandID: Int,
    val categoryID: Int,
    val displacement: Int,
    val gasTypeID: Int,
    val gearTypeID: Int,
    val modelEndYear: Int,
    val modelStartYear: Int,
    val passenger: Int,
    val seriesID: Int,
    val transmission: Int
)