package com.vardemin.hels.data

import android.content.SharedPreferences
import android.util.Log
import com.vardemin.hels.data.HelsItemDataSource.Companion.API_VERSION
import com.vardemin.hels.data.db.dao.SessionsDao
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.model.mapper.HelsSessionMapper
import com.vardemin.hels.model.session.SessionItem
import com.vardemin.hels.utils.mapEntity
import com.vardemin.hels.utils.mapItem
import com.vardemin.hels.utils.mapItemList
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required
import kotlin.coroutines.CoroutineContext

internal class SessionDataSource(
    module: DataModule,
    private val itemDataSources: List<HelsItemDataSource<*>>
) : Injects<DataModule>, CoroutineScope {
    private val apiPath: String = "$API_VERSION/sessions"
    private val apiCurrentPath: String = "$API_VERSION/session"
    private val wsPath: String = "/ws/session"
    override val coroutineContext: CoroutineContext by required { defaultCoroutineContext }
    private val dao: SessionsDao by required { sessionsDao }
    private val mapper: HelsSessionMapper by required { sessionsMapper }
    private val sharedPreferences: SharedPreferences by required { sharedPreferences }
    private val json: Json by required { json }

    private val mutableSessionFlow: MutableSharedFlow<SessionItem> =
        MutableSharedFlow(extraBufferCapacity = EXTRA_BUFFER_CAPACITY)
    val sessionsFlow: SharedFlow<SessionItem> = mutableSessionFlow.asSharedFlow()

    var sessionId: String
        get() = sharedPreferences.getString(SESSION_ID_KEY, "") ?: ""
        set(value) {
            sharedPreferences.edit().putString(SESSION_ID_KEY, value).apply()
        }

    var previousSessionId: String
        get() = sharedPreferences.getString(PREVIOUS_SESSION_ID_KEY, "") ?: ""
        set(value) {
            sharedPreferences.edit().putString(PREVIOUS_SESSION_ID_KEY, value).apply()
        }

    var currentSession: SessionItem? = null

    init {
        inject(module)
    }

    suspend fun getSessions(): List<SessionItem> {
        return dao.getSessions().mapItemList(mapper)
    }

    suspend fun getCurrentSession(): SessionItem? {
        return currentSession ?: run {
            val currentSessionId: String = sessionId
            if (currentSessionId.isNotEmpty()) {
                null
            } else dao.getSessionById(currentSessionId).mapItem(mapper)
        }
    }

    suspend fun applyLastSession(initProps: Map<String, String> = emptyMap()) {
        getCurrentSession()?.let {
            val targetSession = if (initProps.isNotEmpty()) {
                val newProperties = it.properties.toMutableMap().apply {
                    initProps.entries.forEach { (key, value) ->
                        this[key] = value
                    }
                }
                val newSession = it.copy(properties = newProperties)
                update(newSession)
                newSession
            } else it
            currentSession = targetSession
            mutableSessionFlow.emit(targetSession)
        } ?: startNewSession(initProps)
    }

    suspend fun startNewSession(initProps: Map<String, String> = emptyMap()): SessionItem {
        previousSessionId.takeIf { it.isNotEmpty() }?.let {
            delete(it)
        }
        val currentSessionId = sessionId
        val newSession = SessionItem(properties = initProps)
        dao.insertSession(newSession.mapEntity(mapper))
        sessionId = newSession.id
        previousSessionId = currentSessionId
        currentSession = newSession
        mutableSessionFlow.emit(newSession)
        return newSession
    }

    private suspend fun update(sessionItem: SessionItem) {
        dao.updateSession(sessionItem.mapEntity(mapper))
        if (sessionItem.id == currentSession?.id) {
            currentSession = sessionItem
        }
        mutableSessionFlow.emit(sessionItem)
    }

    fun updateCurrentProps(updater: MutableMap<String, String>.() -> Unit) {
        launch {
            getCurrentSession()?.let {
                val newProperties = it.properties.toMutableMap().apply(updater)
                update(it.copy(properties = newProperties))
            }
        }
    }

    private suspend fun delete(id: String) {
        dao.deleteSessionById(id)
        itemDataSources.forEach {
            it.reset(id)
        }
    }

    fun configureRouting(route: Route) {
        with(route) {
            get(apiPath) {
                runCatching {
                    getSessions().takeIf { it.isNotEmpty() }?.let {
                        call.respond(it)
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        "Session is not configured yet"
                    )
                }.onFailure {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        it.message ?: "Some error occurred"
                    )
                }
            }
            get(apiCurrentPath) {
                runCatching {
                    getCurrentSession()?.let {
                        call.respond(it)
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        "Session is not configured yet"
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
                    sessionsFlow.collect {
                        val jsonEncoded: String = json.encodeToString(it)
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
        private const val EXTRA_BUFFER_CAPACITY = 16
        private const val SESSION_ID_KEY = "session_id"
        private const val PREVIOUS_SESSION_ID_KEY = "previous_session_id"
        const val SESSION_PROP_DEVICE = "device"
        const val SESSION_PROP_OS = "operation_system"
        const val SESSION_DEVICE_ID = "device_id"
        const val SESSION_USER_ID = "user_id"
    }
}