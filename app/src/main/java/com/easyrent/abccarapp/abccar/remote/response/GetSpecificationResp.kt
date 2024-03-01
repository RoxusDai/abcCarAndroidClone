package com.easyrent.abccarapp.abccar.remote.response

data class GetSpecificationResp(
    val colorList: List<ColorType>,
    val engineDisplacementList: List<EngineDisplacementType>,
    val gasTypeList: List<GasType>,
    val gearTypeList: List<GearType>,
    val passengerList: List<Passenger>,
    val transmissionList: List<TransmissionType>
)

data class ColorType(
    val id: Int,
    val name: String
)

data class TransmissionType(
    val id: Int,
    val name: String
)

data class GearType(
    val id: Int,
    val name: String
)

data class GasType(
    val id: Int,
    val name: String
)

data class EngineDisplacementType(
    val id: Int,
    val name: String
)

data class Passenger(
    val id: Int,
    val name: String
)

