package com.vardemin.hels.log

interface HelsLogger {

    fun d(tag: String, message: String, properties: Map<String, String>)

    fun v(tag: String, message: String, properties: Map<String, String>)

    fun i(tag: String, message: String, properties: Map<String, String>)
}