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
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val filterState = MutableStateFlow<CharactersFilterModel?>(null)

    val characters: Flow<PagingData<CharacterModel>> =
        filterState
            .flatMapLatest(getCharactersUseCase::invoke)
            .cachedIn(viewModelScope)

    private var hasLoadedInitially = false

    fun loadInitial() {
        if (hasLoadedInitially) return
        hasLoadedInitially = true
    }

    fun applyFilter(filter: CharactersFilterModel?) {
        filterState.value = filter
    }

    fun clearFilter() {
        filterState.value = null
    }
}