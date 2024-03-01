package com.easyrent.abccarapp.abccar.viewmodel.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.manager.CountyDistrictSelector
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.AddCarAndPutOnAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.AddCarReq
import com.easyrent.abccarapp.abccar.remote.request.AddContactPersonReq
import com.easyrent.abccarapp.abccar.remote.request.EditCarReq
import com.easyrent.abccarapp.abccar.remote.response.CountyItem
import com.easyrent.abccarapp.abccar.remote.response.DistrictItem
import com.easyrent.abccarapp.abccar.repository.editor.contact.ContactInfoRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.ContactEditorStatus
import kotlinx.coroutines.launch

class ContactInfoViewModel(
    val repository: ContactInfoRepository
) : ViewModel() {

    private val _statusLiveData: MutableLiveData<ContactEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<ContactEditorStatus> get() = _statusLiveData

    private val areaSelector = CountyDistrictSelector()

    fun setCountyItem(item: CountyItem) {
        areaSelector.county = item
    }

    fun setDistrictItem(item: DistrictItem) {
        areaSelector.district = item
    }

    fun isCountyDistrictPass(): Boolean {
        return areaSelector.county != null && areaSelector.district != null
    }

    fun getContactList() {

        _statusLiveData.value = ContactEditorStatus.Loading

        val id = AccountInfoManager.getMemberInfo().memberId

        viewModelScope.launch {
            val resp = repository.getContactPersonList(id)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    resp.data.let {
                        ContactEditorStatus.ContactList(it)
                    }
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }

    fun addContactPerson(
        req: AddContactPersonReq
    ) {
        viewModelScope.launch {

            _statusLiveData.value = ContactEditorStatus.Loading

            val resp = repository.addContactPerson(req)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    ContactEditorStatus.AddContact(resp.data?: false)
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }

    fun getCountyList() {
        if (areaSelector.getCountyList().isEmpty()) {
            viewModelScope.launch {
                val resp = repository.getCountyAndDistrictList()

                when(resp) {
                    is ApiResponse.Success -> {
                        resp.data?.let {
                            areaSelector.initList(it)
                            _statusLiveData.value = ContactEditorStatus.CountyList(areaSelector.getCountyList())
                        }
                    }
                    is ApiResponse.ApiFailure -> {
                        errorMessage(
                            errorCode = resp.errorCode,
                            message = resp.message
                        )
                    }
                    is ApiResponse.NetworkError -> {
                        errorMessage(
                            errorCode = resp.statusCode,
                            message = resp.message
                        )
                    }
                }
            }
        } else {
            _statusLiveData.value = ContactEditorStatus.CountyList(areaSelector.getCountyList())
        }

    }

    fun getDistrictList() {
        areaSelector.county?.let { countyItem ->

            val disList = areaSelector.getDistrictList().filter {
                it.countryID == countyItem.id
            }

            _statusLiveData.value = ContactEditorStatus.DistrictList(disList)

        } ?: run {
            _statusLiveData.value = ContactEditorStatus.DistrictList(emptyList())
        }
    }

    fun addCar(req: AddCarReq) {
        viewModelScope.launch {

            _statusLiveData.value = ContactEditorStatus.Loading

            val resp = repository.addCar(req)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    ContactEditorStatus.AddCarFinish
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }

    }

    fun addCarAndPutOnAdvertisingBoard(
        req: AddCarAndPutOnAdvertisingBoardReq
    ) {
        viewModelScope.launch {

            _statusLiveData.value = ContactEditorStatus.Loading

            val resp = repository.addCarAndPutOnAdvertisingBoard(req)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    ContactEditorStatus.AddCarAndPutOnAdvertisingBoardFinish
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }

    fun editCar(
        req: EditCarReq
    ) {
        viewModelScope.launch {

            _statusLiveData.value = ContactEditorStatus.Loading

            val resp = repository.editCar(req)

            _statusLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    ContactEditorStatus.EditDone
                }
                is ApiResponse.ApiFailure -> {
                    errorMessage(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    errorMessage(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }

    private fun errorMessage(
        errorCode: String,
        message: String
    ): ContactEditorStatus.ErrorMessage {
        return ContactEditorStatus.ErrorMessage(errorCode, message)
    }

    fun clearTable() {
        areaSelector.clearSelector()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val api = ApiClient.instance
                val source = EditorInfoSourceImp(api)
                val repository = ContactInfoRepository(source)
                return ContactInfoViewModel(repository) as T
            }
        }
    }

}