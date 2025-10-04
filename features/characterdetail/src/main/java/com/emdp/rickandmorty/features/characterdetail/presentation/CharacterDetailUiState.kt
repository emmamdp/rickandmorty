package com.emdp.rickandmorty.features.characterdetail.presentation

import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.CharacterDetailUiModel

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Content(val uiModel: CharacterDetailUiModel) : CharacterDetailUiState
    data object Error : CharacterDetailUiState
}