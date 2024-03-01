package com.easyrent.abccarapp.abccar.repository.editor.photo

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.source.editor.EditorInfoSource
import java.io.File

class EditorPhotoRepository(
    val source: EditorInfoSource
) {

    suspend fun uploadFiles(
        list: List<File>
    ): ApiResponse<List<String>> {
        return source.uploadMultiImages(list)
    }

    suspend fun uploadVideo(
        file: File
    ): ApiResponse<List<String>> {
        return source.uploadVideoFile(file)
    }
}