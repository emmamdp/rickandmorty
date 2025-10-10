package com.emdp.rickandmorty.data.source.local.mapper

import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class CharacterLocalMapperImplTest {

    private val sut: CharacterLocalMapper = CharacterLocalMapperImpl()

    @Test
    fun `toEntity maps non-blank fields and enums correctly`() {
        val model = CharacterModel(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "Scientist",
            gender = CharacterGender.MALE,
            originName = "Earth (C-137)",
            locationName = "Citadel of Ricks",
            imageUrl = "https://img/rick.png",
            episodeUrls = listOf("e1", "e2"),
            createdIso = "2017-11-04T18:50:21.651Z"
        )

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
        val model = CharacterModel(
            id = 2,
            name = "Morty Smith",
            status = CharacterStatus.UNKNOWN,
            species = "Human",
            type = "",
            gender = CharacterGender.MALE,
            originName = "",
            locationName = " ",
            imageUrl = "https://img/morty.png",
            episodeUrls = emptyList(),
            createdIso = ""
        )

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
        val entity = CharacterEntity(
            id = 3,
            name = "Birdperson",
            status = "DEAD",
            species = "Bird-Person",
            type = null,
            gender = "MALE",
            imageUrl = "https://img/birdperson.png",
            originName = null,
            locationName = null,
            episodes = null,
            created = null
        )

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
        val entity = CharacterEntity(
            id = 4,
            name = "Unknown Dude",
            status = "SOMETHING_WEIRD",
            species = "???",
            type = "???",
            gender = "NOT_A_GENDER",
            imageUrl = "https://img/unknown.png",
            originName = "Somewhere",
            locationName = "Nowhere",
            episodes = listOf("e1"),
            created = "2020-01-01T00:00:00Z"
        )

        val model = sut.toModel(entity)

        assertEquals(CharacterStatus.UNKNOWN, model.status)
        assertEquals(CharacterGender.UNKNOWN, model.gender)
    }

    @Test
    fun `toModel is case-insensitive for enums`() {
        val entity = CharacterEntity(
            id = 5,
            name = "Case Test",
            status = "alive",
            species = "Human",
            type = "",
            gender = "male",
            imageUrl = "https://img/case.png",
            originName = "earth",
            locationName = "somewhere",
            episodes = listOf("e1", "e2"),
            created = "date"
        )

        val model = sut.toModel(entity)

        assertEquals(CharacterStatus.ALIVE, model.status)
        assertEquals(CharacterGender.MALE, model.gender)
    }
}