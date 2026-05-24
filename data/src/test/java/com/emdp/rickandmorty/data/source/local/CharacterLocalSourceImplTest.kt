package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class CharacterLocalSourceImplTest {

    private val charactersDao: CharactersDao = mock()
    private val localMapper: CharacterLocalMapper = mock()

    private val localSource = CharacterLocalSourceImpl(charactersDao, localMapper)

    @Test
    fun `getCharacterById emits model when entity exists`() = runTest {
        val entity = CharacterEntityMother.mockRick()
        val model = CharacterModelMother.mockRick()
        val characterId = entity.id

        whenever(charactersDao.observeCharacterById(characterId)).thenReturn(flowOf(entity))
        whenever(localMapper.toModel(entity)).thenReturn(model)

        val result = localSource.getCharacterById(characterId).first()

        assertSame(model, result)
        verify(charactersDao).observeCharacterById(characterId)
    }

    @Test
    fun `getCharacterById emits null when entity does not exist`() = runTest {
        val characterId = 42
        whenever(charactersDao.observeCharacterById(characterId)).thenReturn(flowOf(null))

        val result = localSource.getCharacterById(characterId).first()

        assertNull(result)
    }

    @Test
    fun `saveCharacter calls dao upsertAll with mapped entity`() = runTest {
        val model = CharacterModelMother.mockRick()
        val entity = CharacterEntityMother.mockRick()
        
        whenever(localMapper.toEntity(model)).thenReturn(entity)

        localSource.saveCharacter(model)

        verify(localMapper).toEntity(model)
        verify(charactersDao).upsertAll(listOf(entity))
    }
}
