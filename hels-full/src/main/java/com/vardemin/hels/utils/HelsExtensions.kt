package com.vardemin.hels.utils

import com.vardemin.hels.data.local.HelsEntity
import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.model.HelsMapper

internal fun <Item : HelsItem, Entity : HelsEntity> Item.mapEntity(mapper: HelsMapper<Entity, Item>): Entity {
    return mapper.mapDb(this)
}

internal fun <Item : HelsItem, Entity : HelsEntity> List<Item>.mapEntityList(mapper: HelsMapper<Entity, Item>): List<Entity> {
    return map { mapper.mapDb(it) }
}

internal fun <Item : HelsItem, Entity : HelsEntity> Entity.mapItem(mapper: HelsMapper<Entity, Item>): Item {
    return mapper.mapItem(this)
}

internal fun <Item : HelsItem, Entity : HelsEntity> List<Entity>.mapItemList(mapper: HelsMapper<Entity, Item>): List<Item> {
    return map { mapper.mapItem(it) }
}