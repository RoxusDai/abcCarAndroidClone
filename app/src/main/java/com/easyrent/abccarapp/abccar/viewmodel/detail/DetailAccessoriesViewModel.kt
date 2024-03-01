package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.response.EquippedDrive
import com.easyrent.abccarapp.abccar.remote.response.EquippedInside
import com.easyrent.abccarapp.abccar.remote.response.EquippedOutside
import com.easyrent.abccarapp.abccar.remote.response.EquippedSafety
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.repository.editor.FeatureDescRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.FeatureEditorStatus
import com.google.common.math.IntMath.pow
import kotlinx.coroutines.launch

class DetailAccessoriesViewModel(
    private val repository: FeatureDescRepository
) : ViewModel() {
    val driveList: MutableList<Pair<Int, String>> = mutableListOf()
    val insideList: MutableList<Pair<Int, String>> = mutableListOf()
    val outsideList: MutableList<Pair<Int, String>> = mutableListOf()
    val safetyList: MutableList<Pair<Int, String>> = mutableListOf()

    private val _statusLiveData: MutableLiveData<FeatureEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<FeatureEditorStatus> = _statusLiveData

    fun initAccessoriesTable(resp: GetEditCarInfoResp) {
        viewModelScope.launch {
            val accresp = repository.getAccessoriesTagsList()

            when (accresp) {
                is ApiResponse.Success -> {



                    driveList.addAll(convertDriveEquip(resp.carInfo.equippedDrive,accresp.data.equippedDriveList))
                    insideList.addAll(convertInsideEquip(resp.carInfo.equippedInside,accresp.data.equippedInsideList))
                    outsideList.addAll(convertOutsideEquip(resp.carInfo.equippedOutside,accresp.data.equippedOutsideList))
                    safetyList.addAll(convertSafetyEquip(resp.carInfo.equippedSafety,accresp.data.equippedSafetyList))

                    _statusLiveData.value = FeatureEditorStatus.InitEquippedTagsList(accresp.data)
                }

                is ApiResponse.ApiFailure -> {
                    _statusLiveData.value = errorMessage(accresp.errorCode, accresp.message)
                }

                is ApiResponse.NetworkError -> {
                    _statusLiveData.value = errorMessage(accresp.statusCode, accresp.message)
                }
            }

        }
    }

    private fun convertSafetyEquip(
        equippedSafety: Int,
        equippedSafetyList: List<EquippedSafety>?
    ): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val out = Integer.toBinaryString(equippedSafety).reversed()

        out.forEachIndexed { index, c ->
            if(c == '1'){
                list.add(Pair(equippedSafetyList?.get(index)?.id ?: pow(2,index),
                    equippedSafetyList?.get(index)?.name ?: "_"
                ))
            }
        }

        return list
    }

    private fun convertOutsideEquip(
        equippedOutside: Int,
        equippedOutsideList: List<EquippedOutside>?
    ): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val out = Integer.toBinaryString(equippedOutside).reversed()

        out.forEachIndexed { index, c ->
            if(c == '1'){
                list.add(Pair(equippedOutsideList?.get(index)?.id ?: pow(2,index),
                    equippedOutsideList?.get(index)?.name ?: "_"
                ))
            }
        }

        return list
    }

    private fun convertInsideEquip(
        equippedInside: Int,
        equippedInsideList: List<EquippedInside>?
    ): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val out = Integer.toBinaryString(equippedInside).reversed()
        out.forEachIndexed { index, c ->
            if(c == '1'){
                list.add(Pair(equippedInsideList?.get(index)?.id ?: pow(2,index),
                    equippedInsideList?.get(index)?.name ?: "_"
                ))
            }
        }

        return list
    }

    private fun convertDriveEquip(
        equippedDrive: Int,
        equippedDriveList: List<EquippedDrive>?
    ): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val out = Integer.toBinaryString(equippedDrive).reversed()

        out.forEachIndexed { index, c ->
            if(c == '1'){
                list.add(Pair(equippedDriveList?.get(index)?.id ?: pow(2,index),
                    equippedDriveList?.get(index)?.name ?: "_"
                ))
            }
        }

        return list
    }

    private fun errorMessage(errorCode: String, message: String): FeatureEditorStatus {
        return FeatureEditorStatus.ErrorMessage(errorCode, message)
    }

    companion object {
        ///設定資料來源
        ///想了一下,還是從編輯呼叫
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = EditorInfoSourceImp(apiClient)
                val repository = FeatureDescRepository(apiSource)
                return DetailAccessoriesViewModel(repository) as T
            }
        }
    }
}