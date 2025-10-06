package com.emdp.rickandmorty.data.source.local.mapper

import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.domain.models.CharacterModel

interface CharacterLocalMapper {

    fun toEntity(model: CharacterModel): CharacterEntity
    fun toModel(entity: CharacterEntity): CharacterModel

    fun toEntityList(models: List<CharacterModel>): List<CharacterEntity> =
        models.map(::toEntity)
}