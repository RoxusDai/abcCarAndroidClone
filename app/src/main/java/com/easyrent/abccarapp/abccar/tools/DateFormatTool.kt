package com.easyrent.abccarapp.abccar.tools

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatTool {

    private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.TAIWAN)

    fun formatterToDate(string: String): Date? {
        return formatter.parse(string)
    }

    fun dateToString(date: Date): String {
        return formatter.format(date)
    }
}