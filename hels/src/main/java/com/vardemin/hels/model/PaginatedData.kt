package com.vardemin.hels.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedData<T>(
    val page: Int,
    val items: Int,
    val data: List<T>
)