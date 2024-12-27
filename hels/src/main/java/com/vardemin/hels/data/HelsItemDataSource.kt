package com.vardemin.hels.data

import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.model.HelsOperation
import com.vardemin.hels.model.PaginatedHelsItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

abstract class HelsItemDataSource<Item : HelsItem>(
    val apiPath: String,
    val wsPath: String,
    private val serializer: KSerializer<Item>
) : CoroutineScope {
    protected val mutableOperationFlow: MutableSharedFlow<HelsOperation> =
        MutableSharedFlow(extraBufferCapacity = HELS_EXTRA_BUFFER_CAPACITY)
    val operationFlow: SharedFlow<HelsOperation> = mutableOperationFlow.asSharedFlow()

    abstract suspend fun getPaginated(
        sessionId: String,
        page: Int,
        perPage: Int = HELS_DEFAULT_ITEMS_PER_PAGE
    ): PaginatedHelsItemList<Item>

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
            mutableOperationFlow.emit(HelsOperation.Add(sessionId, itemToReturn, serializer))
        }
    }

    fun update(sessionId: String, id: String, update: (Item) -> Item) {
        launch {
            onUpdateItem(sessionId, id, update)?.let { itemToReturn ->
                mutableOperationFlow.emit(HelsOperation.Update(sessionId, itemToReturn, serializer))
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

    companion object {
        const val HELS_DEFAULT_ITEMS_PER_PAGE = 48
        const val HELS_EXTRA_BUFFER_CAPACITY = 1024
    }
}