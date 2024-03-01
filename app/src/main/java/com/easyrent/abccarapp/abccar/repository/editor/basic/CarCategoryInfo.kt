package com.easyrent.abccarapp.abccar.repository.editor.basic

data class CarCategoryInfo(
    val categoryId: Int,
    val name: String,
    val parentID: Int,
    val shownOnDropdown: Int,
    val type: Int
)