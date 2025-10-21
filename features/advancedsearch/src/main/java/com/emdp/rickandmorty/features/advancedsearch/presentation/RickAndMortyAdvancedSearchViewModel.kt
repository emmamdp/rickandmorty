package com.emdp.rickandmorty.features.advancedsearch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
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
        if (name.isBlank() && !hasAnyFilter()) resetSearch()
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
            currentState.isLoadingMore
        ) return

        _uiState.value = currentState.copy(isLoadingMore = true)
        performSearch(append = true)
    }

    private fun performSearch(append: Boolean = false) {
        viewModelScope.launch {
            if (!append) _uiState.value = AdvancedSearchUiState.Loading

            val pageToLoad = if (append) currentPage + 1 else currentPage
            val result = getCharactersUseCase(page = pageToLoad, filter = _filters.value)
            when (result) {
                is DataResult.Success -> handleSuccess(pagedData = result.data, append)
                is DataResult.Error -> handleError(error = result.error, append)
            }
        }
    }

    private fun handleSuccess(
        pagedData: RickAndMortyPagedData<CharacterModel>,
        append: Boolean
    ) {
        hasMorePages = pagedData.hasMore
        currentPage = pagedData.page

        if (!append) allCharacters.clear()
        allCharacters.addAll(pagedData.data)
        _uiState.value =
            getSuccessUiState(charactersList = allCharacters.toList(), hasMorePages = hasMorePages)
    }

    private fun handleError(error: AppError, append: Boolean) =
        when {
            error.is404() -> handleNoResults()
            append && allCharacters.isNotEmpty() -> handleLoadMoreError()
            else -> handleSearchError(error)
        }

    private fun handleNoResults() {
        allCharacters.clear()
        hasMorePages = false
        _uiState.value = getSuccessUiState(charactersList = emptyList())
    }

    private fun handleLoadMoreError() {
        _uiState.value = getSuccessUiState(charactersList = allCharacters.toList())
    }

    private fun getSuccessUiState(
        charactersList: List<CharacterModel>,
        hasMorePages: Boolean = false
    ) = AdvancedSearchUiState.Success(
        characters = charactersList,
        hasMorePages = hasMorePages,
        isLoadingMore = false
    )

    private fun handleSearchError(error: AppError) {
        _uiState.value = AdvancedSearchUiState.Error(
            messageRes = ErrorMapper.mapToUserMessage(error)
        )
    }

    private fun resetSearch() {
        currentPage = 1
        hasMorePages = true
        allCharacters.clear()
        _uiState.value = AdvancedSearchUiState.Idle
    }

    private fun hasAnyFilter(): Boolean =
        with(_filters.value) {
            name != null || status != null || species != null || gender != null || type != null
        }

    private fun AppError.is404(): Boolean =
        this is AppError.Http && this.code == 404
}