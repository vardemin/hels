package com.vardemin.hels.network

import com.vardemin.hels.Hels
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
import java.util.TreeMap

class HelsInterceptor(
    private val maxBodySize: Long = HELS_MAX_BODY_DEFAULT_SIZE
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBody = request.body
        val headers = request.headers
        var bodyString: String? = null
        if (requestBody != null &&
            requestBody.contentLength() in 1..maxBodySize &&
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
        val requestId = Hels.logRequest(
            request.method,
            request.url.toString(),
            headers.toMap(),
            bodyString,
            currentDateTime()
        )

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Hels.logRequestError(requestId, e.message ?: "Unknown error", currentDateTime())
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


            if (contentLength in 1..maxBodySize) {
                val charset: Charset = responseBody.contentType().charsetOrUtf8()
                responseString = buffer.clone().readString(charset)
            }
        }
        Hels.logResponse(
            requestId,
            response.code,
            responseHeaders.toMap(),
            responseString,
            endTime
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

    private fun Headers.toMap(): Map<String, List<String>> {
        val result = TreeMap<String, MutableList<String>>(String.CASE_INSENSITIVE_ORDER)
        for (i in 0 until size) {
            val name = name(i).lowercase()
            var values: MutableList<String>? = result[name]
            if (values == null) {
                values = mutableListOf()
                result[name] = values
            }
            value(i).takeIf { it.isNotBlank() }?.let {
                values.add(it)
            }
        }
        return result
    }

    companion object {
        private const val HELS_MAX_BODY_DEFAULT_SIZE = 1024L
    }
}