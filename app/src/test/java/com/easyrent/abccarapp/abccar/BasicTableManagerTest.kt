package com.easyrent.abccarapp.abccar

import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicTableManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BasicTableManagerTest {

    private val manager = BasicTableManager()

    @Before
    fun setup() {
        manager.clear()
    }

    @Test
    fun initBradInfoTest() {
        val target = manager.getBrandInfo().isUnqualified()
        val excepted = false
        Assert.assertEquals(excepted, target)
    }

    @Test
    fun initSeriesInfoTest() {
        val target = manager.getSeriesInfo().isUnqualified()
        val excepted = false
        Assert.assertEquals(excepted, target)
    }

    @Test
    fun initCategoryInfoTest() {
        val target = manager.getCategory().isUnqualified()
        val excepted = false
        Assert.assertEquals(excepted, target)
    }

    @Test
    fun initManufactureInfoTest() {
        val target = manager.getManufacture().isUnqualified()
        val expected = false
        Assert.assertEquals(expected, target)
    }

    @Test
    fun initDescInfo() {
        val target = manager.getDescInfo().isUnqualified()
        val expected = false
        Assert.assertEquals(expected, target)
    }




}