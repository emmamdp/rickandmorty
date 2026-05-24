package com.emdp.rickandmorty.core.ui.image

import okhttp3.Interceptor
import okhttp3.Response

class CoilInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Thread.sleep(DELAY_BETWEEN_REQUESTS)
        
        val request = chain.request().newBuilder()
            .header("User-Agent", USER_AGENT)
            .build()
        var response = chain.proceed(request)
        var retryCount = 0

        while (response.code == 429 && retryCount < MAX_RETRIES) {
            response.close()
            Thread.sleep(RETRY_DELAY * (retryCount + 1))
            response = chain.proceed(request)
            retryCount++
        }
        
        return response
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
        private const val DELAY_BETWEEN_REQUESTS = 200L
        private const val RETRY_DELAY = 1000L
        private const val MAX_RETRIES = 2
    }
}
