package com.emdp.rickandmorty.features.characterdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.usecase.character.GetCharacterUseCase
import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailUiState.Content
import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailUiState.Error
import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailUiState.Loading
import com.emdp.rickandmorty.features.characterdetail.presentation.mapper.CharacterDetailMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val getCharacterUseCase: GetCharacterUseCase,
    private val mapper: CharacterDetailMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private var lastCharacterId: Int? = null

    fun load(characterId: Int) {
        if (characterId <= 0) {
            _uiState.value = Error
            return
        }

        if (lastCharacterId == characterId && _uiState.value is Content) return

        lastCharacterId = characterId
        _uiState.value = Loading

        viewModelScope.launch {
            val params = GetCharacterUseCase.Params(characterId)
            when (val result = getCharacterUseCase(params)) {
                is DataResult.Success -> {
                    val uiModel = mapper.getUiModel(result.data)
                    _uiState.value = Content(uiModel = uiModel)
                }
                is DataResult.Error -> _uiState.value = Error
            }
        }
    }

    fun retry() {
        lastCharacterId?.let { load(it) }
    }
}