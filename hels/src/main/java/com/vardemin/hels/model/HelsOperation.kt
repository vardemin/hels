package com.vardemin.hels.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed interface HelsOperation {
    val sessionId: String
    fun toJson(json: Json): String

    @Serializable
    @SerialName("reset")
    data class Reset(
        override val sessionId: String
    ): HelsOperation {
        override fun toJson(json: Json): String {
            return json.encodeToString(this)
        }
    }

    @Serializable
    @SerialName("add")
    data class Add<Item: HelsItem>(
        override val sessionId: String,
        val data: Item,
        val serializer: KSerializer<Item>
    ) : HelsOperation {
        override fun toJson(json: Json): String {
            return json.encodeToString(Add.serializer(serializer), this)
        }
    }

    @Serializable
    @SerialName("add")
    data class Update<Item: HelsItem>(
        override val sessionId: String,
        val data: Item,
        val serializer: KSerializer<Item>
    ) : HelsOperation {
        override fun toJson(json: Json): String {
            return json.encodeToString(Update.serializer(serializer), this)
        }
    }

    @Serializable
    @SerialName("add")
    data class Remove(
        override val sessionId: String,
        val id: String
    ) : HelsOperation {
        override fun toJson(json: Json): String {
            return json.encodeToString(Remove.serializer(), this)
        }
    }

}