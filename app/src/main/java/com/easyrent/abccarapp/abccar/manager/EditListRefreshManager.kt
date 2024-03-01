package com.easyrent.abccarapp.abccar.manager

import com.easyrent.abccarapp.abccar.manager.unit.EditCarPageInfo


/**
 *  EditList管理器
 */
class EditListRefreshManager {
    private var pageInfo = EditCarPageInfo()

    fun reset() {
        pageInfo = EditCarPageInfo()
    }

    fun setPageInfo(
        info: EditCarPageInfo
    ) {
        pageInfo = info
    }

    fun getPageIndex() = pageInfo.pageIndex

    fun getPageInfo() = pageInfo

    fun haveNextPage(): Boolean {
        return pageInfo.pageIndex < pageInfo.totalPages
    }

    fun increasePage() {
        pageInfo.pageIndex ++
    }
}