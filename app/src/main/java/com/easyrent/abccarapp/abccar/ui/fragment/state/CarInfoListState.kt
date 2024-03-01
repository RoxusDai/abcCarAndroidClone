package com.easyrent.abccarapp.abccar.ui.fragment.state

import com.easyrent.abccarapp.abccar.remote.response.BatchUpdateCarAdvertisingBoardResp
import com.easyrent.abccarapp.abccar.remote.response.CarEdit

sealed class CarInfoListState {

    data object Loading : CarInfoListState()

    class GetList(
        val list: List<CarEdit>
    ) : CarInfoListState()

    data class PublishDone(val mode: BatchUpdateCarAdvertisingBoardResp) : CarInfoListState()

    data class ReserveDone(val mode:Int) : CarInfoListState()

    class Error(
        val errorCode: String,
        val message: String
    ) : CarInfoListState()


}