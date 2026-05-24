package com.emdp.rickandmorty.core.ui.di

import com.emdp.rickandmorty.core.ui.image.CoilImageLoaderFactory
import com.emdp.rickandmorty.core.ui.image.CoilInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreUiModule = module {
    singleOf(::CoilInterceptor)
    singleOf(::CoilImageLoaderFactory)
}
