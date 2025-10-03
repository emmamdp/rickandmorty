package com.emdp.rickandmorty.features.characterslist.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.domain.models.CharacterModel

data class CharactersListUiState(
    val isLoading: Boolean = false,
    val items: List<CharacterModel> = emptyList(),
    val error: AppError? = null
)