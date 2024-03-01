package com.easyrent.abccarapp.abccar.remote.response

class GetCarCategoryByManufactureResp : ArrayList<CarCategoryByManufactureItem>()

data class CarCategoryByManufactureItem(
    val id: Int,
    val name: String,
    val parentID: Int,
    val shownOnDropdown: Int,
    val type: Int
)