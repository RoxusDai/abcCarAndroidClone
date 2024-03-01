package com.easyrent.abccarapp.abccar

import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class AccountInfoManagerTest {

    private val manager = AccountInfoManager

    @Before
    fun setup() {
        manager.clear()
    }


    @Test
    fun originTokenExistTest() {
        manager.setOriginalToken(getDefaultOriginalToken())
        val target = manager.isOriginTokenExist()
        val expected = true
        Assert.assertEquals(true, manager.isOriginTokenExist())
    }

    @Test
    fun originTokenNotExistTest() {
        val target = manager.isOriginTokenExist()
        val expected = false
        Assert.assertEquals(expected, target)
    }

    @Test
    fun authTokenExistTest() {
        manager.setAuthToken(getDefaultAuthToken())
        val target = manager.isAuthTokenExist()
        val expected = true
        Assert.assertEquals(expected, target)
    }

    @Test
    fun authTokenNotExistTest() {
        val target = manager.isAuthTokenExist()
        val expected = false
        Assert.assertEquals(expected, target)
    }

    @Test
    fun originalTokenAvailableTest() {
        manager.setOriginalToken(getDefaultOriginalToken()) // 2023-11-22

        // 假設一個於有效期限之前的時間
        val date = Calendar.getInstance().apply {
            // 月份從0開始，1月是0，2月是1，可參考Java文件
            // month - the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for January.
            set(2023, 10, 1) // 2023-11-1
        }.time

        val target = manager.isOriginalTokenAvailable(date)
        val expected = true // 預期是有效的Token

        Assert.assertEquals(expected, target)
    }

    @Test
    fun originalTokenNotAvailableTest() {
        manager.setOriginalToken(getDefaultOriginalToken()) // 2023-11-22

        val date = Calendar.getInstance().apply {
            // 月份從0開始，1月是0，2月是1，可參考Java文件
            // month - the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for January.
            set(2023, 11, 1) // 2023-12-1
        }.time

        val target = manager.isOriginalTokenAvailable(date)
        val expected = false

        Assert.assertEquals(expected, target)
    }

    @Test
    fun authTokenAvailableTest() {
        manager.setAuthToken(getDefaultAuthToken()) // 2023-10-23

        val date = Calendar.getInstance().apply {
            // 月份從0開始，1月是0，2月是1，可參考Java文件
            // month - the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for January.
            set(2023, 8, 1) // 2023-9-1
        }.time

        val target = manager.isAuthTokenAvailable(date)
        val expected = true

        Assert.assertEquals(expected, target)
    }

    @Test
    fun authTokenNotAvailableTest() {
        manager.setAuthToken(getDefaultAuthToken()) // 2023-10-23

        val date = Calendar.getInstance().apply {
            // 月份從0開始，1月是0，2月是1，可參考Java文件
            // month - the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for January.
            set(2023, 10, 1) // 2023-11-1
        }.time

        val target = manager.isAuthTokenAvailable(date)
        val expected = false

        Assert.assertEquals(expected, target)
    }


    // {    "alg": "HS256", "typ": "JWT" }
    //
    // {    "guidID": "0a4fd9e9-fd54-4976-bfe6-a0fefed40675",
    //      "memberID": 18384,
    //      "loginTime": "2023-10-23T17:01:09.9550106Z",
    //      "expireTime": "2023-11-22T17:01:09.9550124Z",
    //      "tokenType": "long" }
    private fun getDefaultOriginalToken() = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJndWlkSUQiOiIwYTRmZDllOS1mZDU0LTQ5NzYtYmZlNi1hMGZlZmVkNDA2NzUiLCJtZW1iZXJJRCI6MTgzODQsImxvZ2luVGltZSI6IjIwMjMtMTAtMjNUMTc6MDE6MDkuOTU1MDEwNloiLCJleHBpcmVUaW1lIjoiMjAyMy0xMS0yMlQxNzowMTowOS45NTUwMTI0WiIsInRva2VuVHlwZSI6ImxvbmcifQ.kPkkzWFuHXX2roS9NBNGPbIG0luvOAq8R3naJoOcxf4"

    // {    "alg": "HS256", "typ": "JWT" }
    //
    // {    "memberID": 18384,
    //      "category": 3,
    //      "loginTime": "2023-10-23T17:01:09.9555196Z",
    //      "expireTime": "2023-10-23T18:01:09.9555206Z",
    //      "tokenType": "short"    }

    private fun getDefaultAuthToken() = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJRCI6MTgzODQsImNhdGVnb3J5IjozLCJsb2dpblRpbWUiOiIyMDIzLTEwLTIzVDE3OjAxOjA5Ljk1NTUxOTZaIiwiZXhwaXJlVGltZSI6IjIwMjMtMTAtMjNUMTg6MDE6MDkuOTU1NTIwNloiLCJ0b2tlblR5cGUiOiJzaG9ydCJ9.QahZnWqs2TV7DUycF4SasTEbt98LQtE7XRnMxKMyVBw"

}