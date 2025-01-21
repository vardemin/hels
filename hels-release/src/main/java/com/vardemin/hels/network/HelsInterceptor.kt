package com.vardemin.hels.network

import okhttp3.Interceptor
import okhttp3.Response

class HelsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }
}