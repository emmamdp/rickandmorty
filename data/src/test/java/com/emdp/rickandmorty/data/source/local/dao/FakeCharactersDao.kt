package com.emdp.rickandmorty.data.source.local.dao

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

internal class FakeCharactersDao : CharactersDao {

    private val mutex = Mutex()
    private val store = linkedMapOf<Int, CharacterEntity>()

    override fun pagingSource(
        name: String?, status: String?, gender: String?, species: String?, type: String?
    ): PagingSource<Int, CharacterEntity> {
        val snapshot = { filteredSorted(name, status, gender, species, type) }
        return object : PagingSource<Int, CharacterEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
                return LoadResult.Page(
                    data = snapshot(),
                    prevKey = null,
                    nextKey = null
                )
            }
            override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int? = null
        }
    }

    override suspend fun getCharacterById(id: Int): CharacterEntity? = mutex.withLock { store[id] }

    override suspend fun upsertAll(characters: List<CharacterEntity>) {
        mutex.withLock { characters.forEach { store[it.id] = it } }
    }

    override suspend fun clearAll() = mutex.withLock { store.clear() }

    override suspend fun count(): Int = mutex.withLock { store.size }

    private fun filteredSorted(
        name: String?, status: String?, gender: String?, species: String?, type: String?
    ): List<CharacterEntity> {
        val n = name?.lowercase(Locale.ROOT)
        return store.values.asSequence()
            .filter { n == null || it.name.lowercase(Locale.ROOT).contains(n) }
            .filter { status == null || it.status == status }
            .filter { gender == null || it.gender == gender }
            .filter { species == null || it.species == species }
            .filter { type == null || it.type == type }
            .sortedWith(compareBy<CharacterEntity> { it.name.lowercase(Locale.ROOT) }.thenBy { it.id })
            .toList()
    }
}