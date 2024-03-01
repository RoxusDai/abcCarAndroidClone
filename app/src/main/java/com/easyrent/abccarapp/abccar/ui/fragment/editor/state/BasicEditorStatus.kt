package com.easyrent.abccarapp.abccar.ui.fragment.editor.state

import com.easyrent.abccarapp.abccar.manager.editor.colum.CarSpecificationInfo
import com.easyrent.abccarapp.abccar.remote.response.ColorType
import com.easyrent.abccarapp.abccar.remote.response.EngineDisplacementType
import com.easyrent.abccarapp.abccar.remote.response.GasType
import com.easyrent.abccarapp.abccar.remote.response.GearType
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.remote.response.Passenger
import com.easyrent.abccarapp.abccar.remote.response.TransmissionType
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarCategoryInfo
import com.easyrent.abccarapp.abccar.repository.editor.basic.CarClassificationItem

sealed class BasicEditorStatus {

    data object DataInitDone : BasicEditorStatus()

    class InitTableInfo(
        val resp: GetEditCarInfoResp
    ) : BasicEditorStatus()

    class Brand(
        val list: List<CarClassificationItem>
    ) : BasicEditorStatus()

    class Series(
        val list: List<CarClassificationItem>
    ) : BasicEditorStatus()

    class ManufactureYear(
        val list: List<String>
    ) : BasicEditorStatus()

    class ManufactureMonth(
        val list: List<String>
    ) : BasicEditorStatus()

    class Category(
        val list: List<CarCategoryInfo>
    ) : BasicEditorStatus()

    class CategoryAndSpecification(
        val categoryName: String,
        val specItem: CarSpecificationInfo
    ) : BasicEditorStatus()

    class TransmissionTypeList(
        val list: List<TransmissionType>
    ) : BasicEditorStatus()

    class GearTypeList(
        val list: List<GearType>
    ) : BasicEditorStatus()

    class FuelTypeList(
        val list: List<GasType>
    ) : BasicEditorStatus()

    class EngineDisplacementItemList(
        val list: List<EngineDisplacementType>
    ) : BasicEditorStatus()

    class PassengerItemList(
        val list: List<Passenger>
    ) : BasicEditorStatus()

    class ColorTypeList(
        val list: List<ColorType>
    ) : BasicEditorStatus()

    // Hot認證書檢查
    class HotCerNoResult(
        val boolean: Boolean,
        val message: String = ""
    ) : BasicEditorStatus()

    // 車身驗證錯誤
    class VehicleNo(
        val errorMessage: String,
        val showUploadDesc: Boolean
    ) : BasicEditorStatus()

    class VehicleNumberEdit(
        val message: String
    ) : BasicEditorStatus()

    class VehicleNumberBlock(
        val message: String
    ) : BasicEditorStatus()

    class FileUploadError(
        val message: String
    ) : BasicEditorStatus()

    class PriceErrorDesc(
        val isShowLowerError: Boolean,
        val isShowRangeError: Boolean,
        val maxPrice: String
    ) : BasicEditorStatus()

    // 下一步檢查程序通過
    data object Pass : BasicEditorStatus()

    class ResponseMessage(
        val message: String
    ) : BasicEditorStatus()

    class ErrorMessage(
        val errorCode: String,
        val message: String
    ) : BasicEditorStatus()

    data object Loading : BasicEditorStatus()

    data object LoadingFinish : BasicEditorStatus()

}
