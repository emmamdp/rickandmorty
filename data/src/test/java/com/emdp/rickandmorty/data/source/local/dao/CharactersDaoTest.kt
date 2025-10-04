package com.emdp.rickandmorty.data.source.local.dao

import androidx.paging.PagingSource
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun pagingSource_withFilters_shouldReturnFilteredSortedData() = runBlocking {
        dao.upsertAll(CharacterEntityMother.mockList01())

        val pagingSource: PagingSource<Int, CharacterEntity> = dao.pagingSource(
            name = null,
            status = ALIVE,
            species = HUMAN,
            type = null,
            gender = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 50,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        val names = page.data.map { it.name }
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

        val paging = dao.pagingSource(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )
        val result = paging.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 50,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun pagingSource_whenAllFiltersNull_shouldReturnAllSortedByIdAsc() = runBlocking {
        val list = CharacterEntityMother.mockList02()
        dao.upsertAll(list)

        val paging = dao.pagingSource(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )
        val result = paging.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 50,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        val ids = page.data.map { it.id }
        val sortedIds = ids.sorted()
        assertEquals(sortedIds, ids, ORDER_BY_MESSAGE)
    }

    @Test
    fun pagingSource_filters_shouldRespectOperators() = runBlocking {
        val list = CharacterEntityMother.mockList02()
        dao.upsertAll(list)

        val paging = dao.pagingSource(
            name = null,
            status = ALIVE,
            species = HUMAN,
            type = SCIENTIST,
            gender = GENDER_MALE
        )

        val result = paging.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 50,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page

        page.data.forEach { e ->
            assertEquals(ALIVE, e.status)
            assertTrue(e.species.contains(HUMAN, ignoreCase = true))
            assertEquals(SCIENTIST, e.type)
            assertEquals(GENDER_MALE, e.gender)
        }
    }

    @Test
    fun pagingSource_whenNoMatches_shouldReturnEmptyPage() = runBlocking {
        dao.upsertAll(CharacterEntityMother.mockList02())

        val paging = dao.pagingSource(
            name = NOT_EXISTS,
            status = DEAD,
            species = ROBOT,
            type = UNKNOWN_TYPE,
            gender = GENDERLESS
        )

        val result = paging.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
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