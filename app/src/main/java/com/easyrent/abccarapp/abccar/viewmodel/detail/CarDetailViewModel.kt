package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easyrent.abccarapp.abccar.manager.editor.AccessoriesInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.contact.ContactInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.feature.FeatureInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus

class CarDetailViewModel: ViewModel()  {
    private var carId: Int = 0
    var onlineDescribe = 0
    var warnStatus = ""
    var videoUrl = ""
    var favoriteCount = 0
    var viewCount = 0


    private var getEditCarInfoResp: GetEditCarInfoResp? = null

    private var basicInfoTable: BasicInfoTable = BasicInfoTable()

    private var accessoriesInfoTable: AccessoriesInfoTable = AccessoriesInfoTable()

    private var photoInfoTable = PhotoInfoTable()

    private var featureInfoTable = FeatureInfoTable()

    private var contactInfoTable = ContactInfoTable()

    ///需要一個liveData通知其他fragment取得carid更新資料
    private val _tableLiveData: MutableLiveData<DetailApiStatus> = MutableLiveData()
    val tableLiveData: LiveData<DetailApiStatus> = _tableLiveData

    fun setID(id : Int){
        carId = id

    }
    fun getID() :Int = carId

    fun setEditCarInfoResp(resp: GetEditCarInfoResp) {
        getEditCarInfoResp = resp
        _tableLiveData.value = DetailApiStatus.InitCarInfo(resp)
    }

    fun getEditCarInfoResp() = getEditCarInfoResp

    fun getBasicInfoTable() = basicInfoTable

    fun setBasicInfoTable(table: BasicInfoTable) {
        basicInfoTable = table
    }
    fun setMediaDone(){
        _tableLiveData.value = getEditCarInfoResp?.let { DetailApiStatus.InitMedia(it) }
    }
    fun setSpecDone(){
        _tableLiveData.value = getEditCarInfoResp?.let { DetailApiStatus.InitSpec(it) }
    }
    fun setCertDone(){
        _tableLiveData.value = getEditCarInfoResp?.let { DetailApiStatus.InitCert(it) }
    }
    fun setAsseDone(){
        _tableLiveData.value = getEditCarInfoResp?.let { DetailApiStatus.InitAsse(it) }
    }
    fun setAccessoriesInfoTable(table: AccessoriesInfoTable) {
        accessoriesInfoTable = table
    }

    fun getAccessoriesInfoTable() = accessoriesInfoTable

    fun setPhotoInfoTable(table: PhotoInfoTable) {
        photoInfoTable = table
    }
    fun getPhotoInfoTable() = photoInfoTable


    fun setFeatureInfoTable(table: FeatureInfoTable) {
        featureInfoTable = table
    }

    fun getFeatureInfoTable() = featureInfoTable

    fun setContactInfoTable(table: ContactInfoTable) {
        contactInfoTable = table
    }
    fun getContactInfoTable() = contactInfoTable
    fun LoadFail() {
        _tableLiveData.value = DetailApiStatus.InitFail
    }


}