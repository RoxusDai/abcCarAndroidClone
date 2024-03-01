package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicTableManager
import com.easyrent.abccarapp.abccar.manager.editor.basic.SpecTypesManager
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarSpecificationInfo
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.response.CarInfo
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.repository.editor.basic.EditorBasicInfoRepository
import com.easyrent.abccarapp.abccar.repository.editor.basic.EditorBasicInfoRepositoryImp
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import kotlinx.coroutines.launch

class DetailSpecViewModel(
    private val repository: EditorBasicInfoRepository
) :ViewModel(){
    private val _specLiveData: MutableLiveData<SpecTypesManager> = MutableLiveData()
    val specLiveData: LiveData<SpecTypesManager> = _specLiveData

    private val specTypesManager = SpecTypesManager()
    val manager = BasicTableManager()

    fun initSpecTable(resp: GetEditCarInfoResp) {
        viewModelScope.launch {
            val carInfo = resp.carInfo

            val specResp = repository.getSpecification()
            when (specResp) {
                is ApiResponse.Success -> {
                    specTypesManager.setAllTypes(specResp.data)
                    parseSpecType(carInfo)
                    _specLiveData.value = specTypesManager
                }

                is ApiResponse.ApiFailure -> {
                    getRespMessage(specResp.message)
                    return@launch
                }

                is ApiResponse.NetworkError -> {
                    getRespMessage(specResp.message)
                    return@launch
                }
            }
        }
    }

    private fun parseSpecType(carInfo: CarInfo) {
        val transmissionType = specTypesManager.parseTransmissionType(carInfo.transmissionID)
        val gearType = specTypesManager.parseGearType(carInfo.gearTypeID)
        val gasType = specTypesManager.parseGasType(carInfo.gasTypeID)
        val engineDisplacementType = specTypesManager.parseEngineDisplacementType(carInfo.engineDisplacement)
        val passenger = specTypesManager.parsePassengerType(carInfo.passenger)
        val color = specTypesManager.parseColorType(carInfo.color)

        manager.setSpecification(
            CarSpecificationInfo(
                transmission = transmissionType,
                gearType = gearType,
                fuelType = gasType,
                engineDisplacement = engineDisplacementType,
                passenger = passenger,
            )
        )
        manager.setColor(color)
    }

    private fun getRespMessage(message: String): BasicEditorStatus.ResponseMessage {
        return BasicEditorStatus.ResponseMessage(message)
    }
    companion object {
        ///設定資料來源
        ///想了一下,還是從編輯呼叫
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = EditorInfoSourceImp(apiClient)
                val repository = EditorBasicInfoRepositoryImp(apiSource)
                return DetailSpecViewModel(repository) as T
            }
        }
    }
}