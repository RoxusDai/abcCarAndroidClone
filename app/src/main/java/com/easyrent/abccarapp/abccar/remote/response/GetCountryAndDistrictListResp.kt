package com.easyrent.abccarapp.abccar.remote.response

data class GetCountryAndDistrictListResp(
    val countries: List<CountyItem>,
    val districts: List<DistrictItem>
)

data class CountyItem(
    val id: Int,
    val name: String
)

data class DistrictItem(
    val countryID: Int,
    val id: Int,
    val name: String,
    val zipCode: Int
)