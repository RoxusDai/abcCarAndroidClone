package com.easyrent.abccarapp.abccar.manager.editor.colum

data class PriceInfo(
    val basePrice: Double = 0.0,
    val ceilingPrice: Double = 0.0
) {

    // 基底價格為0
    fun isBaseZero(): Boolean {
        return basePrice <= 0
    }

    // 基底價格大於9999.9 (萬)
    fun isBaseOverMax(): Boolean {
        return basePrice > 9999.9
    }
    // 最高價格大於9999.9 (萬)
    fun isCeilingOverMax(): Boolean {
        return ceilingPrice > 9999.9
    }
    // 最高價格低於基底價格
    fun isCeilingBelowBase(): Boolean {
        return ceilingPrice < basePrice
    }
    // 最高價格不可超過基底價格兩成
    fun isCeilingOverRange(): Boolean {
        val max = basePrice * 1.2 // 不可超過基底價格兩成
        return ceilingPrice > max
    }

}