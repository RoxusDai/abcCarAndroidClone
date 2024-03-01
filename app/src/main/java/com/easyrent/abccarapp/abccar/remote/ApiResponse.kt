package com.easyrent.abccarapp.abccar.remote

sealed class ApiResponse<T>() {
    class Success<T>(
        val data: T
    ): ApiResponse<T>()

    class ApiFailure<T>(
        val errorCode: String,
        val message: String,
    ): ApiResponse<T>()

    class NetworkError<T>(
        val statusCode: String,
        val message: String
    ) : ApiResponse<T>()

}
