package com.vardemin.hels.log

import android.util.Log
import com.vardemin.hels.command.InternalCommandHandler
import com.vardemin.hels.data.EventItemsDataSource
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.data.SessionDataSource
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.model.event.EventItem
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.utils.currentDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required

internal class LoggerCommandHandler(
    module: ComponentsModule
) : Injects<ComponentsModule>, InternalCommandHandler<LoggerCommand> {

    private val sessionDataSource: SessionDataSource by required { sessionDataSource }
    private val dataSource: LogItemsDataSource by required { logItemsDataSource }
    private val eventsDataSource: EventItemsDataSource by required { eventsDataSource }
    private val coroutineScope: CoroutineScope by required { dataModule.defaultScope }

    private val sessionId get() = sessionDataSource.currentSession?.id ?: ""

    init {
        inject(module)
        LogsEmitter.commandFlow.onEach {
            handle(it)
        }.catch {
            Log.e("HELS", it.localizedMessage, it)
        }.launchIn(coroutineScope)
    }

    override fun handle(command: LoggerCommand) {
        with(command) {
            when (this) {
                is LoggerCommand.PushEvent -> eventsDataSource.add(
                    sessionId,
                    EventItem(sessionId, title, message, getLocalDateTime(), properties)
                )

                is LoggerCommand.PushLog -> dataSource.add(
                    sessionId,
                    LogItem(sessionId, tag, message, getLocalDateTime(), level)
                )

                is LoggerCommand.SetAttributes -> sessionDataSource.updateCurrentProps {
                    putAll(attrs)
                }
            }
        }
    }

    private fun getLocalDateTime(): LocalDateTime {
        return currentDateTime()
    }
}