package com.emdp.rickandmorty.data.common.network

import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkConfig.Companion.DEFAULT_BASE_URL
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkProvider.defaultMoshi
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkProvider.provideOkHttp
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkProvider.provideRetrofit
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

internal class RickAndMortyNetworkProviderTest {

    @Test
    fun shouldApplyTimeoutsFromConfig() {
        val cfg = RickAndMortyNetworkConfig(
            baseUrl = DEFAULT_BASE_URL,
            connectTimeoutMs = 1111,
            readTimeoutMs = 2222,
            writeTimeoutMs = 3333
        )
        val client = provideOkHttp(
            config = cfg,
            includeErrorInterceptor = false,
            enableLogging = false
        )

        assertEquals(1111, client.connectTimeoutMillis.toLong())
        assertEquals(2222, client.readTimeoutMillis.toLong())
        assertEquals(3333, client.writeTimeoutMillis.toLong())
    }

    @Test
    fun shouldIncludeLoggingInterceptorWhenEnabled() {
        val client = provideOkHttp(
            config = RickAndMortyNetworkConfig(),
            includeErrorInterceptor = false,
            enableLogging = true
        )
        assertTrue(client.interceptors.any { it is HttpLoggingInterceptor })
    }

    @Test
    fun shouldNotIncludeLoggingInterceptorWhenDisabled() {
        val client = provideOkHttp(
            config = RickAndMortyNetworkConfig(),
            includeErrorInterceptor = false,
            enableLogging = false
        )
        assertTrue(client.interceptors.none { it is HttpLoggingInterceptor })
    }

    @Test
    fun shouldIncludeErrorInterceptorByDefault() {
        val client = provideOkHttp(
            config = RickAndMortyNetworkConfig(),
            enableLogging = false
        )
        assertTrue(client.interceptors.any { it is RickAndMortyNetworkErrorInterceptor })
    }

    @Test
    fun shouldNotIncludeErrorInterceptorWhenDisabled() {
        val client = provideOkHttp(
            config = RickAndMortyNetworkConfig(),
            includeErrorInterceptor = false,
            enableLogging = false
        )
        assertTrue(client.interceptors.none { it is RickAndMortyNetworkErrorInterceptor })
    }

    @Test
    fun shouldRespectInterceptorsOrder_errorBeforeExtrasAndExtrasBeforeLogging() {
        val cfg = RickAndMortyNetworkConfig()
        val extraA = MarkerInterceptor()
        val client = provideOkHttp(
            config = cfg,
            extraInterceptors = listOf(extraA),
            includeErrorInterceptor = true,
            enableLogging = true
        )
        val list = client.interceptors
        val idxError = list.indexOfFirst { it is RickAndMortyNetworkErrorInterceptor }
        val idxA = list.indexOfFirst { it === extraA }
        val idxLogging = list.indexOfFirst { it is HttpLoggingInterceptor }

        assertTrue(idxError >= 0 && idxA >= 0 && idxLogging >= 0)
        assertTrue(idxError < idxA, "Error interceptor must be before extraA")
    }

    @Test
    fun shouldCreateRetrofitWithBaseUrlAndMoshiConverter_withoutOkHttp() {
        val cfg = RickAndMortyNetworkConfig(baseUrl = DEFAULT_BASE_URL)
        val noOpFactory: Call.Factory = Call.Factory {
            throw UnsupportedOperationException("no network")
        }
        val moshiFactory = MoshiConverterFactory.create(defaultMoshi)

        val retrofit = Retrofit.Builder()
            .baseUrl(cfg.baseUrl)
            .addConverterFactory(moshiFactory)
            .callFactory(noOpFactory)
            .build()

        assertEquals(cfg.baseUrl, retrofit.baseUrl().toString())
        assertTrue(retrofit.converterFactories().contains(moshiFactory))
    }

    @Test
    fun shouldBuildRetrofitWithBaseUrlAndInstallMoshiConverter() {
        val cfg = RickAndMortyNetworkConfig(baseUrl = DEFAULT_BASE_URL)
        val client = provideOkHttp(
            config = cfg,
            includeErrorInterceptor = false,
            enableLogging = false
        )
        val retrofit: Retrofit = provideRetrofit(
            config = cfg,
            client = client,
            moshi = defaultMoshi
        )

        assertEquals(cfg.baseUrl, retrofit.baseUrl().toString())
        val hasMoshiFactory = retrofit.converterFactories().any { it is MoshiConverterFactory }
        assertTrue(hasMoshiFactory, "Moshi converter factory should be installed")
    }

    @Test
    fun shouldAddAcceptHeaderToEveryRequest() {
        var seenAccept: String? = null
        val terminal = Interceptor { chain ->
            val req = chain.request()
            seenAccept = req.header("Accept")
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody("application/json".toMediaType()))
                .build()
        }
        val client: OkHttpClient = provideOkHttp(
            config = RickAndMortyNetworkConfig(baseUrl = DEFAULT_BASE_URL),
            extraInterceptors = listOf(terminal),
            includeErrorInterceptor = false,
            enableLogging = false
        )

        val req = Request.Builder()
            .url(DEFAULT_BASE_URL + "ping")
            .build()

        val resp = client.newCall(req).execute()
        assertEquals(200, resp.code)
        assertEquals("application/json", seenAccept)
    }

    @Test
    fun shouldCreateServiceExtension() {
        val cfg = RickAndMortyNetworkConfig(baseUrl = DEFAULT_BASE_URL)
        val client = provideOkHttp(
            config = cfg,
            includeErrorInterceptor = false,
            enableLogging = false
        )
        val retrofit = provideRetrofit(
            config = cfg,
            client = client,
            moshi = defaultMoshi
        )
        val api = retrofit.createService<DummyApi>()

        assertNotNull(api)
    }

    interface DummyApi {
        @GET("ping")
        fun ping(): retrofit2.Call<Unit>
    }
}

private class MarkerInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}