package com.emdp.rickandmorty.features.characterdetail.presentation.mapper

import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.CharacterDetailUiModel

interface CharacterDetailMapper {
    fun getUiModel(model: CharacterModel) : CharacterDetailUiModel
}