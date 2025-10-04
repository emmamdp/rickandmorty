package com.emdp.rickandmorty.data.source.local.mapper

import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus

class CharacterLocalMapperImpl : CharacterLocalMapper {

    override fun toEntity(model: CharacterModel): CharacterEntity {
        return CharacterEntity(
            id = model.id,
            name = model.name,
            status = model.status.name,
            species = model.species,
            type = model.type.ifBlank { null },
            gender = model.gender.name,
            imageUrl = model.imageUrl,
            originName = model.originName.ifBlank { null },
            locationName = model.locationName.ifBlank { null },
            created = model.createdIso.ifBlank { null }
        )
    }

    override fun toModel(entity: CharacterEntity): CharacterModel {
        return CharacterModel(
            id = entity.id,
            name = entity.name,
            status = parseStatus(entity.status),
            species = entity.species,
            type = entity.type ?: "",
            gender = parseGender(entity.gender),
            originName = entity.originName ?: "",
            locationName = entity.locationName ?: "",
            imageUrl = entity.imageUrl,
            episodeUrls = emptyList(),
            createdIso = entity.created ?: ""
        )
    }

    private fun parseStatus(raw: String?): CharacterStatus {
        val normalized = raw?.trim()?.uppercase()
        return runCatching { CharacterStatus.valueOf(normalized ?: "") }
            .getOrDefault(CharacterStatus.UNKNOWN)
    }

    private fun parseGender(raw: String?): CharacterGender {
        val normalized = raw?.trim()?.uppercase()
        return runCatching { CharacterGender.valueOf(normalized ?: "") }
            .getOrDefault(CharacterGender.UNKNOWN)
    }
}