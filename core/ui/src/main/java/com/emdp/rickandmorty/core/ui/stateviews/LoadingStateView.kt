package com.emdp.rickandmorty.core.ui.stateviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emdp.rickandmorty.core.ui.loader.MultiverseLoader

@Composable
fun LoadingStateView(
    modifier: Modifier = Modifier,
    useMultiverseLoader: Boolean = true,
    showMessage: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (useMultiverseLoader) {
            MultiverseLoader(showMessage = showMessage)
        } else {
            CircularProgressIndicator()
        }
    }
}