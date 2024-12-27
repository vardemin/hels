package com.vardemin.hels.model.session

import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.utils.currentDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SessionItem(
    override val id: String = UUID.randomUUID().toString(),
    val started: LocalDateTime = currentDateTime(),
    val properties: Map<String, String> = mapOf()
) : HelsItem