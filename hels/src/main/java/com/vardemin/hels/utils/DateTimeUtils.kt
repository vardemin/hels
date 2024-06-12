package com.vardemin.hels.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

internal fun nowInstant(): Instant {
    return Clock.System.now()
}

internal fun currentDateTime(): LocalDateTime {
    return nowInstant().toLocalDateTime(TimeZone.currentSystemDefault())
}

internal fun LocalDateTime.plus(duration: Duration): Instant {
    return toInstant(TimeZone.currentSystemDefault()).plus(duration)
}