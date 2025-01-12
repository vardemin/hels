package com.vardemin.hels.data

import android.util.Log
import com.vardemin.hels.model.HelsGenericOperation
import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.model.HelsOperation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class HelsItemDataSource<Item : HelsItem>(
    val apiPath: String,
    val wsPath: String,
    val json: Json,
    val serializer: KSerializer<Item>
) : CoroutineScope {

    open val defaultItemsCount: Int = HELS_DEFAULT_ITEMS_PER_PAGE

    protected val mutableOperationFlow: MutableSharedFlow<HelsOperation> =
        MutableSharedFlow(extraBufferCapacity = HELS_EXTRA_BUFFER_CAPACITY)
    val operationFlow: SharedFlow<HelsOperation> = mutableOperationFlow.asSharedFlow()

    abstract suspend fun getPaginated(
        after: LocalDateTime? = null,
        perPage: Int = HELS_DEFAULT_ITEMS_PER_PAGE
    ): List<Item>

    abstract suspend fun getById(
        id: String
    ): Item?

    protected abstract suspend fun onAddItem(sessionId: String, item: Item): Item
    protected abstract suspend fun onUpdateItem(
        sessionId: String,
        id: String,
        update: (Item) -> Item
    ): Item?

    protected abstract suspend fun onRemoveItem(sessionId: String, id: String)
    protected abstract suspend fun onReset(sessionId: String)

    fun add(sessionId: String, item: Item) {
        launch {
            val itemToReturn = onAddItem(sessionId, item)
            mutableOperationFlow.emit(HelsOperation.Add(sessionId, itemToReturn))
        }
    }

    fun update(sessionId: String, id: String, update: (Item) -> Item) {
        launch {
            onUpdateItem(sessionId, id, update)?.let { itemToReturn ->
                mutableOperationFlow.emit(HelsOperation.Update(sessionId, itemToReturn))
            }
        }
    }

    fun remove(sessionId: String, id: String) {
        launch {
            onRemoveItem(sessionId, id)
            mutableOperationFlow.emit(HelsOperation.Remove(sessionId, id))
        }
    }

    suspend fun reset(sessionId: String) {
        onReset(sessionId)
        mutableOperationFlow.emit(HelsOperation.Reset(sessionId))
    }

    public fun configureRouting(route: Route) {
        with(route) {
            get("$API_VERSION/$apiPath") {
                runCatching {
                    val lastDate = call.request.queryParameters["last"]?.takeIf { it.isNotEmpty() }
                        ?.toLocalDateTime()
                    val items = call.request.queryParameters["items"]?.toIntOrNull()
                        ?: defaultItemsCount
                    val result = getPaginated(lastDate, items)
                    call.respondText(
                        json.encodeToString(ListSerializer(serializer), result),
                        ContentType.Application.Json
                    )
                }.onFailure {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        it.message ?: "Some error occurred"
                    )
                }
            }
            get("$API_VERSION/$apiPath/{id}") {
                val id = call.parameters["id"] ?: ""
                runCatching {
                    getById(id)?.let {
                        call.respondText(
                            json.encodeToString(serializer, it),
                            ContentType.Application.Json
                        )
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        "Item with id $id not found"
                    )
                }.onFailure {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        it.message ?: "Some error occurred"
                    )
                }
            }
            webSocket(wsPath) {
                try {
                    operationFlow.collect {
                        val jsonEncoded = if (it is HelsGenericOperation) {
                            it.toJson(json, serializer)
                        } else {
                            json.encodeToString(it)
                        }
                        send(Frame.Text(jsonEncoded))
                    }
                } catch (e: Exception) {
                    val errorMessage = e.message ?: "Error"
                    close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, errorMessage))
                    Log.d("HELS", errorMessage)
                }
            }
        }
    }

    companion object {
        const val API_VERSION = "/api/v1"
        const val HELS_DEFAULT_ITEMS_PER_PAGE = 48
        const val HELS_EXTRA_BUFFER_CAPACITY = 1024
    }
}