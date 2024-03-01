package com.easyrent.abccarapp.abccar.manager.editor.colum

import com.easyrent.abccarapp.abccar.remote.response.EngineDisplacementType
import com.easyrent.abccarapp.abccar.remote.response.GasType
import com.easyrent.abccarapp.abccar.remote.response.GearType
import com.easyrent.abccarapp.abccar.remote.response.Passenger
import com.easyrent.abccarapp.abccar.remote.response.TransmissionType

data class CarSpecificationInfo(
    val transmission: TransmissionType = TransmissionType(id = 0, name = "- - -"),
    val gearType: GearType = GearType(id = 0, name = "- - -"),
    val fuelType: GasType = GasType(id = 0, name = "- - -"),
    val engineDisplacement: EngineDisplacementType = EngineDisplacementType(id = 0, name = "- - -"),
    val passenger: Passenger = Passenger(id = 0, name = "- - -")
)
