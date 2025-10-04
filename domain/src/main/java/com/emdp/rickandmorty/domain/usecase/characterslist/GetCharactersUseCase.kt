package com.emdp.rickandmorty.domain.usecase.characterslist

import androidx.paging.PagingData
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import kotlinx.coroutines.flow.Flow

interface GetCharactersUseCase {

    operator fun invoke(
        filter: CharactersFilterModel? = null
    ): Flow<PagingData<CharacterModel>>
}