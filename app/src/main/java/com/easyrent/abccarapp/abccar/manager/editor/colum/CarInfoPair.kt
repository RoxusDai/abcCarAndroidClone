package com.easyrent.abccarapp.abccar.manager.editor.colum

data class CarInfoPair(
    val id: Int = 0,
    val name: String = ""
): ColumCheckInterface {
    override fun isUnqualified(): Boolean {
        return id == 0 || name.isBlank()
    }

    fun isDefault(): Boolean {
        return id == 0
    }

}
