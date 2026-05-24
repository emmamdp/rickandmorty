package com.emdp.rickandmorty.core.ui.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class CoilImageLoaderFactory(
    private val coilInterceptor: CoilInterceptor
) {
    fun create(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve(DISK_CACHE_FOLDER))
                    .maxSizePercent(DISK_CACHE_PERCENT)
                    .build()
            }
            .components {
                val imageClient = OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .dispatcher(Dispatcher().apply { maxRequestsPerHost = 1 })
                    .addInterceptor(coilInterceptor)
                    .build()
                add(OkHttpNetworkFetcherFactory(callFactory = { imageClient }))
            }
            .crossfade(true)
            .build()
    }

    companion object {
        private const val MEMORY_CACHE_PERCENT = 0.25
        private const val DISK_CACHE_PERCENT = 0.1
        private const val DISK_CACHE_FOLDER = "image_cache"
        private const val TIMEOUT_SECONDS = 15L
    }
}
