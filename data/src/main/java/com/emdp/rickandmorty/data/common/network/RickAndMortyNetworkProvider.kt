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
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val req = original.newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(req)
            }

        if (includeErrorInterceptor) {
            builder.addInterceptor(errorInterceptor)
        }

        extraInterceptors.forEach { builder.addInterceptor(it) }

        if (enableLogging) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

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
}

inline fun <reified T> Retrofit.createService(): T = this.create(T::class.java)