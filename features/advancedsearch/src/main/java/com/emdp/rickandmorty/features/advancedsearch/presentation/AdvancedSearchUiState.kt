package com.emdp.rickandmorty.features.advancedsearch.presentation

import com.emdp.rickandmorty.domain.models.CharacterModel

sealed interface AdvancedSearchUiState {
    data object Idle : AdvancedSearchUiState
    data object Loading : AdvancedSearchUiState
    data object LoadingMore : AdvancedSearchUiState
    data class Success(
        val characters: List<CharacterModel>,
        val hasMorePages: Boolean
    ) : AdvancedSearchUiState
    data class Error(val messageRes: Int) : AdvancedSearchUiState
}