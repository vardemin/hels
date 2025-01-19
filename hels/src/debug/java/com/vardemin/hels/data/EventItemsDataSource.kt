package com.vardemin.hels.data

import com.vardemin.hels.data.db.dao.EventsDao
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.model.event.EventItem
import com.vardemin.hels.model.mapper.HelsEventsMapper
import com.vardemin.hels.utils.mapEntity
import com.vardemin.hels.utils.mapItem
import com.vardemin.hels.utils.mapItemList
import com.vardemin.hels.utils.toLong
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required
import kotlin.coroutines.CoroutineContext

internal class EventItemsDataSource(
    module: DataModule
) : Injects<DataModule>, HelsItemDataSource<EventItem>(
    "/events",
    "/ws/events",
    module.json,
    EventItem.serializer()
) {

    override val coroutineContext: CoroutineContext by required { defaultCoroutineContext }
    private val dao: EventsDao by required { eventsDao }
    private val mapper: HelsEventsMapper by required { eventsMapper }

    init {
        inject(module)
    }

    override suspend fun getPaginated(
        after: LocalDateTime?,
        perPage: Int
    ): List<EventItem> {
        val events = if (after != null) {
            dao.getEventsAfter(perPage, after.toLong())
        } else {
            dao.getEvents(perPage)
        }
        return events.mapItemList(mapper)
    }

    override suspend fun getById(id: String): EventItem? {
        return dao.getEventById(id)?.mapItem(mapper)
    }

    override suspend fun onRemoveItem(sessionId: String, id: String) {
        dao.deleteById(id)
    }

    override suspend fun onReset(sessionId: String) {
        dao.clearEventsForSession(sessionId)
    }

    override suspend fun onUpdateItem(
        sessionId: String,
        id: String,
        update: (EventItem) -> EventItem
    ): EventItem? {
        return getById(id)?.run(update)?.also {
            dao.updateEvent(it.mapEntity(mapper))
        }
    }

    override suspend fun onAddItem(sessionId: String, item: EventItem): EventItem {
        dao.insertEvents(item.mapEntity(mapper))
        return item
    }
}