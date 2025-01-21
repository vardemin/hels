package com.vardemin.hels.model

import com.vardemin.hels.data.local.HelsEntity

internal interface HelsMapper<Entity: HelsEntity, Item: HelsItem> {
    fun mapItem(entity: Entity): Item
    fun mapDb(item: Item): Entity
}