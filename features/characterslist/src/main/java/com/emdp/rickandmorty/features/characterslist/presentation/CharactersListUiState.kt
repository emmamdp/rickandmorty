package com.emdp.rickandmorty.features.characterslist.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.domain.models.CharacterModel

data class CharactersListUiState(
    val characters: List<CharacterModel> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: AppError? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)