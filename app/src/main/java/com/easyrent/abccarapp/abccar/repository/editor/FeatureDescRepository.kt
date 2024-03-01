package com.easyrent.abccarapp.abccar.repository.editor

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.GetCarFeatureResp
import com.easyrent.abccarapp.abccar.remote.response.GetCertificateTypesResp
import com.easyrent.abccarapp.abccar.remote.response.GetEditorTemplateItem
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSource
import java.io.File


class FeatureDescRepository(
    val source: EditorInfoSource
) {
    suspend fun getAccessoriesTagsList(): ApiResponse<GetCarEquippedTagsResp> {
        return source.getCarEquippedTags()
    }

    suspend fun getCarFeatureTagsList(): ApiResponse<GetCarFeatureResp> {
        return source.getCarFeatureTags()
    }

    suspend fun getCertificateTypeTags(): ApiResponse<GetCertificateTypesResp> {
        return source.getCertificateTypeTags()
    }

    suspend fun getEditorTemplate(): ApiResponse<List<GetEditorTemplateItem>> {
        return source.getEditorTemplate()
    }

    suspend fun uploadImageFile(file: File): ApiResponse<List<String>> {
        return source.uploadImageFile(file)
    }

    suspend fun uploadPdfFile(file: File): ApiResponse<List<String>> {
        return source.uploadPdfFile(file)
    }

    suspend fun convertTags(
        req: List<TagsConvertReqItem>
    ) : ApiResponse<List<TagsConvertRespItem>> {
        return source.getTagsConvertResult(req)
    }
}