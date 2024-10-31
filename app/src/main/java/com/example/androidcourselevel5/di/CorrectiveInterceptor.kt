package com.example.androidcourselevel5.di

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class CorrectiveInterceptor: Interceptor {

    // Object for print log message to Logcat
    private val logger by lazy {
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Platform.get().log(message)
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        // REQUEST log printing
        val request = chain.request()
        printRequestLogs(request)

        // RESPONSE logging and fixing
        val startNs = System.nanoTime()
        var response: Response

        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
          logger.log("<-- HTTP FAILED: $e")
          throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            logger.log("<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)")

        val responseHeaders = response.headers
        responseHeaders.forEach {
            logger.log("${it.first}: ${it.second}")
        }

    // need for printing response.body to Logcat
    // second variant -> response.peekBody(Long.MAX_VALUE).string()
        val responseBody = response.body!!
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body
        val buffer = source.buffer
        val charset= responseBody.contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

        if (responseBody.contentLength() != 0L) {
            logger.log("body: ${buffer.clone().readString(charset)}")

            val bodyData = response.peekBody(Long.MAX_VALUE).string()
            val fixedBody = fixResponseBody(bodyData)

            response = response.newBuilder()
                .body(fixedBody.toResponseBody())
                .build()
        }

        logger.log("<-- END HTTP (${buffer.size}-byte body)")

        return response
    }

    private fun printRequestLogs(request: Request) {
        val requestBody = request.body

        val requestStartMessage = "--> ${request.method} ${request.url}"
        logger.log(requestStartMessage)

        if (requestBody != null) {
            val contentTypeMessage = "Content-Type: ${requestBody.contentType()}"
                logger.log(contentTypeMessage)
            val contentLengthMessage = "Content-Length: ${requestBody.contentLength()}"
                logger.log(contentLengthMessage)
        }

        val requestHeaders = request.headers
        requestHeaders.forEach {
            logger.log("${it.first}: ${it.second}")
        }

        if (requestBody != null) {
            logger.log("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
        } else {
            logger.log("--> END ${request.method}")
        }
    }

    private fun fixResponseBody(data: String): String {
        return if (data.takeLast(2) == "}}") {
            data
        } else {
            data.plus('}')
        }
    }

}
