package com.vardemin.hels.log

import com.vardemin.hels.command.InternalCommand
import com.vardemin.hels.model.log.LogLevel

internal sealed interface LoggerCommand : InternalCommand {
    data class PushLog(
        val level: LogLevel,
        val tag: String,
        val message: String
    ) : LoggerCommand

    class PushEvent(
        val title: String,
        val message: String,
        val properties: Map<String, String>
    ) : LoggerCommand

    class SetAttributes(
        val attrs: Map<String, String>
    ) : LoggerCommand
}