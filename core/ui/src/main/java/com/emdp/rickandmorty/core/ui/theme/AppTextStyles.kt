package com.emdp.rickandmorty.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

object AppTextStyles {

    @get:Composable
    val TitleEmphasis: TextStyle
        get() = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold
        )

    @get:Composable
    val BodyNormal: TextStyle
        get() = MaterialTheme.typography.bodyMedium

    @get:Composable
    val CaptionEmphasis: TextStyle
        get() = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium
        )
}