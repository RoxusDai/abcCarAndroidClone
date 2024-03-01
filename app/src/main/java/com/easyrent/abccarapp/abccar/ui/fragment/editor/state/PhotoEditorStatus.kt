package com.easyrent.abccarapp.abccar.ui.fragment.editor.state

import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem

sealed class PhotoEditorStatus {

    data object Loading : PhotoEditorStatus()

    class AddImageList(
        val urls: List<PhotoItem>
    ) : PhotoEditorStatus()

    class UpdateImageList(
        val urls: List<PhotoItem>
    ) : PhotoEditorStatus()

    class UpdateTable(
        val table: PhotoInfoTable
    ) : PhotoEditorStatus()

    data class UpdateVideoDone(
        val url: String
    ) : PhotoEditorStatus()

    class Error(
        val errorCode: String,
        val message: String
    ) : PhotoEditorStatus()
}
