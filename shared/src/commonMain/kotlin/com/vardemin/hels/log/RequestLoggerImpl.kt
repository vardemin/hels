package com.vardemin.hels.log

import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.model.request.RequestData
import com.vardemin.hels.model.request.RequestErrorData
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.ResponseData
import com.vardemin.hels.utils.currentDateTime
import kotlinx.datetime.LocalDateTime

internal class RequestLoggerImpl(private val dataSource: RequestsDataSource) : HRequestLogger {
    private val items = mutableMapOf<String, RequestItem>()

    override fun generateId() = randomUUID()

    override fun logRequest(
        id: String,
        method: String,
        url: String,
        headers: Map<String, String>,
        payload: String?,
    ) {
        val requestData = RequestData(method, url, headers.toMultimap(), payload, getLocalDateTime())
        val requestItem = RequestItem(id, requestData, null, null)
        items[id] = requestItem
        dataSource.tryEmit(requestItem)
    }

    override fun logResponse(
        id: String,
        code: Int,
        headers: Map<String, String>,
        payload: String?,
    ) {
        val requestItem = items[id]
        requestItem?.let {
            val responseData = ResponseData(code, headers.toMultimap(), payload, getLocalDateTime())
            items[id] = requestItem.copy(response = responseData).also {
                dataSource.tryEmit(it)
            }
            items.remove(id)
        }
    }

    override fun logError(id: String, message: String) {
        val requestItem = items[id]
        requestItem?.let {
            val errorData = RequestErrorData(message, getLocalDateTime())
            items[id] = requestItem.copy(error = errorData).also {
                dataSource.tryEmit(it)
            }
            items.remove(id)
        }
    }

    private fun getLocalDateTime(): LocalDateTime {
        return currentDateTime()
    }
}

fun Map<String, String>.toMultimap(): Map<String, List<String>> {
    val result: MutableMap<String, MutableList<String>> = mutableMapOf()
    keys.forEach {
        val lowerCaseKey = it.lowercase()
        get(it)?.let { valueToAdd ->
            if (result.containsKey(lowerCaseKey)) {
                result[lowerCaseKey]?.add(valueToAdd)
            } else {
                val newList = mutableListOf<String>().apply { add(valueToAdd) }
                result[lowerCaseKey] = newList
            }
        }
    }
    return result
}

expect fun randomUUID(): String