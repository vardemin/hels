package com.vardemin.hels.di

import com.vardemin.hels.data.EventItemsDataSource
import com.vardemin.hels.data.HelsItemDataSource
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.data.SessionDataSource

internal class ComponentsModule(
    val dataModule: DataModule,
    additionalDataSource: List<HelsItemDataSource<*>> = emptyList()
) {
    val logItemsDataSource = LogItemsDataSource(dataModule)
    val requestsDataSource = RequestsDataSource(dataModule)
    val eventsDataSource = EventItemsDataSource(dataModule)
    val allDataSources =
        listOf(logItemsDataSource, requestsDataSource, eventsDataSource) + additionalDataSource
    val sessionDataSource = SessionDataSource(dataModule, allDataSources)
}