package com.easyrent.abccarapp.abccar.repository.login

import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.EditPasswordReq
import com.easyrent.abccarapp.abccar.remote.request.ForgetPasswordReq
import com.easyrent.abccarapp.abccar.remote.request.MemberLoginReq
import com.easyrent.abccarapp.abccar.remote.response.ForgetPasswordResp
import com.easyrent.abccarapp.abccar.source.login.MemberInfoSource
import com.easyrent.abccarapp.abccar.source.preferences.PreferencesSource
import com.easyrent.abccarapp.abccar.source.TokenPackage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

/**
 *  LoginRepository
 *
 *  在API Source 與 ViewModel 之間可以另外做資料的轉換與處理
 *
 */

class LoginRepositoryImp(
    private val apiSource: MemberInfoSource,
    private val preferencesSource: PreferencesSource
) : LoginRepository {
    override suspend fun isTokenAvailable(): Boolean {

        val tokenPackage = getToken().first()

        // 需要 Manager 判斷是否過期
        AccountInfoManager.setOriginalToken(tokenPackage)

        return AccountInfoManager.isOriginalTokenAvailable(Date())
    }

    override suspend fun changePassword(req : EditPasswordReq): ApiResponse<TokenPackage>{
        val resp = apiSource.changePassword(req)
        return resp
    }

    override suspend fun memberLogin(req: MemberLoginReq): ApiResponse<MemberLoginInfo> {
        val resp = apiSource.memberLogin(req)
        // 如果API Response為成功，且token不為空白，則更新Manager中的Token
        if (resp is ApiResponse.Success) {
            resp.data.let { info ->
                val original = info.originalToken
                val auth = info.authorizationToken

                saveOriginalToken(original)

                val token = TokenPackage(
                    originalToken = info.originalToken,
                    authorizationToken = info.authorizationToken
                )

                AccountInfoManager.setToken(token)

                val memberId = info.memberInfoResp.id
                val hotMemberId = info.memberInfoResp.hotMemberID
                AccountInfoManager.setMemberInfo(memberId, hotMemberId)
            }
        }
        return resp
    }




    override suspend fun refreshToken(): ApiResponse<Boolean> {
        val resp = apiSource.refreshAuthToken()

        return when(resp) {
            is ApiResponse.Success -> {
                saveOriginalToken(resp.data.originalToken)

                val token = TokenPackage(
                    originalToken = resp.data.originalToken,
                    authorizationToken =  resp.data.authorizationToken
                )

                AccountInfoManager.setToken(token)
                ApiResponse.Success(true)
            }
            is ApiResponse.ApiFailure -> {
                ApiResponse.ApiFailure(
                    errorCode = resp.errorCode,
                    message = resp.message
                )
            }
            is ApiResponse.NetworkError -> {
                ApiResponse.NetworkError(
                    statusCode = resp.statusCode,
                    message = resp.message
                )
            }
        }
    }

    override suspend fun getMemberInfo(): ApiResponse<MemberLoginInfo> {

        val resp = apiSource.getMemberInfo()

        if (resp is ApiResponse.Success) {
            resp.data.let { info ->

                val memberId = info.memberInfoResp.id
                val hotMemberId = info.memberInfoResp.hotMemberID
                AccountInfoManager.setMemberInfo(memberId, hotMemberId)
            }
        }
        return resp
    }


    override suspend fun saveOriginalToken(token: String) {
        preferencesSource.setOriginalToken(token)
    }

    override fun getToken(): Flow<String> {
        return preferencesSource.getToken()
    }



}