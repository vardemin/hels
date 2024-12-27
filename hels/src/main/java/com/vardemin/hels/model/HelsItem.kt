package com.vardemin.hels.model

interface HelsItem {
    val id: String
}

interface HelsItemWithSession : HelsItem {
    val sessionId: String
}