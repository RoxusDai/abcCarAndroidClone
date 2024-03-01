package com.easyrent.abccarapp.abccar.manager.editor.colum

import com.easyrent.abccarapp.abccar.remote.response.ColorType


data class CarDescInfo(
    val colorType: ColorType = ColorType(id = 0, name = "- - -"),
    val mileage: String = "0"
): ColumCheckInterface {
    override fun isUnqualified(): Boolean {
        return isColorUnqualified() || isMileageUnqualified()
    }

    fun isColorUnqualified() = colorType.id == 0
    fun isMileageUnqualified() = mileage == "0"
}
