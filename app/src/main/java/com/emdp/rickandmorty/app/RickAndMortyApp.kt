package com.emdp.rickandmorty.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.emdp.rickandmorty.BuildConfig
import com.emdp.rickandmorty.core.di.RickAndMortyDiModules
import com.emdp.rickandmorty.core.ui.image.CoilImageLoaderFactory
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RickAndMortyApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.INFO)
            androidContext(this@RickAndMortyApp)
            modules(RickAndMortyDiModules.allModules())
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return get<CoilImageLoaderFactory>().create(context)
    }
}
