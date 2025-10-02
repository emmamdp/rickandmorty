package com.emdp.rickandmorty.data.source.remote.mapper

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.data.source.remote.dto.CharacterDto
import com.emdp.rickandmorty.data.source.remote.dto.CharactersResponseDto
import com.emdp.rickandmorty.domain.models.CharacterModel

interface CharactersRemoteMapper {
    fun toModel(response: CharactersResponseDto): List<CharacterModel>
    fun toModel(dto: CharacterDto): CharacterModel
    fun toError(throwable: Throwable): AppError
}