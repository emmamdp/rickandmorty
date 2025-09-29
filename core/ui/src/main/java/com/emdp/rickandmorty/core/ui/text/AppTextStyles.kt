package com.emdp.rickandmorty.core.ui.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.emdp.rickandmorty.core.ui.theme.MortyYellow
import com.emdp.rickandmorty.core.ui.theme.PortalGreen
import com.emdp.rickandmorty.core.ui.theme.RickCyan

object AppTextStyles {

    @get:Composable
    val TitleEmphasis: TextStyle
        get() = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Companion.SemiBold
        )

    @get:Composable
    val BodyNormal: TextStyle
        get() = MaterialTheme.typography.bodyMedium

    @get:Composable
    val CaptionEmphasis: TextStyle
        get() = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Companion.Medium
        )

    @Composable
    fun multiverseTitle(): Brush = Brush.Companion.linearGradient(
        colors = listOf(
            PortalGreen,
            MortyYellow,
            RickCyan,
            PortalGreen
        )
    )
}