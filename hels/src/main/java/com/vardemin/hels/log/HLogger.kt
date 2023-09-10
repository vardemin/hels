package com.vardemin.hels.log

interface HLogger {

    fun d(tag: String, message: String, properties: Map<String, String> = mapOf())

    fun v(tag: String, message: String, properties: Map<String, String> = mapOf())

    fun i(tag: String, message: String, properties: Map<String, String> = mapOf())
}