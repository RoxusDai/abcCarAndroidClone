package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicTableManager
import com.easyrent.abccarapp.abccar.manager.editor.basic.SpecTypesManager
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarSpecificationInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.PriceInfo
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.response.CarInfo
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.repository.editor.basic.EditorBasicInfoRepository
import com.easyrent.abccarapp.abccar.repository.editor.basic.EditorBasicInfoRepositoryImp
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.tools.DateFormatTool
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import kotlinx.coroutines.launch
import java.util.Calendar

class DetailBasicInfoViewModel(
    private val repository: EditorBasicInfoRepository
) : ViewModel(){

    ////livedata
    private val _tableLiveData: MutableLiveData<BasicInfoTable> = MutableLiveData()
    val tableLiveData: LiveData<BasicInfoTable> = _tableLiveData
    private val _statusLiveData: MutableLiveData<BasicEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<BasicEditorStatus> = _statusLiveData

    private val manager = BasicTableManager()
    private val dateTool = DateFormatTool()



    fun getEditCarInfo(carId: Int) {
        viewModelScope.launch {
            _statusLiveData.value = BasicEditorStatus.Loading

            val resp = repository.getCarEditInfo(carId)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    BasicEditorStatus.InitTableInfo(resp.data)
                }
                is ApiResponse.ApiFailure -> {
                    getErrorMessage(resp.errorCode, resp.message)
                }
                is ApiResponse.NetworkError -> {
                    getErrorMessage(resp.statusCode, resp.message)
                }
            }
        }
    }

    private fun getErrorMessage(errorCode: String, message: String): BasicEditorStatus.ErrorMessage {
        return BasicEditorStatus.ErrorMessage(errorCode, message)
    }

    fun initBasicTable(resp: GetEditCarInfoResp) {
        viewModelScope.launch {

            val carInfo = resp.carInfo

            manager.setCarId(carInfo.id) // 設定CarId

            dateTool.formatterToDate(carInfo.manufactureDate)

            val calendar = Calendar.getInstance()

            dateTool.formatterToDate(carInfo.manufactureDate)?.let {
                calendar.time = it
            } ?: run  {
                getRespMessage("初始化資料Manufacture錯誤")
                return@launch
            }

            val info = resp.carInfo
            val brandSeriesResp = repository.getCarBrandSeriesCategory(
                GetCarBrandSeriesCategoryReq(
                    info.brandID,
                    info.seriesID
                )
            )

            when(brandSeriesResp) {
                is ApiResponse.Success -> {
                    if (brandSeriesResp.data.isEmpty()) {
                        getRespMessage("資料初始化錯誤")
                        return@launch
                    } else {
                        val brandSeriesItem = brandSeriesResp.data.first()
                        manager.setBrandInfo(brandSeriesItem.brandID, brandSeriesItem.brandName)
                        manager.setSeriesInfo(brandSeriesItem.seriesID, brandSeriesItem.seriesName)
                        manager.setCategory(brandSeriesItem.categoryID, brandSeriesItem.categoryName)

                        manager.setManufactureYear(calendar.get(Calendar.YEAR))
                        manager.setManufactureMonth(calendar.get(Calendar.MONTH) + 1) // 月份陣列是從0開始
                    }
                }
                is ApiResponse.ApiFailure -> {
                    getRespMessage(brandSeriesResp.message)
                    return@launch
                }
                is ApiResponse.NetworkError -> {
                    getRespMessage(brandSeriesResp.message)
                    return@launch
                }
            }
            manager.setMileage(carInfo.mileage)
            manager.setIsImport(carInfo.carSourceType == 2) // 2為平輸車
            manager.setHotVerify(carInfo.hotcerno ?: "")
            manager.setVehicleNo(carInfo.vehicleNo ?: "沒有號碼")
            manager.setVerifyId(carInfo.verifyID)

            if (manager.isImportSource()) {
                manager.setImportDocFile(
                    url = carInfo.uploadFiles.find { it.carFileType == 3}?.url ?: "",
                    extension = carInfo.uploadFiles.find { it.carFileType == 3 }?.url?.substringAfterLast(".")  ?: ""
                )
            } else {
                manager.setLicenseFile(
                    url = carInfo.uploadFiles.find { it.carFileType == 3}?.url ?: "",
                    extension = carInfo.uploadFiles.find { it.carFileType == 3 }?.url?.substringAfterLast(".")  ?: ""
                )
            }

            manager.setPriceInfo(
                PriceInfo(
                    basePrice = carInfo.price,
                    ceilingPrice = carInfo.priceMax
                )
            )

            refreshTable()

        }
    }

    private fun getRespMessage(message: String): BasicEditorStatus.ResponseMessage {
        return BasicEditorStatus.ResponseMessage(message)
    }
    fun refreshTable() {
        _tableLiveData.value = manager.getTable()
    }



    companion object {
        ///設定資料來源
        ///想了一下,還是從編輯呼叫
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = EditorInfoSourceImp(apiClient)
                val repository = EditorBasicInfoRepositoryImp(apiSource)
                return DetailBasicInfoViewModel(repository) as T
            }
        }
    }
}