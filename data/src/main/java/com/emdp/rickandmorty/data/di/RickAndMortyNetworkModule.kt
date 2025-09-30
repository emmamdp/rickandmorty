package com.emdp.rickandmorty.data.di

import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkConfig
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkProvider
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

val rickAndMortyNetworkModule = module {
    single { RickAndMortyNetworkConfig() }
    single<Moshi> { RickAndMortyNetworkProvider.defaultMoshi }
    single<OkHttpClient> {
        RickAndMortyNetworkProvider.provideOkHttp(
            config = get(),
            extraInterceptors = emptyList(),
            includeErrorInterceptor = true
        )
    }
    single<Retrofit> {
        RickAndMortyNetworkProvider.provideRetrofit(
            config = get(),
            client = get(),
            moshi = get()
        )
    }
}