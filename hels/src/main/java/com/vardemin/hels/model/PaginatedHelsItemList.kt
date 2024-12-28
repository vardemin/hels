package com.vardemin.hels.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedHelsItemList<out T: HelsItem>(
    val data: List<T>,
    val page: Int,
    val perPage: Int,
    val totalPages: Int
)