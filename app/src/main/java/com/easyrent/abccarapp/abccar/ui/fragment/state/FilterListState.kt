package com.easyrent.abccarapp.abccar.ui.fragment.state

import com.easyrent.abccarapp.abccar.manager.editor.colum.CarInfoPair


open class FilterListState {

    class GetBrandList(
        val list: ArrayList<String>
    ) : FilterListState()

    class GetCategoryList(
        val list: ArrayList<String>
    ) : FilterListState()


    class Error(
        val errorCode: String,
        val message: String
    ) : FilterListState()
}