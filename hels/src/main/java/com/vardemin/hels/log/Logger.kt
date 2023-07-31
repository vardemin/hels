package com.vardemin.hels.log

import com.vardemin.hels.model.LogItem
import com.vardemin.hels.utils.CircularArray

internal class Logger(size: Int) {
    private val array = CircularArray<LogItem>(size)

    fun add(item: LogItem) {
        array.add(item)
    }

}