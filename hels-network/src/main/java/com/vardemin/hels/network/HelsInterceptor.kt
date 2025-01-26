package com.vardemin.hels.network

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.EOFException
import okio.GzipSource
import java.nio.charset.Charset
import java.util.TreeMap

/**
 * Hels implementation on OkHttp3 Interceptor
 * @param networkLogger Logger instance (Hels object from full or release version)
 * @param isEnabled whether to process requests (usually false on release)
 * @param maxBodySize Consider request and response bodies only with the following limit in bytes.
 */
class HelsInterceptor(
    private val networkLogger: HNetworkLogger,
    private val isEnabled: Boolean,
    private val maxBodySize: Long = HELS_MAX_BODY_DEFAULT_SIZE
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!isEnabled) return chain.proceed(request)

        val requestBody = request.body
        val headers = request.headers
        var bodyString: String? = null
        var totalRequestBodySize = 0L
        if (requestBody != null &&
            !bodyHasUnknownEncoding(request.headers) &&
            !requestBody.isDuplex() &&
            !requestBody.isOneShot()
        ) {
            var buffer = Buffer()
            requestBody.writeTo(buffer)

            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
                totalRequestBodySize = buffer.size
            } else {
                totalRequestBodySize = requestBody.contentLength()
            }

            if (buffer.isProbablyUtf8() && checkBodySize(buffer.size)) {
                val charset: Charset = requestBody.contentType().charsetOrUtf8()
                bodyString = buffer.readString(charset)
            }
        }
        val requestId = networkLogger.logRequest(
            request.method,
            request.url.toString(),
            headers.toMap(),
            maxOf(totalRequestBodySize, 0L),
            bodyString,
            currentDateTime()
        )

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            networkLogger.logRequestError(requestId, e.message ?: "Unknown error", currentDateTime())
            throw e
        }
        val endTime = currentDateTime()
        val responseHeaders = response.headers
        val responseBody = response.body!!
        var totalResponseBodySize = responseBody.contentLength()
        var responseString: String? = null
        if (response.promisesBody() &&
            !bodyHasUnknownEncoding(responseHeaders) &&
            !bodyIsStreaming(response)
        ) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            if ("gzip".equals(responseHeaders["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }
            totalResponseBodySize = buffer.size

            if (buffer.isProbablyUtf8() && checkBodySize(totalResponseBodySize)) {
                val charset: Charset = responseBody.contentType().charsetOrUtf8()
                responseString = buffer.clone().readString(charset)
            }
        }
        networkLogger.logResponse(
            requestId,
            response.code,
            responseHeaders.toMap(),
            maxOf(totalResponseBodySize, 0L),
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

    private fun currentDateTime(): Long {
        return System.currentTimeMillis()
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

    private fun checkBodySize(bodySize: Long) : Boolean {
        return if (maxBodySize > 0) {
            bodySize in 1..maxBodySize
        } else true
    }

    companion object {
        const val HELS_MAX_BODY_DEFAULT_SIZE = 1024L
    }
}