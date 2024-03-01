package com.easyrent.abccarapp.abccar.remote.response

data class GetCarEquippedTagsResp(
    val airBagList: List<Any>?,
    val equippedDriveList: List<EquippedDrive>?,
    val equippedEnsureList: List<EquippedEnsure>?,
    val equippedInsideList: List<EquippedInside>?,
    val equippedOutsideList: List<EquippedOutside>?,
    val equippedSafetyList: List<EquippedSafety>?,
    val interiorExteriorList: List<EquippedInteriorExterior>?,
    val resultCode: String,
    val resultMessage: String,
    val safetyList: List<Safety>
)

data class EquippedInteriorExterior(
    val id: Int,
    val name: String
)

data class EquippedDrive(
    val id: Int,
    val name: String
)

data class EquippedEnsure(
    val id: Int,
    val name: String
)

data class EquippedInside(
    val id: Int,
    val name: String
)

data class EquippedOutside(
    val id: Int,
    val name: String
)

data class EquippedSafety(
    val id: Int,
    val name: String
)

data class Safety(
    val id: Int,
    val name: String
)