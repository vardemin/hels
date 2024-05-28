package com.vardemin.hels.network

import com.vardemin.hels.model.request.RequestData
import com.vardemin.hels.model.request.RequestErrorData
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.ResponseData
import com.vardemin.hels.requests
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.EOFException
import java.nio.charset.Charset
import java.util.UUID

class HelsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBody = request.body
        val headers = request.headers
        val requestId = UUID.randomUUID().toString()
        var bodyString: String? = null
        if (requestBody != null &&
            !bodyHasUnknownEncoding(request.headers) &&
            !requestBody.isDuplex() &&
            !requestBody.isOneShot() &&
            !"gzip".equals(headers["Content-Encoding"], ignoreCase = true)
        ) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            if (!buffer.isProbablyUtf8()) {
                val charset: Charset = requestBody.contentType().charsetOrUtf8()
                bodyString = buffer.readString(charset)
            }
        }
        val now = currentDateTime()
        val requestItem = RequestItem(
            requestId,
            RequestData(
                request.method,
                request.url.toString(),
                headers.toMultimap(),
                bodyString,
                now
            ),
            null,
            null
        )
        requests.tryEmit(requestItem)
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            requests.tryEmit(
                requestItem.copy(
                    error = RequestErrorData(e.message ?: "Unknown error", currentDateTime())
                )
            )
            throw e
        }
        val endTime = currentDateTime()
        val responseHeaders = response.headers
        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()

        var responseString: String? = null
        if (response.promisesBody() &&
            !bodyHasUnknownEncoding(response.headers) &&
            !bodyIsStreaming(response) &&
            !"gzip".equals(responseHeaders["Content-Encoding"], ignoreCase = true)
        ) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer


            if (contentLength != 0L) {
                val charset: Charset = responseBody.contentType().charsetOrUtf8()
                responseString = buffer.clone().readString(charset)
            }
        }
        requests.tryEmit(
            requestItem.copy(
                response = ResponseData(
                    response.code,
                    responseHeaders.toMultimap(),
                    responseString,
                    endTime
                )
            )
        )
        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun bodyIsStreaming(response: Response): Boolean {
        val contentType = response.body?.contentType()
        return contentType != null && contentType.type == "text" && contentType.subtype == "event-stream"
    }

    private fun MediaType?.charsetOrUtf8(): Charset {
        return this?.charset() ?: Charsets.UTF_8
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }

    private fun currentDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}