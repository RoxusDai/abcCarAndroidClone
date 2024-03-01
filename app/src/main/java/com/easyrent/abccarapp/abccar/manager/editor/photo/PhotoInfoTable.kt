package com.easyrent.abccarapp.abccar.manager.editor.photo

import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem

data class PhotoInfoTable(
    var isInit: Boolean = false,
    val imageUrlList: List<PhotoItem> = mutableListOf(),
    val videoUrl: String = ""
)