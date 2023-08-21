package com.vardemin.hels.server

import android.content.Context
import androidx.annotation.RawRes
import com.vardemin.hels.R

internal class ServerConfig(
    context: Context,
    val port: Int = 1515,
    @RawRes indexHtmlRes: Int = R.raw.index,
    @RawRes cssRes: Int = R.raw.bulma
) {
    val indexBytes by lazy {
        context.resources.openRawResource(indexHtmlRes).readBytes()
    }

    val cssBytes by lazy {
        context.resources.openRawResource(cssRes).readBytes()
    }
}