package com.easyrent.abccarapp.abccar.source

sealed class HttpResponse<T>(
    val httpMessage: String
) {
    class Success<T>(
        val statusCode: String,
        val payload: T
    ) : HttpResponse<T>(
        httpMessage = "Success"
    )

    class Failure<T>(
        val statusCode: String,
        message: String
    ) : HttpResponse<T>(
        httpMessage = message
    )


    companion object {
        fun getRespBodyNullMessage() = "Response body is null"
    }
}
