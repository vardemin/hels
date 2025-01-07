package com.vardemin.hels.log

object EmptyLogger : HLogger {

    override fun d(tag: String, message: String) {
        // No op
    }

    override fun v(tag: String, message: String) {
        // No op
    }

    override fun i(tag: String, message: String) {
        // No op
    }

    override fun e(tag: String, message: String) {
        // No op
    }

    override fun e(tag: String, throwable: Throwable?) {
        // No op
    }

    override fun event(title: String, message: String, properties: Map<String, String>) {
        // No op
    }
}