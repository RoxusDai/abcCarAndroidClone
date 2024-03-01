package com.easyrent.abccarapp.abccar.manager.editor.photo

import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem

class EditorPhotoManager {

    private var videoUrl: String = ""
    private var imageUrlList: MutableList<PhotoItem> = mutableListOf()

    fun setVideoUrl(url: String) {
        videoUrl = url
    }

    fun getImageUrlList() = imageUrlList

    fun addImageUrls(list: List<PhotoItem>) {
        imageUrlList.addAll(list)
    }

    fun deleteImage(photoItem: PhotoItem) {
        imageUrlList.remove(photoItem)
    }

    fun setImageCover(index: Int) {
        imageUrlList.forEach {
            it.isCover = false
        }

        imageUrlList[index].isCover = true
    }

    fun setTable(table: PhotoInfoTable) {
        // 如果imageUrlList原本就是空的，則更新新Table資料
        imageUrlList = mutableListOf<PhotoItem>().apply {
            addAll(table.imageUrlList)
        }
        setVideoUrl(table.videoUrl)
    }

    fun getTable(): PhotoInfoTable {
        return PhotoInfoTable(
            imageUrlList = imageUrlList,
            videoUrl = videoUrl
        )
    }


    companion object {
        private const val MaxImageSize = 20
    }

}