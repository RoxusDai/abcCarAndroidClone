package com.easyrent.abccarapp.abccar.manager.editor

data class AccessoriesInfoTable(
    var isInit: Boolean = false,
    val equippedDrive: Int = 0,
    val equippedInside: Int = 0,
    val equippedSafety: Int = 0,
    val equippedOutside: Int = 0,
    val spacial: String = ""
)
