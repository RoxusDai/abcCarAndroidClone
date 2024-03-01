package com.easyrent.abccarapp.abccar.manager

import com.easyrent.abccarapp.abccar.repository.login.MemberInfo
import com.easyrent.abccarapp.abccar.source.TokenPackage
import com.easyrent.abccarapp.abccar.tools.DateFormatTool
import com.google.gson.Gson
import java.util.Base64
import java.util.Date

object AccountInfoManager {

    private var tokenPackage: TokenPackage = TokenPackage()
    private var memberInfo: MemberInfo = MemberInfo()

    private val formatTool = DateFormatTool()

    private val gson = Gson()

    fun setMemberInfo(memberId: Int, hotMemberId: String) {
        memberInfo = MemberInfo(memberId, hotMemberId)
    }

    fun getMemberInfo() = memberInfo

    fun isHotMember(): Boolean {
        return memberInfo.hotMemberId.isNotBlank()
    }

    fun setToken(token: TokenPackage) {
        tokenPackage.originalToken = token.originalToken
        tokenPackage.authorizationToken = token.authorizationToken
    }

    fun setOriginalToken(token: String) {
        tokenPackage.originalToken = token
    }

    fun setAuthToken(token: String) {
        tokenPackage.authorizationToken = token
    }

    // 檢查Token並回傳Token，更新Token委外處理
    fun checkToken(updateToken: (String) -> TokenPackage): TokenPackage {
        if (!isAuthTokenAvailable(Date())) {
            val originalToken = tokenPackage.originalToken
            val tokenPackage = updateToken.invoke(originalToken)
            setToken(tokenPackage)
        }
        return tokenPackage
    }

    fun isOriginTokenExist(): Boolean {
        return parseOriginalToken() != null
    }

    fun isAuthTokenExist(): Boolean {
        return parseAuthToken() != null
    }

    fun isOriginalTokenAvailable(date: Date): Boolean {
        if (tokenPackage.originalToken.isBlank()) return false

        return parseOriginalToken()?.expireTime?.let { expString ->
            // 判斷指定的時間是否為Token期限之前 ( Tests if this date is before the specified date. )
            // 2023-11-01 date
            // 2023-11-22 token
            val tokenDate = formatTool.formatterToDate(expString)
            tokenDate?.after(date)
        } ?: false
    }

    fun isAuthTokenAvailable(date: Date): Boolean {
        if (tokenPackage.authorizationToken.isBlank()) return false

        return parseAuthToken()?.expireTime?.let { expString ->
            val tokenDate = formatTool.formatterToDate(expString)
            tokenDate?.after(date)
        } ?: false
    }

    private fun parseOriginalToken(): OriginalToken? {
        // 劃分JWT
        val splitList = tokenPackage.originalToken.split(Regex("\\."), 0)

        // 檢查是否符合格式
        if (isInvalidToken(splitList)) return null

        // Base64解碼
        val jsonString = String(
            Base64.getUrlDecoder().decode(splitList[1])
        )

        return gson.fromJson(jsonString, OriginalToken::class.java)
    }

    private fun parseAuthToken(): AuthToken? {
        // 劃分JWT
        val splitList = tokenPackage.authorizationToken.split(Regex("\\."), 0)

        // 檢查是否符合格式
        if (isInvalidToken(splitList)) return null

        // Base64解碼
        val jsonString = String(
            Base64.getUrlDecoder().decode(splitList[1])
        )

        return gson.fromJson(jsonString, AuthToken::class.java)
    }

    // 檢查是否符合基礎格式
    private fun isInvalidToken(list: List<String>): Boolean {
        return list.size < 2 || list[1].isBlank()
    }

    fun clear() {
        tokenPackage = TokenPackage()
        memberInfo = MemberInfo()
    }

    private data class OriginalToken(
        val guidId: String?,
        val memberId: String?,
        val loginTime: String?,
        val expireTime: String?,
        val tokenType: String?
    )

    private data class AuthToken(
        val memberId: String?,
        val category: Int?,
        val loginTime: String?,
        val expireTime: String?,
        val tokenType: String?
    )
}