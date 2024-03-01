package com.easyrent.abccarapp.abccar.ui.fragment.state

import com.easyrent.abccarapp.abccar.remote.response.DisplayTitle
import com.easyrent.abccarapp.abccar.remote.response.GetLastPoolPointsHistoryResp
import com.easyrent.abccarapp.abccar.remote.response.GetMemberResp

sealed class AccountInfoState {

    data object Loading : AccountInfoState()

    class MemberInfo(
        val info: GetMemberResp
    ) : AccountInfoState()

    class AccountInfo(
        val displayTitle: DisplayTitle
    ) : AccountInfoState()

    class PointInfo(
        val info: GetLastPoolPointsHistoryResp
    ) : AccountInfoState()

    class Error(
        val errorCode: String,
        val message: String
    ) : AccountInfoState()

}