package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _filterState: MutableStateFlow<CharactersFilterModel?> = MutableStateFlow(null)
    val filterState: StateFlow<CharactersFilterModel?> = _filterState.asStateFlow()

    private val stableFilter =
        filterState.distinctUntilChangedBy { it.toCanonicalKey() }

    init {
        viewModelScope.launch {
            stableFilter.collect { filter ->
                println("🎯 FILTRO (stable) CAMBIÓ: ${filter.toCanonicalKey()}")
            }
        }
    }

    val characters: Flow<PagingData<CharacterModel>> =
        stableFilter
            .flatMapLatest(getCharactersUseCase::invoke)
            .cachedIn(viewModelScope)

    private var hasLoadedInitially = false

    fun loadInitial() {
        if (hasLoadedInitially) return
        hasLoadedInitially = true
    }

    fun applyFilter(filter: CharactersFilterModel?) {
        if (_filterState.value.toCanonicalKey() != filter.toCanonicalKey()) {
            _filterState.value = filter
        }
    }

    fun clearFilter() {
        if (_filterState.value != null) {
            _filterState.value = null
        }
    }
}

private fun CharactersFilterModel?.toCanonicalKey(): String =
    if (this == null) "null"
    else buildString {
        append(name?.trim().orEmpty()); append('|')
        append(status?.trim()?.lowercase().orEmpty()); append('|')
        append(species?.trim().orEmpty()); append('|')
        append(type?.trim().orEmpty()); append('|')
        append(gender?.trim()?.lowercase().orEmpty())
    }
