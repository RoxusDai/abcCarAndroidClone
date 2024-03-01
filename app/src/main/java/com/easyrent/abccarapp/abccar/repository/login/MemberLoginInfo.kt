package com.easyrent.abccarapp.abccar.repository.login

import com.easyrent.abccarapp.abccar.remote.response.GetMemberResp

/**
 *  用於APP內部的Data class
 */
data class MemberLoginInfo(
    val memberInfoResp: GetMemberResp,
    val originalToken: String,
    val authorizationToken: String
)