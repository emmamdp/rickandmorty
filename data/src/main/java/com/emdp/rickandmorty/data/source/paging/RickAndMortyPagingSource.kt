package com.emdp.rickandmorty.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel

class RickAndMortyPagingSource(
    private val remoteSource: CharactersRemoteSource,
    private val charactersDao: CharactersDao,
    private val toEntity: (List<CharacterModel>) -> List<CharacterEntity>,
    private val filter: CharactersFilterModel?
) : PagingSource<Int, CharacterModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val page = params.key ?: 1

        return try {
            val query = filter.toQueryParts()

            when (val result = remoteSource.getCharacters(
                page = page,
                name = query.name,
                status = query.status,
                species = query.species,
                type = query.type,
                gender = query.gender
            )) {
                is DataResult.Error -> LoadResult.Error(Exception("API Error"))
                is DataResult.Success -> {
                    val characters = result.data.results

                    try {
                        charactersDao.upsertAll(toEntity(characters))
                    } catch (e: Exception) {
                    }

                    LoadResult.Page(
                        data = characters,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = result.data.nextPage
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun CharactersFilterModel?.toQueryParts(): QueryParts {
        return QueryParts(
            name = this?.name?.takeIf { it.isNotBlank() },
            status = this?.status?.lowercase()?.takeIf { it.isNotBlank() },
            species = this?.species?.takeIf { it.isNotBlank() },
            type = this?.type?.takeIf { it.isNotBlank() },
            gender = this?.gender?.lowercase()?.takeIf { it.isNotBlank() }
        )
    }

    private data class QueryParts(
        val name: String?,
        val status: String?,
        val species: String?,
        val type: String?,
        val gender: String?
    )
}