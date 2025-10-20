package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.CharactersTotalPagesDao
import com.emdp.rickandmorty.data.source.local.entity.CharactersTotalPagesEntity
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel

class CharacterLocalSourceImpl(
    private val charactersDao: CharactersDao,
    private val totalPagesDao: CharactersTotalPagesDao,
    private val localMapper: CharacterLocalMapper
) : CharacterLocalSource {

    override suspend fun getCharactersPage(
        page: Int,
        pageSize: Int,
        filter: CharactersFilterModel?
    ): List<CharacterModel> = runCatching {
        val offset = (page - 1) * pageSize
        val entities = charactersDao.getCharacters(
            name = filter?.name,
            status = filter?.status,
            species = filter?.species,
            type = filter?.type,
            gender = filter?.gender,
            limit = pageSize,
            offset = offset
        )
        entities.map(localMapper::toModel)
    }.getOrElse { emptyList() }

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        try {
            val entity = charactersDao.getCharacterById(id)
            if (entity != null) {
                DataResult.Success(localMapper.toModel(entity))
            } else {
                DataResult.Error(AppError.DataNotFound)
            }
        } catch (t: Throwable) {
            DataResult.Error(AppError.Unexpected(t))
        }

    override suspend fun upsertCharacters(models: List<CharacterModel>) {
        if (models.isEmpty()) return
        val entities = models.map(localMapper::toEntity)
        charactersDao.upsertAll(entities)
    }

    override suspend fun getTotalPages(filter: CharactersFilterModel?): Int? =
        totalPagesDao.getTotalPages(buildFilterKey(filter))

    override suspend fun saveTotalPages(filter: CharactersFilterModel?, totalPages: Int) {
        totalPagesDao.upsert(
            CharactersTotalPagesEntity(
                filterKey = buildFilterKey(filter),
                totalPages = totalPages,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    private fun buildFilterKey(filter: CharactersFilterModel?): String {
        fun norm(value: String?) = value?.trim()?.lowercase().orEmpty()
        return listOf(
            "name=${norm(filter?.name)}",
            "status=${norm(filter?.status)}",
            "species=${norm(filter?.species)}",
            "type=${norm(filter?.type)}",
            "gender=${norm(filter?.gender)}"
        ).joinToString(";")
    }
}