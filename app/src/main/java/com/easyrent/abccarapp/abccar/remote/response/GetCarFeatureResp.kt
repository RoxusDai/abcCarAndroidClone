package com.easyrent.abccarapp.abccar.remote.response

class GetCarFeatureResp : ArrayList<GetCarFeatureItem>()

data class GetCarFeatureItem(
    val id: Int,
    val name: String
)