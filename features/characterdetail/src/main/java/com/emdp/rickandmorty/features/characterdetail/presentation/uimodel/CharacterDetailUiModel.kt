package com.emdp.rickandmorty.features.characterdetail.presentation.uimodel

import androidx.compose.ui.graphics.Color

data class CharacterDetailUiModel(
    val name: String,
    val imageUrl: String,
    val imageContentDescription: String,
    val statusChip: UiChipModel,
    val speciesChip: UiChipModel,
    val infoItems: List<InfoItemModel>
)

data class UiChipModel(
    val text: String,
    val containerColor: Color,
    val labelColor: Color
)

data class InfoItemModel(
    val label: String,
    val value: String
)