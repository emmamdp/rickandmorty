package com.emdp.rickandmorty.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val PortalGreen = Color(0xFF97CE4C)
val RickCyan = Color(0xFF00B5CC)
val MortyYellow = Color(0xFFF5E050)

val Neutral100 = Color(0xFFFFFFFF)
val Neutral98 = Color(0xFFFAFAFA)
val Neutral90 = Color(0xFFE0E0E0)
val Neutral30 = Color(0xFF4F4F4F)
val Neutral10 = Color(0xFF1B1B1B)
val Neutral0  = Color(0xFF000000)

val ErrorRed = Color(0xFFEF5350)

val ChipGreen  = Color(0xFF2ECC71)
val ChipRed    = Color(0xFFE74C3C)
val ChipGray   = Color(0xFF95A5A6)
val ChipPurple = Color(0xFF9B59B6)
val ChipBlue   = Color(0xFF3498DB)
val ChipTeal   = Color(0xFF1ABC9C)

val TextPrimaryLight = Color(0xFF1a1a1a)
val TextPrimaryDark = Neutral100

val TextSecondaryLight = Color(0xFF2d2d2d)
val TextSecondaryDark = Neutral100.copy(alpha = 0.9f)

val TextTertiaryLight = Color(0xFF3d3d3d)
val TextTertiaryDark = Neutral100.copy(alpha = 0.8f)

val ContentOnLoader = Neutral100

fun onFor(bg: Color): Color = if (bg.luminance() > 0.5f) Color.Black else Color.White