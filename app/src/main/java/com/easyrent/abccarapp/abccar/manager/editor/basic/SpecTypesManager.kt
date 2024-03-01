package com.easyrent.abccarapp.abccar.manager.editor.basic

import com.easyrent.abccarapp.abccar.remote.response.ColorType
import com.easyrent.abccarapp.abccar.remote.response.EngineDisplacementType
import com.easyrent.abccarapp.abccar.remote.response.GasType
import com.easyrent.abccarapp.abccar.remote.response.GearType
import com.easyrent.abccarapp.abccar.remote.response.GetSpecificationResp
import com.easyrent.abccarapp.abccar.remote.response.Passenger
import com.easyrent.abccarapp.abccar.remote.response.TransmissionType

/**
 *  Specification types 管理器
 *
 *   避免參數在使用過程意外遭到更動
 */
class SpecTypesManager{
    private val colorTypes = mutableListOf<ColorType>()
    private val gearTypes = mutableListOf<GearType>()
    private val gasTypes = mutableListOf<GasType>()
    private val transmissionTypes = mutableListOf<TransmissionType>()
    private val engineDisplacementTypes = mutableListOf<EngineDisplacementType>()
    private val passengerTypes = mutableListOf<Passenger>()

    fun setAllTypes(
        resp: GetSpecificationResp
    ) {
        setColorTypes(resp.colorList)
        setGearTypes(resp.gearTypeList)
        setGasTypes(resp.gasTypeList)
        setTransmissionTypes(resp.transmissionList)
        setEngineDisplacementTypes(resp.engineDisplacementList)
        setPassengerTypes(resp.passengerList)
    }

    fun setColorTypes(list: List<ColorType>) {
        colorTypes.clear()
        colorTypes.addAll(list)
    }

    fun setGearTypes(list: List<GearType>) {
        gearTypes.clear()
        gearTypes.addAll(list)
    }

    fun setGasTypes(list: List<GasType>) {
        gasTypes.clear()
        gasTypes.addAll(list)
    }

    fun setTransmissionTypes(list: List<TransmissionType>) {
        transmissionTypes.clear()
        transmissionTypes.addAll(list)
    }

    fun setEngineDisplacementTypes(list: List<EngineDisplacementType>) {
        engineDisplacementTypes.clear()
        engineDisplacementTypes.addAll(list)
    }

    fun setPassengerTypes(list: List<Passenger>) {
        passengerTypes.clear()
        passengerTypes.addAll(list)
    }

    fun getColorTypes(): List<ColorType> = colorTypes
    fun getGearTypes(): List<GearType> = gearTypes
    fun getGasTypes(): List<GasType> = gasTypes
    fun getTransmissionTypes(): List<TransmissionType> = transmissionTypes
    fun getEngineDisplacementTypes(): List<EngineDisplacementType> = engineDisplacementTypes
    fun getPassengerType(): List<Passenger> = passengerTypes

    fun parseColorType(id: Int): ColorType {
        return getColorTypes().find { it.id == id } ?: ColorType(id = 0, name = "- - -")
    }

    fun parseGearType(id: Int): GearType {
        return getGearTypes().find { it.id == id } ?: GearType(id = 0, name = "- - -")
    }

    fun parseGasType(id: Int): GasType {
        return getGasTypes().find { it.id == id } ?: GasType(id = 0, name = "- - -")
    }

    fun parseTransmissionType(id: Int): TransmissionType {
        return getTransmissionTypes().find { it.id == id } ?: TransmissionType(id = 0, name = "- - -")
    }

    fun parseEngineDisplacementType(id: Int): EngineDisplacementType {

        val types = getEngineDisplacementTypes().sortedBy { id }

        return when{
            id <= types.first().id -> types.first()
            id >= types.last().id -> types.last()
            else -> {
                getEngineDisplacementTypes().find { it.id == id } ?: EngineDisplacementType(id = 0, name = "- - -")
            }
        }
    }

    fun parsePassengerType(id: Int): Passenger {
        return getPassengerType().find { it.id == id } ?: Passenger(id = 0, name = "- - -")
    }

}