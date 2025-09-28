package com.emdp.rickandmorty.features.splash.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.emdp.rickandmorty.core.ui.loader.MultiverseLoader
import com.emdp.rickandmorty.features.splash.R
import kotlinx.coroutines.delay

@Composable
fun RickAndMortySplashScreen(onFinished: (() -> Unit)) {
    LaunchedEffect(Unit) {
        delay(1200)
        onFinished()
    }
    RickAndMortyIllustration()
}

@Composable
fun RickAndMortyIllustration() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash_illustration),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        MultiverseLoader()
    }
}