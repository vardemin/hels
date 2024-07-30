package com.vardemin.hels.model.log

import com.vardemin.hels.proto.LogEntryLevel

enum class LogLevel {
    Verbose,
    Debug,
    Info,
    Warning,
    Error,
    Fatal,
    Silent
}

fun convertLogLevel(entryLevel: LogEntryLevel): LogLevel =
    when (entryLevel) {
        LogEntryLevel.Info -> LogLevel.Info
        LogEntryLevel.Verbose -> LogLevel.Verbose
        LogEntryLevel.Debug -> LogLevel.Debug
        LogEntryLevel.Warning -> LogLevel.Warning
        LogEntryLevel.Error -> LogLevel.Error
        LogEntryLevel.Fatal -> LogLevel.Fatal
        LogEntryLevel.Silent -> LogLevel.Silent
    }