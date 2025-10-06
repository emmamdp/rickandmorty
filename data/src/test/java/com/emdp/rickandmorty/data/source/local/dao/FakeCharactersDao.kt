package com.emdp.rickandmorty.data.source.local.dao

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class FakeCharactersDao : CharactersDao {

    private val mutex = Mutex()
    private val store = linkedMapOf<Int, CharacterEntity>()

    override fun pagingSource(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): PagingSource<Int, CharacterEntity> {
        val snapshot = { filteredSorted(name, status, species, type, gender) }

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

    override suspend fun getCharacterById(id: Int): CharacterEntity? =
        mutex.withLock { store[id] }

    override suspend fun upsertAll(characters: List<CharacterEntity>) {
        mutex.withLock { characters.forEach { store[it.id] = it } }
    }

    override suspend fun clearAll() = mutex.withLock { store.clear() }

    private fun filteredSorted(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): List<CharacterEntity> {
        val fName = name?.takeIf { it.isNotBlank() }
        val fStatus = status?.takeIf { it.isNotBlank() }
        val fSpecies = species?.takeIf { it.isNotBlank() }
        val fType = type?.takeIf { it.isNotBlank() }
        val fGender = gender?.takeIf { it.isNotBlank() }

        fun like(value: String?, filter: String?): Boolean {
            if (filter == null) return true
            val v = value ?: return false
            return v.contains(filter, ignoreCase = true)
        }

        fun eq(value: String?, filter: String?): Boolean {
            if (filter == null) return true
            val v = value?.takeIf { it.isNotBlank() } ?: return false
            return v.equals(filter, ignoreCase = true)
        }

        return store.values.asSequence()
            .filter { e -> like(e.name, fName) }
            .filter { e -> eq(e.status, fStatus) }
            .filter { e -> like(e.species, fSpecies) }
            .filter { e -> eq(e.type, fType) }
            .filter { e -> eq(e.gender, fGender) }
            .sortedBy { it.id }
            .toList()
    }
}