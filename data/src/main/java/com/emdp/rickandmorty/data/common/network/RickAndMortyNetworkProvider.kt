package com.emdp.rickandmorty.data.common.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RickAndMortyNetworkProvider {

    val defaultMoshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun provideOkHttp(
        config: RickAndMortyNetworkConfig,
        extraInterceptors: List<Interceptor> = emptyList(),
        includeErrorInterceptor: Boolean = true,
        enableLogging: Boolean = false,
        errorInterceptor: Interceptor = RickAndMortyNetworkErrorInterceptor()
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeoutMs, TimeUnit.MILLISECONDS)

        if (enableLogging) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        builder.addInterceptor { chain ->
            val original: Request = chain.request()
            val req = original.newBuilder()
                .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)
                .header(HEADER_USER_AGENT, USER_AGENT_VALUE)
                .build()
            chain.proceed(req)
        }

        if (includeErrorInterceptor) {
            builder.addInterceptor(errorInterceptor)
        }

        extraInterceptors.forEach { builder.addInterceptor(it) }

        return builder.build()
    }

    fun provideRetrofit(
        config: RickAndMortyNetworkConfig,
        client: OkHttpClient,
        moshi: Moshi = defaultMoshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private const val HEADER_ACCEPT = "Accept"
    private const val HEADER_USER_AGENT = "User-Agent"
    private const val CONTENT_TYPE_JSON = "application/json"
    private const val USER_AGENT_VALUE = "Mozilla/5.0 (Linux; Android 13; RickAndMortyApp) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36"
}

inline fun <reified T> Retrofit.createService(): T = this.create(T::class.java)
