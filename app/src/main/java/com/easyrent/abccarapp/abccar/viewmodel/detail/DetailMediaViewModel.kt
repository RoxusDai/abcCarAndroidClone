package com.easyrent.abccarapp.abccar.viewmodel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.editor.photo.EditorPhotoManager
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.repository.editor.photo.EditorPhotoRepository
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSourceImp
import com.easyrent.abccarapp.abccar.tools.FilesTool
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.PhotoEditorStatus

class DetailMediaViewModel(
    private val repository: EditorPhotoRepository
) : ViewModel() {

    private val tableManager = EditorPhotoManager()

    private val _statusLiveData: MutableLiveData<PhotoEditorStatus> = MutableLiveData()
    val statusLiveData: LiveData<PhotoEditorStatus> get() = _statusLiveData


    fun getList(): List<PhotoItem> = tableManager.getImageUrlList()

    fun setPhotoTable(table : PhotoInfoTable){
        tableManager.setTable(table)
        _statusLiveData.value = PhotoEditorStatus.UpdateTable(tableManager.getTable())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val api = ApiClient.instance
                val source = EditorInfoSourceImp(api)
                val repository = EditorPhotoRepository(source)
                return DetailMediaViewModel (repository) as T
            }
        }
    }
}