package com.emdp.rickandmorty.data.source.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.FakeCharacterRemoteKeysDao
import com.emdp.rickandmorty.data.source.local.dao.FakeCharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.data.source.local.entity.CharacterRemoteKeysEntity
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.Executor

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class RickAndMortyRemoteMediatorTest {

    private val remoteSource: CharactersRemoteSource = mock()
    private val mapper: CharacterLocalMapper = mock()
    private val charactersDao = FakeCharactersDao()
    private val remoteKeysDao = FakeCharacterRemoteKeysDao()

    private val database: RickAndMortyDatabase = mock()

    private lateinit var mediator: RickAndMortyRemoteMediator

    @BeforeEach
    fun setUp() {
        whenever(database.charactersDao()).thenReturn(charactersDao)
        whenever(database.remoteKeysDao()).thenReturn(remoteKeysDao)

        val immediateExecutor = Executor { it.run() }
        whenever(database.transactionExecutor).thenReturn(immediateExecutor)
        whenever(database.queryExecutor).thenReturn(immediateExecutor)

        mediator = RickAndMortyRemoteMediator(
            database = database,
            remoteSource = remoteSource,
            mapper = mapper,
            filter = null
        )
    }

    @Test
    fun `load with PREPEND returns Success and endOfPaginationReached true`() =
        runTest(UnconfinedTestDispatcher()) {
            val result = mediator.load(LoadType.PREPEND, createPagingState())

            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

    @Test
    fun `load with APPEND when nextKey is null returns Success and endOfPaginationReached true`() =
        runTest(UnconfinedTestDispatcher()) {
            val lastItem = CharacterEntityMother.mockRick()
            val pagingState = createPagingState(
                listOf(PagingSource.LoadResult.Page(listOf(lastItem), null, null))
            )
            remoteKeysDao.insertAll(listOf(CharacterRemoteKeysEntity(lastItem.id, null, null)))

            val result = mediator.load(LoadType.APPEND, pagingState)

            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

    @Test
    fun `initialize returns SKIP_INITIAL_REFRESH when local data exists`() =
        runTest(UnconfinedTestDispatcher()) {
            charactersDao.upsertAll(listOf(CharacterEntityMother.mockRick()))

            val result = mediator.initialize()

            assertTrue(result == RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
        }

    @Test
    fun `initialize returns LAUNCH_INITIAL_REFRESH when local data is empty`() =
        runTest(UnconfinedTestDispatcher()) {
            charactersDao.clearAll()
            val result = mediator.initialize()
            assertTrue(result == RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
        }

    private fun createPagingState(
        pages: List<PagingSource.LoadResult.Page<Int, CharacterEntity>> = emptyList()
    ): PagingState<Int, CharacterEntity> {
        return PagingState(pages, null, PagingConfig(20), 0)
    }
}
