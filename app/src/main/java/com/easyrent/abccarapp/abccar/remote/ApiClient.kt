package com.easyrent.abccarapp.abccar.remote

import com.easyrent.abccarapp.abccar.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    private fun getClient(): ApiService {
        val url = if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "QAtest") DEV_URL else URL
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    private fun getForgetPasswordClient(): ForgetPasswordApiService{
        val url = if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "QAtest") forgetPasswordTestUrl else forgetPasswordUrl
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpClient())
            .build()
            .create(ForgetPasswordApiService::class.java)
    }

    private fun getHttpClient(): OkHttpClient {
        return if (BuildConfig.DEBUG|| BuildConfig.BUILD_TYPE == "QAtest") {
            OkHttpClient.Builder()
                .addInterceptor(getLoggingInterceptor()) // Logcat顯示API回應數據
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build()
        } else {
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build()
        }
    }

    // 設定Http Logging Interceptor
    private fun getLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    companion object {
        private const val DEV_URL = "https://dev2.abccar.com.tw/v1/appapi/"
        private const val URL = "https://www.abccar.com.tw/v1/appapi/"

        private const val forgetPasswordTestUrl = "https://dev2.abccar.com.tw/appapi/"
        private const val forgetPasswordUrl = "https://www.abccar.com.tw/apiv2/"

        val instance: ApiService by lazy { ApiClient().getClient() }

        val forgetPasswordInstance: ForgetPasswordApiService by lazy{ApiClient().getForgetPasswordClient()}

    }
}