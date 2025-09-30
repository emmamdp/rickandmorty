package com.emdp.rickandmorty.data.common.network

data class RickAndMortyNetworkConfig(
    val baseUrl: String = DEFAULT_BASE_URL,
    val connectTimeoutMs: Long = 10_000,
    val readTimeoutMs: Long = 20_000,
    val writeTimeoutMs: Long = 20_000
) {
    companion object {
        const val DEFAULT_BASE_URL: String = "https://rickandmortyapi.com/api/"
    }
}