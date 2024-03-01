package com.easyrent.abccarapp.abccar.repository.mainCarList

import com.easyrent.abccarapp.abccar.remote.response.CarEdit
import com.easyrent.abccarapp.abccar.remote.response.DisplayTitle
import com.easyrent.abccarapp.abccar.remote.response.Pagination

data class MainCarListInfo(
    val displayTitle: DisplayTitle,
    val carEditList: List<CarEdit>,
    val pagination: Pagination
)