package com.easyrent.abccarapp.abccar.repository

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.response.DisplayTitle
import com.easyrent.abccarapp.abccar.remote.response.GetLastPoolPointsHistoryResp
import com.easyrent.abccarapp.abccar.remote.response.GetMemberResp
import com.easyrent.abccarapp.abccar.source.main.account.AccountInfoSource

class AccountInfoRepository(
    val source: AccountInfoSource
) {
    suspend fun getAccountInfo(): ApiResponse<DisplayTitle> {
        return source.getAccountInfo()
    }

    suspend fun getPointHistory(): ApiResponse<GetLastPoolPointsHistoryResp> {
        return source.getPointHistory()
    }

    suspend fun getMemberInfo(): ApiResponse<GetMemberResp> {
        return source.getMemberInfo()
    }
}