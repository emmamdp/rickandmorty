package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharactersListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersListUiState())
    val uiState: StateFlow<CharactersListUiState> = _uiState

    private var lastPageRequested: Int = 1
    private var hasLoadedInitially: Boolean = false

    fun loadInitial() {
        if (hasLoadedInitially) return
        hasLoadedInitially = true
        load(page = 1)
    }

    fun retry() {
        load(page = lastPageRequested)
    }

    private fun load(page: Int) {
        lastPageRequested = page
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val params = GetCharactersUseCase.Params(page = page)
            val result = getCharactersUseCase(params)
            when (result) {
                is DataResult.Success -> handleSuccess(page = result.data)
                is DataResult.Error -> handleError(error = result.error)
            }
        }
    }

    private fun handleSuccess(page: CharactersPageModel) {
        _uiState.value = CharactersListUiState(
            isLoading = false,
            items = page.results,
            error = null
        )
    }

    private fun handleError(error: AppError) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = error
        )
    }
}