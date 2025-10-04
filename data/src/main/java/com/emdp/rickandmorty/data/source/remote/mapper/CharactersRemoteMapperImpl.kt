package com.emdp.rickandmorty.data.source.remote.mapper

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.data.source.remote.dto.CharacterDto
import com.emdp.rickandmorty.data.source.remote.dto.CharactersResponseDto
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.FEMALE
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.GENDERLESS
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.MALE
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.UNKNOWN
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus.ALIVE
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus.DEAD
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.io.IOException
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus.UNKNOWN as CHARACTER_STATUS_UNKNOWN

class CharactersRemoteMapperImpl : CharactersRemoteMapper {

    override fun toModel(response: CharactersResponseDto): CharactersPageModel =
        CharactersPageModel(
            count = response.info.count,
            pages = response.info.pages,
            nextPage = extractPageParam(url = response.info.next),
            prevPage = extractPageParam(url = response.info.prev),
            results = response.results.map { dto -> toModel(dto) }
        )

    override fun toModel(dto: CharacterDto): CharacterModel =
        CharacterModel(
            id = dto.id,
            name = dto.name,
            status = dto.status.toDomainStatus(),
            species = dto.species,
            type = dto.type,
            gender = dto.gender.toDomainGender(),
            originName = dto.origin.name,
            locationName = dto.location.name,
            imageUrl = dto.image,
            episodeUrls = dto.episode,
            createdIso = dto.created
        )

    override fun toError(throwable: Throwable): AppError =
        when (throwable) {
            is HttpException -> AppError.Http(
                code = throwable.code(),
                message = throwable.message()
            )

            is JsonDataException,
            is JsonEncodingException -> AppError.Serialization(cause = throwable)

            is IOException -> AppError.Network(cause = throwable)
            else -> AppError.Unexpected(cause = throwable)
        }

    private fun String.toDomainStatus(): CharacterStatus =
        when (this.lowercase()) {
            STATUS_ALIVE -> ALIVE
            STATUS_DEAD -> DEAD
            else -> CHARACTER_STATUS_UNKNOWN
        }

    private fun String.toDomainGender(): CharacterGender =
        when (this.lowercase()) {
            GENDER_FEMALE -> FEMALE
            GENDER_MALE -> MALE
            GENDER_GENDERLESS -> GENDERLESS
            else -> UNKNOWN
        }

    private fun extractPageParam(url: String?): Int? {
        if (url.isNullOrBlank()) return null
        val qIndex = url.indexOf('?')
        if (qIndex == -1 || qIndex == url.lastIndex) return null
        val query = url.substring(qIndex + 1)

        for (pair in query.split('&')) {
            val eq = pair.indexOf('=')
            if (eq <= 0) continue
            val key = pair.substring(0, eq)
            if (key != PAGE) continue
            val value = pair.substring(eq + 1)
            return value.toIntOrNull()
        }
        return null
    }

    companion object {
        private const val STATUS_ALIVE = "alive"
        private const val STATUS_DEAD = "dead"
        private const val GENDER_FEMALE = "female"
        private const val GENDER_MALE = "male"
        private const val GENDER_GENDERLESS = "genderless"
        private const val PAGE = "page"
    }
}