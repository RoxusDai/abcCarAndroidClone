package com.easyrent.abccarapp.abccar.repository.editor.accessories

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.TagsConvertReqItem
import com.easyrent.abccarapp.abccar.remote.response.GetCarEquippedTagsResp
import com.easyrent.abccarapp.abccar.remote.response.TagsConvertRespItem
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSource

class AccessoriesInfoRepository(
    private val source: EditorInfoSource
) {

    suspend fun getAccessoriesTagsList(): ApiResponse<GetCarEquippedTagsResp> {
        return source.getCarEquippedTags()
    }

    suspend fun convertTags(
        req: List<TagsConvertReqItem>
    ) : ApiResponse<List<TagsConvertRespItem>> {
        return source.getTagsConvertResult(req)
    }

}