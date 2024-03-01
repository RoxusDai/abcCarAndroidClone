package com.easyrent.abccarapp.abccar.manager.editor.colum

data class DatePair(
    val year: Int = -1,
    val month: Int = 0
): ColumCheckInterface {
    override fun isUnqualified(): Boolean {
        return isYearDefault() || (month <= 0 || month > 12)
    }

    fun isYearDefault() = year < 0
    fun isMonthDefault() = month == 0

}