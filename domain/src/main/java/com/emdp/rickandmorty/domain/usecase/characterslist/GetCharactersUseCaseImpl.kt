package com.emdp.rickandmorty.domain.usecase.characterslist

import androidx.paging.PagingData
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCaseImpl(
    private val repository: CharactersRepository
) : GetCharactersUseCase {

    override fun invoke(filter: CharactersFilterModel?): Flow<PagingData<CharacterModel>> =
        repository.getCharactersPaged(filter)
}