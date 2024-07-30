package com.vardemin.hels.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoroutineScope.ticker(
    tickInMillis: Long,
    onTick: () -> Unit
) {
    this.launch(Dispatchers.Default) {
        while (true) {
            withContext(Dispatchers.Main) { onTick() }
            delay(tickInMillis)
        }
    }
}