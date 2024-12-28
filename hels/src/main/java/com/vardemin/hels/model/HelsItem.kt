package com.vardemin.hels.model

import kotlinx.serialization.Serializable

@Serializable
abstract class HelsItem {
    abstract val id: String
}

@Serializable
abstract class HelsItemWithSession: HelsItem() {
    abstract val sessionId: String
}