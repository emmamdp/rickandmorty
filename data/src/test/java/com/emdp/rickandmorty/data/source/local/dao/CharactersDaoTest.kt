package com.emdp.rickandmorty.data.source.local.dao

import androidx.paging.PagingSource
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun clearAll_shouldDeleteRows() = runBlocking {
        dao.upsertAll(CharacterEntityMother.mockList01())
        assertEquals(2, dao.count())

        dao.clearAll()
        assertEquals(0, dao.count())
    }

    @Test
    fun pagingSource_withFilters_shouldReturnFilteredSortedData() = runBlocking {
        dao.upsertAll(CharacterEntityMother.mockList02())

        val pagingSource: PagingSource<Int, CharacterEntity> = dao.pagingSource(
            name = SM, status = ALIVE, gender = null, species = HUMAN, type = null
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
        assertEquals(listOf(MORTY_SMITH, SUMMER_SMITH), names)
    }

    companion object {
        private const val RICK_SANCHEZ = "Rick Sanchez"
        private const val MORTY_SMITH = "Morty Smith"
        private const val SUMMER_SMITH = "Summer Smith"
        private const val ALIVE = "Alive"
        private const val HUMAN = "Human"
        private const val SM = "sm"
    }
}