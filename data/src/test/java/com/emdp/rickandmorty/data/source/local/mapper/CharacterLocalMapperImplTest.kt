package com.emdp.rickandmorty.data.source.local.mapper

import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class CharacterLocalMapperImplTest {

    private val sut: CharacterLocalMapper = CharacterLocalMapperImpl()

    @Test
    fun `toEntity maps non-blank fields and enums correctly`() {
        val model = CharacterModelMother.mockRickFull()

        val entity = sut.toEntity(model)

        assertEquals(model.id, entity.id)
        assertEquals(model.name, entity.name)
        assertEquals("ALIVE", entity.status)
        assertEquals(model.species, entity.species)
        assertEquals(model.type, entity.type)
        assertEquals("MALE", entity.gender)
        assertEquals(model.imageUrl, entity.imageUrl)
        assertEquals(model.originName, entity.originName)
        assertEquals(model.locationName, entity.locationName)
        assertEquals(model.createdIso, entity.created)
    }

    @Test
    fun `toEntity converts blank strings to null for optional DB fields`() {
        val model = CharacterModelMother.mockWithBlanks()

        val entity = sut.toEntity(model)

        assertEquals(null, entity.type)
        assertEquals(null, entity.originName)
        assertEquals(null, entity.locationName)
        assertEquals(null, entity.created)
        assertEquals("UNKNOWN", entity.status)
        assertEquals("MALE", entity.gender)
    }

    @Test
    fun `toModel maps strings to enums and nulls to empty strings`() {
        val entity = CharacterEntityMother.mockBirdperson()

        val model = sut.toModel(entity)

        assertEquals(entity.id, model.id)
        assertEquals(entity.name, model.name)
        assertEquals(CharacterStatus.DEAD, model.status)
        assertEquals(entity.species, model.species)
        assertEquals("", model.type)
        assertEquals(CharacterGender.MALE, model.gender)
        assertEquals(entity.imageUrl, model.imageUrl)
        assertEquals("", model.originName)
        assertEquals("", model.locationName)
        assertEquals("", model.createdIso)
        assertTrue(model.episodeUrls.isEmpty())
    }

    @Test
    fun `toModel handles unknown enum strings as UNKNOWN`() {
        val entity = CharacterEntityMother.mockUnknown()

        val model = sut.toModel(entity)

        assertEquals(CharacterStatus.UNKNOWN, model.status)
        assertEquals(CharacterGender.UNKNOWN, model.gender)
    }

    @Test
    fun `toModel is case-insensitive for enums`() {
        val entity = CharacterEntityMother.mockCaseSensitive()

        val model = sut.toModel(entity)

        assertEquals(CharacterStatus.ALIVE, model.status)
        assertEquals(CharacterGender.MALE, model.gender)
    }

    @Test
    fun `toEntityList maps list of models correctly`() {
        val models = CharacterModelMother.mockList()

        val entities = sut.toEntityList(models)

        assertEquals(models.size, entities.size)
        assertEquals(models[0].id, entities[0].id)
    }
}
