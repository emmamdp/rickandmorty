package com.emdp.rickandmorty.data.source.local.dao

import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CharactersDaoTest {

    private lateinit var dao: CharactersDao

    @BeforeEach
    fun setUp() {
        dao = FakeCharactersDao()
    }

    @Test
    fun upsertAndGetById_shouldReturnTheInsertedItem() = runBlocking {
        val rick = CharacterEntityMother.mockRickySanchez()

        dao.upsertAll(listOf(rick))
        val loaded = dao.getCharacterById(1)

        assertEquals(RICK_SANCHEZ, loaded?.name)
        assertEquals(ALIVE, loaded?.status)
    }

    @Test
    fun upsert_shouldUpdateExistingRow() = runBlocking {
        val v1 = CharacterEntityMother.mockRick()
        val v2 = v1.copy(name = RICK_SANCHEZ)

        dao.upsertAll(listOf(v1))
        dao.upsertAll(listOf(v2))

        assertEquals(RICK_SANCHEZ, dao.getCharacterById(1)?.name)
    }

    @Test
    fun getCharacters_withFilters_shouldReturnFilteredSortedData() = runBlocking {
        dao.upsertAll(CharacterEntityMother.mockList01())

        val result = dao.getCharacters(
            name = null,
            status = ALIVE,
            species = HUMAN,
            type = null,
            gender = null,
            limit = 50,
            offset = 0
        )

        val names = result.map { it.name }
        assertEquals(listOf(RICK, MORTY), names)
    }

    @Test
    fun getCharacterById_whenNotFound_shouldReturnNull() = runBlocking {
        val loaded = dao.getCharacterById(999)
        assertNull(loaded)
    }

    @Test
    fun clearAll_afterInsert_shouldRemoveAllRows() = runBlocking {
        val list = CharacterEntityMother.mockList02()
        dao.upsertAll(list)
        assertNotNull(dao.getCharacterById(list.first().id))

        dao.clearAll()

        val result = dao.getCharacters(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null,
            limit = 50,
            offset = 0
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun getCharacters_whenAllFiltersNull_shouldReturnAllSortedByIdAsc() = runBlocking {
        val list = CharacterEntityMother.mockList02()
        dao.upsertAll(list)

        val result = dao.getCharacters(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null,
            limit = 50,
            offset = 0
        )

        val ids = result.map { it.id }
        val sortedIds = ids.sorted()
        assertEquals(sortedIds, ids, ORDER_BY_MESSAGE)
    }

    @Test
    fun getCharacters_filters_shouldRespectOperators() = runBlocking {
        val list = CharacterEntityMother.mockList02()
        dao.upsertAll(list)

        val result = dao.getCharacters(
            name = null,
            status = ALIVE,
            species = HUMAN,
            type = SCIENTIST,
            gender = GENDER_MALE,
            limit = 50,
            offset = 0
        )

        result.forEach { e ->
            assertEquals(ALIVE, e.status)
            assertTrue(e.species.contains(HUMAN, ignoreCase = true))
            assertEquals(SCIENTIST, e.type)
            assertEquals(GENDER_MALE, e.gender)
        }
    }

    @Test
    fun getCharacters_whenNoMatches_shouldReturnEmptyList() = runBlocking {
        dao.upsertAll(CharacterEntityMother.mockList02())

        val result = dao.getCharacters(
            name = NOT_EXISTS,
            status = DEAD,
            species = ROBOT,
            type = UNKNOWN_TYPE,
            gender = GENDERLESS,
            limit = 20,
            offset = 0
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun getCharacters_withLimitAndOffset_shouldPaginate() = runBlocking {
        val list = (1..25).map {
            CharacterEntityMother.mockRick().copy(id = it, name = "Character $it")
        }
        dao.upsertAll(list)

        val page1 = dao.getCharacters(null, null, null, null, null, limit = 10, offset = 0)
        val page2 = dao.getCharacters(null, null, null, null, null, limit = 10, offset = 10)
        val page3 = dao.getCharacters(null, null, null, null, null, limit = 10, offset = 20)

        assertEquals(10, page1.size)
        assertEquals(10, page2.size)
        assertEquals(5, page3.size)
        assertEquals(1, page1.first().id)
        assertEquals(11, page2.first().id)
        assertEquals(21, page3.first().id)
    }

    companion object {
        private const val RICK_SANCHEZ = "Rick Sanchez"
        private const val RICK = "Rick"
        private const val MORTY = "Morty"
        private const val NOT_EXISTS = "zzzz_not_exists"
        private const val ALIVE = "Alive"
        private const val DEAD = "Dead"
        private const val HUMAN = "Human"
        private const val ROBOT = "Robot"
        private const val SCIENTIST = "Scientist"
        private const val UNKNOWN_TYPE = "UnknownType"
        private const val GENDER_MALE = "Male"
        private const val GENDERLESS = "Genderless"
        private const val ORDER_BY_MESSAGE = "Expected ascending order by id"
    }
}