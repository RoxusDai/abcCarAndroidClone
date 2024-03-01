package com.easyrent.abccarapp.abccar.source

import retrofit2.Call

object HttpResponseTransfer {
    fun <T> transform(call: Call<T>): HttpResponse<T> {
        return try {
            val resp = call.execute()

            if (resp.isSuccessful) {
                resp.body()?.let {
                    HttpResponse.Success(
                        resp.code().toString(),
                        it
                    )
                } ?: HttpResponse.Failure(
                    statusCode = resp.code().toString(),
                    message = HttpResponse.getRespBodyNullMessage()
                )
            } else {
                HttpResponse.Failure(
                    statusCode = resp.code().toString(),
                    message = resp.errorBody()?.string() ?: "Empty Error Message"
                )
            }
        } catch (e: Exception) {
            HttpResponse.Failure(
                statusCode = "-1",
                message = "Network Error ${e.message}"
            )
        }
    }
}