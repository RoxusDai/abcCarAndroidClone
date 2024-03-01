package com.easyrent.abccarapp.abccar.repository.login

import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.EditPasswordReq
import com.easyrent.abccarapp.abccar.remote.request.MemberLoginReq
import com.easyrent.abccarapp.abccar.source.TokenPackage
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun isTokenAvailable(): Boolean

    suspend fun changePassword(req: EditPasswordReq): ApiResponse<TokenPackage>
    suspend fun memberLogin(req: MemberLoginReq): ApiResponse<MemberLoginInfo>
    suspend fun getMemberInfo(): ApiResponse<MemberLoginInfo>
    suspend fun refreshToken(): ApiResponse<Boolean>
    suspend fun saveOriginalToken(token: String)
    fun getToken(): Flow<String>

}