package com.emdp.rickandmorty.data.source.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

internal class RickAndMortyPagingSourceTest {

    private val remoteSource: CharactersRemoteSource = mock()
    private val charactersDao: CharactersDao = mock()
    private val toEntity: (List<CharacterModel>) -> List<CharacterEntity> = mock()

    @Test
    fun `load returns Page when api call is successful`() = runTest {
        val characters = CharacterModelMother.mockList()
        val pageModel = CharactersPageModel(
            count = 826,
            pages = 42,
            nextPage = 2,
            prevPage = null,
            results = characters
        )

        whenever(
            remoteSource.getCharacters(
                page = 1,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        ).thenReturn(DataResult.Success(pageModel))

        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(characters, page.data)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load returns Page with null nextKey when last page`() = runTest {
        val characters = listOf(CharacterModelMother.mockRick())
        val pageModel = CharactersPageModel(
            count = 826,
            pages = 42,
            nextPage = null,
            prevPage = 41,
            results = characters
        )

        whenever(
            remoteSource.getCharacters(
                page = 42,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        ).thenReturn(DataResult.Success(pageModel))

        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 42,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(characters, page.data)
        assertEquals(41, page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun `load with filter passes filter to remote source`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        val characters = listOf(CharacterModelMother.mockRick())
        val pageModel = CharactersPageModel(
            count = 10,
            pages = 1,
            nextPage = null,
            prevPage = null,
            results = characters
        )

        whenever(
            remoteSource.getCharacters(
                page = 1,
                name = "Rick",
                status = "alive",
                species = "Human",
                type = null,
                gender = "male"
            )
        ).thenReturn(DataResult.Success(pageModel))

        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = filter
        )

        pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        verify(remoteSource).getCharacters(
            page = 1,
            name = "Rick",
            status = "alive",
            species = "Human",
            type = null,
            gender = "male"
        )
    }

    @Test
    fun `load returns Error when api call fails`() = runTest {
        whenever(
            remoteSource.getCharacters(
                page = 1,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        ).thenReturn(
            DataResult.Error(
                com.emdp.rickandmorty.core.common.result.AppError.Network(
                    cause = Exception("Network error")
                )
            )
        )

        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `getRefreshKey returns correct page when anchorPosition is in middle`() {
        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = null
        )

        val pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf(CharacterModelMother.mockRick()),
                prevKey = null,
                nextKey = 2
            ),
            PagingSource.LoadResult.Page(
                data = listOf(CharacterModelMother.mockMorty()),
                prevKey = 1,
                nextKey = 3
            )
        )

        val state = PagingState(
            pages = pages,
            anchorPosition = 1,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(2, refreshKey)
    }

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() {
        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = null
        )

        val state = PagingState<Int, CharacterModel>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertNull(refreshKey)
    }

    @Test
    fun `getRefreshKey uses nextKey when prevKey is null`() {
        val pagingSource = RickAndMortyPagingSource(
            remoteSource = remoteSource,
            charactersDao = charactersDao,
            toEntity = toEntity,
            filter = null
        )

        val pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf(CharacterModelMother.mockRick()),
                prevKey = null,
                nextKey = 2
            )
        )

        val state = PagingState(
            pages = pages,
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(1, refreshKey)
    }
}