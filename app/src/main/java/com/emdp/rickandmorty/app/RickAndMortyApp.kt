package com.emdp.rickandmorty.app

import android.app.Application
import com.emdp.rickandmorty.BuildConfig
import com.emdp.rickandmorty.core.di.RickAndMortyDiModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RickAndMortyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.INFO)
            androidContext(this@RickAndMortyApp)
            modules(RickAndMortyDiModules.allModules())
        }
    }
}