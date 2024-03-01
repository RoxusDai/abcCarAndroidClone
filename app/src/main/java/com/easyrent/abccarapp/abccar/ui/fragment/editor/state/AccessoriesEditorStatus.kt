package com.easyrent.abccarapp.abccar.ui.fragment.editor.state

import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem

sealed class AccessoriesEditorStatus {

    data object Loading : AccessoriesEditorStatus()

    class InitTagsList(
        val resp: GetCarEquippedTagsResp
    ) : AccessoriesEditorStatus()

    class InitTagsCheckStatus(
        val resp: List<TagsConvertRespItem>
    ) : AccessoriesEditorStatus()

    class ErrorMessage(
        val errorCode: String,
        val message: String
    ) : AccessoriesEditorStatus()

}