package com.vardemin.hels.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
abstract class HelsItem {
    abstract val id: String
    abstract val time: LocalDateTime
}

@Serializable
abstract class HelsItemWithSession: HelsItem() {
    abstract val sessionId: String
}