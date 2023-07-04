package com.vardemin.hels.log

import com.vardemin.hels.utils.CircularArray

internal class Logger(size: Int) {
    private val array = CircularArray<>(size)

}