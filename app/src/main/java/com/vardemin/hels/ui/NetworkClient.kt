package com.vardemin.hels.ui

import com.vardemin.hels.network.HelsInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

object NetworkClient {

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(HelsInterceptor())
            .build()
    }

    fun makeCall() {
        scope.launch {
            try {
                val request = Request.Builder()
                    .url("https://catfact.ninja/fact")
                    .get()
                    .build()
                okHttpClient.newCall(request).execute()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }
}