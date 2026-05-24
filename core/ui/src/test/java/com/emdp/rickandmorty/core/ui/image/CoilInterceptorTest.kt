package com.emdp.rickandmorty.core.ui.image

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

internal class CoilInterceptorTest {

    private val sut = CoilInterceptor()
    private val chain: Interceptor.Chain = mock()

    @Test
    fun `intercept adds User-Agent header`() {
        val request = Request.Builder().url("https://img.png").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(any())).thenReturn(response)

        sut.intercept(chain)

        verify(chain).proceed(org.mockito.kotlin.check { 
            assertEquals("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36", it.header("User-Agent"))
        })
    }

    @Test
    fun `intercept retries on 429 error`() {
        val request = Request.Builder().url("https://img.png").build()
        val errorResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(429)
            .message("Too Many Requests")
            .build()
        val successResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(any()))
            .thenReturn(errorResponse)
            .thenReturn(successResponse)

        sut.intercept(chain)

        verify(chain, times(2)).proceed(any())
    }
}
