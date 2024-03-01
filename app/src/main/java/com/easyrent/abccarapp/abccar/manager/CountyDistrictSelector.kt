package com.easyrent.abccarapp.abccar.manager

import com.easyrent.abccarapp.abccar.remote.response.CountyItem
import com.easyrent.abccarapp.abccar.remote.response.DistrictItem
import com.easyrent.abccarapp.abccar.remote.response.GetCountryAndDistrictListResp

class CountyDistrictSelector {

    var county: CountyItem? = null
    var district: DistrictItem? = null

    private val countyList: MutableList<CountyItem> = mutableListOf()
    private val districtList: MutableList<DistrictItem> = mutableListOf()

    fun initList(list: GetCountryAndDistrictListResp) {
        countyList.clear()
        districtList.clear()
        countyList.addAll(list.countries)
        districtList.addAll(list.districts)
    }

    fun getCountyList(): List<CountyItem> = countyList
    fun getDistrictList(): List<DistrictItem> = districtList

    fun clearSelector() {
        county = null
        district = null
    }

}