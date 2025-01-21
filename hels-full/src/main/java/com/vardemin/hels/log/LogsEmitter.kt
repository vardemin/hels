package com.vardemin.hels.log

import com.vardemin.hels.model.log.LogLevel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal object LogsEmitter : HLogger {
    private const val REPLAY_CACHE_SIZE = 512
    private const val BUFFER_SIZE = 512

    private val mutableCommandFlow = MutableSharedFlow<LoggerCommand>(
        replay = REPLAY_CACHE_SIZE,
        extraBufferCapacity = BUFFER_SIZE
    )

    val commandFlow = mutableCommandFlow.asSharedFlow()

    override fun d(tag: String, message: String) {
        pushLog(LogLevel.Debug, tag, message)
    }

    override fun v(tag: String, message: String) {
        pushLog(LogLevel.Verbose, tag, message)
    }

    override fun i(tag: String, message: String) {
        pushLog(LogLevel.Info, tag, message)
    }

    override fun e(tag: String, message: String) {
        pushLog(LogLevel.Error, tag, message)
    }

    override fun e(tag: String, throwable: Throwable?) {
        pushLog(LogLevel.Error, tag, throwable?.message ?: "Some error occurred")
    }

    override fun event(title: String, message: String, properties: Map<String, String>) {
        mutableCommandFlow.tryEmit(LoggerCommand.PushEvent(title, message, properties))
    }

    override fun setAttributes(vararg attrs: Pair<String, String>) {
        mutableCommandFlow.tryEmit(LoggerCommand.SetAttributes(attrs.toMap()))
    }

    private fun pushLog(
        level: LogLevel,
        tag: String,
        message: String
    ) {
        mutableCommandFlow.tryEmit(LoggerCommand.PushLog(level, tag, message))
    }
}