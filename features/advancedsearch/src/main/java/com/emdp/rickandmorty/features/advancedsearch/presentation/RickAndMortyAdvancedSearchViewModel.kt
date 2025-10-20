package com.emdp.rickandmorty.features.advancedsearch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.features.advancedsearch.common.ErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RickAndMortyAdvancedSearchViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdvancedSearchUiState>(AdvancedSearchUiState.Idle)
    val uiState: StateFlow<AdvancedSearchUiState> = _uiState.asStateFlow()

    private val _filters = MutableStateFlow(CharactersFilterModel())
    val filters: StateFlow<CharactersFilterModel> = _filters.asStateFlow()

    private var currentPage = 1
    private val allCharacters = mutableListOf<CharacterModel>()
    private var hasMorePages = true

    fun updateName(name: String) {
        _filters.update { it.copy(name = name.ifBlank { null }) }

        if (name.isBlank() && !hasActiveFilters(_filters.value)) {
            resetSearch()
        }
    }

    fun updateStatus(status: String?) {
        _filters.update { it.copy(status = status) }
        search()
    }

    fun updateSpecies(species: String?) {
        _filters.update { it.copy(species = species) }
        search()
    }

    fun updateGender(gender: String?) {
        _filters.update { it.copy(gender = gender) }
        search()
    }

    fun updateType(type: String) {
        _filters.update { it.copy(type = type.ifBlank { null }) }
    }

    fun clearFilters() {
        _filters.value = CharactersFilterModel()
        resetSearch()
    }

    fun search() {
        if (!hasAnyFilter()) {
            resetSearch()
            return
        }

        resetSearch()
        performSearch()
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (!hasMorePages ||
            currentState !is AdvancedSearchUiState.Success ||
            currentState.isLoadingMore) return

        _uiState.value = currentState.copy(isLoadingMore = true)
        performSearch(append = true)
    }

    private fun performSearch(append: Boolean = false) {
        viewModelScope.launch {
            if (!append) {
                _uiState.value = AdvancedSearchUiState.Loading
            }

            val pageToLoad = if (append) currentPage + 1 else currentPage

            when (val result = getCharactersUseCase(page = pageToLoad, filter = _filters.value)) {
                is DataResult.Success -> {
                    val pagedData = result.data
                    hasMorePages = pagedData.hasMore
                    currentPage = pagedData.page

                    if (append) {
                        allCharacters.addAll(pagedData.data)
                    } else {
                        allCharacters.clear()
                        allCharacters.addAll(pagedData.data)
                    }

                    _uiState.value = AdvancedSearchUiState.Success(
                        characters = allCharacters.toList(),
                        hasMorePages = hasMorePages,
                        isLoadingMore = false
                    )
                }

                is DataResult.Error -> {
                    _uiState.value = if (append && allCharacters.isNotEmpty()) {
                        AdvancedSearchUiState.Success(
                            characters = allCharacters.toList(),
                            hasMorePages = false,
                            isLoadingMore = false
                        )
                    } else {
                        AdvancedSearchUiState.Error(
                            messageRes = ErrorMapper.mapToUserMessage(result.error)
                        )
                    }
                }
            }
        }
    }

    private fun resetSearch() {
        currentPage = 1
        hasMorePages = true
        allCharacters.clear()
        _uiState.value = AdvancedSearchUiState.Idle
    }

    private fun hasActiveFilters(filters: CharactersFilterModel): Boolean {
        return filters.status != null ||
                filters.species != null ||
                filters.gender != null ||
                filters.type != null
    }

    private fun hasAnyFilter(): Boolean {
        val current = _filters.value
        return current.name != null ||
                current.status != null ||
                current.species != null ||
                current.gender != null ||
                current.type != null
    }
}