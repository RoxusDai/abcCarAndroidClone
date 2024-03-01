package com.easyrent.abccarapp.abccar.ui.fragment.editor.state

import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarFeatureResp
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesItem
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem

sealed class FeatureEditorStatus {

    data object Loading : FeatureEditorStatus()

    data object InitDone : FeatureEditorStatus()

    class InitCerListType(
        val list: List<GetCertificateTypesItem>
    ) : FeatureEditorStatus()

    class InitEquippedTagsList(
        val resp: GetCarEquippedTagsResp
    ) : FeatureEditorStatus()

    class InitFeatureTagsList(
        val resp: GetCarFeatureResp
    ) : FeatureEditorStatus()

    class TemplateContent(
        val temp: String
    ) : FeatureEditorStatus()

    class GetCerTypeList(
        val index: Int,
        val list: List<GetCertificateTypesItem>
    ) : FeatureEditorStatus()

    class GetUploadUrl(
        val url: String,
    ) : FeatureEditorStatus()

    class InitTagsStatus(
        val list: List<TagsConvertRespItem>
    ) : FeatureEditorStatus()

    class ErrorMessage(
        val errorCode: String,
        val message: String
    ) : FeatureEditorStatus()

}