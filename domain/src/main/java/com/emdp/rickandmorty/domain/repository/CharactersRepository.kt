package com.emdp.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {

    fun getCharactersPaged(
        filter: CharactersFilterModel? = null
    ): Flow<PagingData<CharacterModel>>

    suspend fun getCharacterById(id: Int): DataResult<CharacterModel>
}