package com.emdp.rickandmorty.data.repository

import androidx.paging.PagingSource
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity

class EmptyPagingSource : PagingSource<Int, CharacterEntity>() {

    override fun getRefreshKey(
        state: androidx.paging.PagingState<Int, CharacterEntity>
    ) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
        return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
    }
}