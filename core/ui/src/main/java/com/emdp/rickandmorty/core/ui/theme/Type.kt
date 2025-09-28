package com.emdp.rickandmorty.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.emdp.rickandmorty.core.ui.R

private val InterFontFamily = FontFamily(
    Font(resId = R.font.inter_variable, weight = FontWeight.Normal),
    Font(resId = R.font.inter_variable, weight = FontWeight.Medium),
    Font(resId = R.font.inter_variable, weight = FontWeight.SemiBold),
    Font(resId = R.font.inter_variable, weight = FontWeight.Bold)
)

private fun Typography.withFontFamily(interFontFamily: FontFamily) = copy(
    displayLarge = displayLarge.copy(fontFamily = interFontFamily),
    displayMedium = displayMedium.copy(fontFamily = interFontFamily),
    displaySmall = displaySmall.copy(fontFamily = interFontFamily),
    headlineLarge = headlineLarge.copy(fontFamily = interFontFamily),
    headlineMedium = headlineMedium.copy(fontFamily = interFontFamily),
    headlineSmall = headlineSmall.copy(fontFamily = interFontFamily),
    titleLarge = titleLarge.copy(fontFamily = interFontFamily),
    titleMedium = titleMedium.copy(fontFamily = interFontFamily),
    titleSmall = titleSmall.copy(fontFamily = interFontFamily),
    bodyLarge = bodyLarge.copy(fontFamily = interFontFamily),
    bodyMedium = bodyMedium.copy(fontFamily = interFontFamily),
    bodySmall = bodySmall.copy(fontFamily = interFontFamily),
    labelLarge = labelLarge.copy(fontFamily = interFontFamily),
    labelMedium = labelMedium.copy(fontFamily = interFontFamily),
    labelSmall = labelSmall.copy(fontFamily = interFontFamily)
)

val RickAndMortyTypography = Typography().withFontFamily(InterFontFamily)