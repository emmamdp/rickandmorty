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
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _filterState = MutableStateFlow<CharactersFilterModel?>(null)
    val filterState: StateFlow<CharactersFilterModel?> = _filterState.asStateFlow()

    val characters: Flow<PagingData<CharacterModel>> = _filterState
        .flatMapLatest { filter ->
            getCharactersUseCase.invoke(filter)
        }
        .cachedIn(viewModelScope)

    fun applyFilter(filter: CharactersFilterModel?) {
        _filterState.value = filter
    }

    fun clearFilter() {
        _filterState.value = null
    }
}
