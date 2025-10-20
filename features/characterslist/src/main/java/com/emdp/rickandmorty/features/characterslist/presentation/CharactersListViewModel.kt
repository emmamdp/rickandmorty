package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CharactersListUiState())
    val state: StateFlow<CharactersListUiState> = _state.asStateFlow()

    private val _filterState = MutableStateFlow<CharactersFilterModel?>(null)
    val filterState: StateFlow<CharactersFilterModel?> = _filterState.asStateFlow()

    fun loadCharacters(refresh: Boolean = false) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = refresh || it.characters.isEmpty(),
                    isLoadingMore = !refresh && it.characters.isNotEmpty(),
                    error = null
                )
            }

            val page = if (refresh) 1 else _state.value.currentPage

            when (val result = getCharactersUseCase(page, _filterState.value)) {
                is DataResult.Success -> {
                    _state.update {
                        it.copy(
                            characters = if (refresh) result.data.data
                            else it.characters + result.data.data,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = result.data.page + 1,
                            hasMore = result.data.hasMore,
                            error = null
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    fun loadMore() {
        if (!_state.value.isLoading &&
            !_state.value.isLoadingMore &&
            _state.value.hasMore
        ) {
            loadCharacters(refresh = false)
        }
    }

    fun refresh() {
        loadCharacters(refresh = true)
    }

    fun applyFilter(filter: CharactersFilterModel?) {
        _filterState.value = filter
        loadCharacters(refresh = true)
    }

    fun clearFilter() {
        _filterState.value = null
        loadCharacters(refresh = true)
    }
}
