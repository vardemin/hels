package com.vardemin.hels.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface HelsOperation {
    val sessionId: String
    val operation: String

    @Serializable
    @SerialName("reset")
    data class Reset(
        override val sessionId: String,
        override val operation: String = "reset"
    ): HelsOperation

    @Serializable
    @SerialName("add")
    data class Add<Item: HelsItem>(
        override val sessionId: String,
        val data: Item,
        override val operation: String = "add"
    ) : HelsOperation, HelsGenericOperation {
        override fun toJson(json: Json, serializer: KSerializer<*>): String {
            return json.encodeToString(Add.serializer(serializer as KSerializer<Item>) ,this)
        }
    }

    @Serializable
    @SerialName("update")
    data class Update<Item: HelsItem>(
        override val sessionId: String,
        val data: Item,
        override val operation: String = "update"
    ) : HelsOperation, HelsGenericOperation {
        override fun toJson(json: Json, serializer: KSerializer<*>): String {
            return json.encodeToString(serializer(serializer as KSerializer<Item>) ,this)
        }
    }

    @Serializable
    @SerialName("remove")
    data class Remove(
        override val sessionId: String,
        val id: String,
        override val operation: String = "remove"
    ) : HelsOperation
}

interface HelsGenericOperation {
    fun toJson(json: Json, serializer: KSerializer<*>): String
}