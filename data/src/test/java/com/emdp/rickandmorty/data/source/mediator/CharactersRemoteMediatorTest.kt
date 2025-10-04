package com.emdp.rickandmorty.data.source.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import androidx.paging.RemoteMediator.MediatorResult.Success
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.RemoteKeysDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntity
import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntityMother
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.models.CharactersPageModelMother
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.check
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.IOException
import java.util.stream.Stream

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class CharactersRemoteMediatorTest {

    private val db = mock(RickAndMortyDatabase::class.java)
    private val charactersDao = mock(CharactersDao::class.java)
    private val remoteKeysDao = mock(RemoteKeysDao::class.java)
    private val remoteSource = mock(CharactersRemoteSource::class.java)

    private var filter: CharactersFilterModel? = CharactersFilterModelMother.mock()
    private val nullFilter = CharactersFilterModelMother.mockNull()

    @Test
    fun `REFRESH inserts entities, remoteKeys and returns filter to remote`() =
        runTest {
            val page = samplePage(page = 1)

            whenever(
                remoteSource.getCharacters(
                    page = eq(1),
                    name = eq(filter?.name),
                    status = eq(filter?.status),
                    species = eq(filter?.species),
                    type = eq(filter?.type),
                    gender = eq(filter?.gender)
                )
            ).thenReturn(DataResult.Success(page))

            val result = mediator().load(LoadType.REFRESH, state = emptyState())

            assertSuccess(result)
            with(inOrder(remoteKeysDao, charactersDao)) {
                verify(remoteKeysDao).clearRemoteKeys()
                verify(charactersDao).clearAll()
            }
            verifyInsertedFrom(page)
            verify(remoteSource).getCharacters(
                page = eq(1),
                name = eq(filter?.name),
                status = eq(filter?.status),
                species = eq(filter?.species),
                type = eq(filter?.type),
                gender = eq(filter?.gender)
            )
        }

    @Test
    fun `REFRESH with empty results ends pagination`() = runTest {
        whenever(
            remoteSource.getCharacters(
                page = eq(1),
                name = isNull(),
                status = isNull(),
                species = isNull(),
                type = isNull(),
                gender = isNull()
            )
        ).thenReturn(DataResult.Success(data = CharactersPageModelMother.mockEmpty()))

        val result = mediator(f = nullFilter).load(
            LoadType.REFRESH,
            state = onePageState(data = emptyList())
        )

        assertSuccess(result, endReached = true)
        verify(charactersDao).upsertAll(characters = check { list -> assertTrue(list.isEmpty()) })
        verify(remoteKeysDao).upsertAll(keys = check { list -> assertTrue(list.isEmpty()) })
        verify(remoteSource).getCharacters(
            page = eq(1),
            name = isNull(),
            status = isNull(),
            species = isNull(),
            type = isNull(),
            gender = isNull()
        )
    }

    @Test
    fun `APPEND when remote returns error maps to MediatorResult_Error`() = runTest {
        val existing = CharacterEntityMother.mockRandomListEntities(min = 1, max = 3)
        val lastId = existing.last().id

        whenever(remoteKeysDao.remoteKeysById(lastId))
            .thenReturn(RemoteKeysEntityMother.mockSetId(lastId))
        whenever(
            remoteSource.getCharacters(
                page = eq(2),
                name = isNull(),
                status = isNull(),
                species = isNull(),
                type = isNull(),
                gender = isNull()
            )
        ).thenReturn(DataResult.Error(AppError.Network()))

        val result =
            mediator(f = nullFilter).load(LoadType.APPEND, state = onePageState(data = existing))

        assertTrue(result is MediatorResult.Error)
        assertTrue((result as MediatorResult.Error).throwable is IOException)
        verify(charactersDao, never()).upsertAll(any())
        verify(remoteKeysDao, never()).upsertAll(any())
        verify(remoteSource).getCharacters(
            page = eq(2),
            name = isNull(),
            status = isNull(),
            species = isNull(),
            type = isNull(),
            gender = isNull()
        )
    }

    @Test
    fun `REFRESH error mapping - Serialization returns IOException`() = runTest {
        whenever(
            remoteSource.getCharacters(
                page = eq(1),
                name = anyOrNull(),
                status = anyOrNull(),
                species = anyOrNull(),
                type = anyOrNull(),
                gender = anyOrNull()
            )
        ).thenReturn(DataResult.Error(error = AppError.Serialization()))

        val result = mediator(f = nullFilter).load(
            LoadType.REFRESH,
            state = onePageState(data = emptyList())
        )

        assertTrue(result is MediatorResult.Error)
        assertTrue((result as MediatorResult.Error).throwable is IOException)
    }

    @Test
    fun `REFRESH error mapping - Unexpected returns RuntimeException`() = runTest {
        whenever(
            remoteSource.getCharacters(
                page = eq(1),
                name = anyOrNull(),
                status = anyOrNull(),
                species = anyOrNull(),
                type = anyOrNull(),
                gender = anyOrNull()
            )
        ).thenReturn(
            DataResult.Error(
                error = AppError.Unexpected(Throwable("x"))
            )
        )

        val result = mediator(f = nullFilter).load(
            LoadType.REFRESH,
            state = onePageState(data = emptyList())
        )

        assertTrue(result is MediatorResult.Error)
        assertTrue((result as MediatorResult.Error).throwable is RuntimeException)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendCases")
    fun run(case: AppendCase) = runTest {
        val existing = CharacterEntityMother.mockRandomListEntities(min = 1, max = 3)
        val lastId = existing.last().id
        val nextKey = case.remoteKeys.nextKey
        var stubbedPage: CharactersPageModel? = null

        whenever(remoteKeysDao.remoteKeysById(lastId))
            .thenReturn(case.remoteKeys)
        if (case.expectRemoteCall && nextKey != null) {
            val page = samplePage(page = nextKey, hasNext = case.hasNext)
            stubbedPage = page
            whenever(
                remoteSource.getCharacters(
                    page = eq(nextKey),
                    name = isNull(),
                    status = isNull(),
                    species = isNull(),
                    type = isNull(),
                    gender = isNull()
                )
            ).thenReturn(DataResult.Success(page))
        }

        val result = mediator(nullFilter).load(LoadType.APPEND, state = onePageState(existing))

        assertSuccess(result, endReached = case.expectedEndReached)
        if (case.expectRemoteCall) {
            val page = checkNotNull(stubbedPage)
            verify(remoteKeysDao).remoteKeysById(lastId)
            verifyInsertedFrom(page)
            verify(remoteSource).getCharacters(
                page = eq(checkNotNull(nextKey)),
                name = isNull(),
                status = isNull(),
                species = isNull(),
                type = isNull(),
                gender = isNull()
            )
        } else {
            verify(remoteKeysDao).remoteKeysById(lastId)
            verifyNoWritesOrRemote()
            verifyNoMoreInteractions(remoteSource, charactersDao, remoteKeysDao)
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("prependCases")
    fun run(case: PrependCase) = runTest {
        val existing = CharacterEntityMother.mockRandomListEntities(min = 2, max = 4)
        val firstId = existing.first().id
        val prevKey = case.remoteKeys.prevKey
        var stubbedPage: CharactersPageModel? = null

        whenever(remoteKeysDao.remoteKeysById(firstId))
            .thenReturn(case.remoteKeys)
        if (case.expectRemoteCall && prevKey != null) {
            val page = samplePage(page = prevKey)
            stubbedPage = page
            whenever(
                remoteSource.getCharacters(
                    page = eq(prevKey),
                    name = eq(filter?.name),
                    status = eq(filter?.status),
                    species = eq(filter?.species),
                    type = eq(filter?.type),
                    gender = eq(filter?.gender)
                )
            ).thenReturn(DataResult.Success(page))
        }

        val result = mediator().load(LoadType.PREPEND, state = onePageState(existing))

        assertSuccess(result, endReached = case.expectedEndReached)

        if (case.expectRemoteCall) {
            val page = checkNotNull(stubbedPage)
            verify(remoteKeysDao).remoteKeysById(firstId)
            verifyInsertedFrom(page)
            verify(remoteSource).getCharacters(
                page = eq(checkNotNull(prevKey)),
                name = eq(filter?.name),
                status = eq(filter?.status),
                species = eq(filter?.species),
                type = eq(filter?.type),
                gender = eq(filter?.gender)
            )
        } else {
            verify(remoteKeysDao).remoteKeysById(firstId)
            verifyNoWritesOrRemote()
            verifyNoMoreInteractions(remoteSource, charactersDao, remoteKeysDao)
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("refreshAnchorCases")
    fun run(case: RefreshAnchorCase) = runTest {
        val existing = CharacterEntityMother.mockRandomListEntities(min = 10, max = 12)
        val order = inOrder(remoteKeysDao, charactersDao)

        if (case.remoteKeyExists) {
            whenever(remoteKeysDao.remoteKeysById(11)).thenReturn(
                RemoteKeysEntity(
                    characterId = 11,
                    prevKey = 3,
                    nextKey = 5,
                    updatedAt = 0L
                )
            )
        } else {
            whenever(remoteKeysDao.remoteKeysById(11)).thenReturn(null)
        }

        val expectedPage = samplePage(page = case.expectedPage)
        whenever(
            remoteSource.getCharacters(
                page = eq(case.expectedPage),
                name = isNull(),
                status = isNull(),
                species = isNull(),
                type = isNull(),
                gender = isNull()
            )
        ).thenReturn(DataResult.Success(expectedPage))

        val result = mediator(nullFilter).load(
            LoadType.REFRESH,
            state = onePageState(data = existing, anchor = 1)
        )

        assertSuccess(result, endReached = false)
        verify(remoteKeysDao).remoteKeysById(11)
        order.verify(remoteKeysDao).clearRemoteKeys()
        order.verify(charactersDao).clearAll()
        verifyInsertedFrom(expectedPage)
        verify(remoteSource).getCharacters(
            page = eq(case.expectedPage),
            name = isNull(),
            status = isNull(),
            species = isNull(),
            type = isNull(),
            gender = isNull()
        )
    }

    private suspend fun verifyInsertedFrom(page: CharactersPageModel) {
        verify(charactersDao).upsertAll(
            check { inserted ->
                assertEquals(
                    page.results.map { it.id }.toSet(),
                    inserted.map { it.id }.toSet()
                )
            }
        )
        verify(remoteKeysDao).upsertAll(
            check { keys ->
                assertEquals(page.results.size, keys.size)
                keys.forEach {
                    assertEquals(page.prevPage, it.prevKey)
                    assertEquals(page.nextPage, it.nextKey)
                }
            }
        )
    }

    private suspend fun verifyNoWritesOrRemote() {
        verify(remoteSource, never()).getCharacters(
            page = any(),
            name = anyOrNull(),
            status = anyOrNull(),
            species = anyOrNull(),
            type = anyOrNull(),
            gender = anyOrNull()
        )
        verify(charactersDao, never()).upsertAll(any())
        verify(remoteKeysDao, never()).upsertAll(any())
    }

    private fun toEntity(models: List<CharacterModel>): List<CharacterEntity> =
        models.map {
            CharacterEntity(
                id = it.id,
                name = it.name,
                status = it.status.name,
                species = it.species,
                type = it.type.ifBlank { null },
                gender = it.gender.name,
                imageUrl = it.imageUrl,
                originName = it.originName.ifBlank { null },
                locationName = it.locationName.ifBlank { null },
                created = it.createdIso.ifBlank { null }
            )
        }

    private fun mediator(f: CharactersFilterModel? = filter) =
        CharactersRemoteMediator(
            database = db,
            charactersDao = charactersDao,
            remoteKeysDao = remoteKeysDao,
            remoteSource = remoteSource,
            toEntity = ::toEntity,
            filter = f,
            runInTransaction = { block -> block() }
        )

    private fun onePageState(
        data: List<CharacterEntity>,
        anchor: Int? = null,
        pageSize: Int = TEST_PAGE_SIZE
    ) = PagingState<Int, CharacterEntity>(
        pages = listOf(Page(data = data, prevKey = null, nextKey = null)),
        anchorPosition = anchor,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0
    )

    private fun emptyState(pageSize: Int = TEST_PAGE_SIZE) =
        PagingState<Int, CharacterEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )

    private fun assertSuccess(result: MediatorResult, endReached: Boolean = false) {
        assertTrue(result is Success)
        val success = result as Success
        assertEquals(endReached, success.endOfPaginationReached)
    }

    private fun samplePage(
        page: Int,
        size: Int = TEST_PAGE_SIZE,
        hasNext: Boolean = true
    ): CharactersPageModel {
        val startId = (page - 1) * size + 1
        val items =
            (0 until size).map { i -> CharacterModelMother.mockRandomCharacter(startId + i) }
        val next = if (hasNext) page + 1 else null
        val prev = if (page > 1) page - 1 else null
        val totalPages = if (hasNext) page + 1 else page
        val totalCount = totalPages * size
        return CharactersPageModel(
            count = totalCount,
            pages = totalPages,
            nextPage = next,
            prevPage = prev,
            results = items
        )
    }

    internal data class AppendCase(
        val name: String,
        val remoteKeys: RemoteKeysEntity,
        val expectRemoteCall: Boolean,
        val hasNext: Boolean,
        val expectedEndReached: Boolean
    )

    internal data class PrependCase(
        val name: String,
        val remoteKeys: RemoteKeysEntity,
        val expectRemoteCall: Boolean,
        val expectedEndReached: Boolean
    )

    internal data class RefreshAnchorCase(
        val name: String,
        val remoteKeyExists: Boolean,
        val expectedPage: Int
    )

    companion object {
        private const val TEST_PAGE_SIZE = 3

        @JvmStatic
        fun refreshAnchorCases(): Stream<RefreshAnchorCase> = Stream.of(
            RefreshAnchorCase(
                name = "REFRESH con anchor → remoteKey existe (usa nextKey-1 = 4)",
                remoteKeyExists = true,
                expectedPage = 4
            ),
            RefreshAnchorCase(
                name = "REFRESH con anchor → remoteKey NO existe (fallback a page 1)",
                remoteKeyExists = false,
                expectedPage = 1
            )
        )

        @JvmStatic
        fun appendCases(): Stream<AppendCase> = Stream.of(
            AppendCase(
                name = "APPEND → nextKey=2 (hay siguiente página)",
                remoteKeys = RemoteKeysEntity(
                    characterId = 3,
                    prevKey = 1,
                    nextKey = 2,
                    updatedAt = 0L
                ),
                expectRemoteCall = true,
                hasNext = true,
                expectedEndReached = false
            ),
            AppendCase(
                name = "APPEND → nextKey=null (fin de paginación, no hay llamada remota)",
                remoteKeys = RemoteKeysEntity(
                    characterId = 3,
                    prevKey = 1,
                    nextKey = null,
                    updatedAt = 0L
                ),
                expectRemoteCall = false,
                hasNext = false,
                expectedEndReached = true
            )
        )

        @JvmStatic
        fun prependCases(): Stream<PrependCase> = Stream.of(
            PrependCase(
                name = "PREPEND → prevKey=1 (hay página previa)",
                remoteKeys = RemoteKeysEntity(
                    characterId = 2,
                    prevKey = 1,
                    nextKey = 2,
                    updatedAt = 0L
                ),
                expectRemoteCall = true,
                expectedEndReached = false
            ),
            PrependCase(
                name = "PREPEND → prevKey=null (fin por delante, sin llamada remota)",
                remoteKeys = RemoteKeysEntity(
                    characterId = 2,
                    prevKey = null,
                    nextKey = 2,
                    updatedAt = 0L
                ),
                expectRemoteCall = false,
                expectedEndReached = true
            )
        )
    }
}