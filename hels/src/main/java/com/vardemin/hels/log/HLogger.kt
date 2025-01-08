package com.vardemin.hels.log

interface HLogger {

    /**
     * Log debug level message
     */
    fun d(tag: String, message: String)

    /**
     * Log verbose level message
     */
    fun v(tag: String, message: String)

    /**
     * Log info level message
     */
    fun i(tag: String, message: String)

    /**
     * Log error message
     */
    fun e(tag: String, message: String)

    /**
     * Log throwable error
     */
    fun e(tag: String, throwable: Throwable?)

    /**
     * Log event
     * @param properties specific to target event
     */
    fun event(title: String, message: String, properties: Map<String, String> = mapOf())

    /**
     * Set current session attributes
     * @param attrs to set
     */
    fun setAttributes(vararg attrs: Pair<String, String>)
}