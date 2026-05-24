package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.core.ui.card.RickAndMortyCharacterCard
import com.emdp.rickandmorty.core.ui.searchbar.RickAndMortySearchBar
import com.emdp.rickandmorty.core.ui.stateviews.EmptyStateView
import com.emdp.rickandmorty.core.ui.stateviews.ErrorStateView
import com.emdp.rickandmorty.core.ui.stateviews.LoadingStateView
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.core.ui.topbar.RickAndMortyTopBar
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.features.characterslist.R
import org.koin.androidx.compose.koinViewModel

private const val CONTENT_TYPE_CHARACTER = "character"
private const val KEY_LOADING_FOOTER = "loading_footer"
private const val KEY_ERROR_FOOTER = "error_footer"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersListScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersListViewModel = koinViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }

    RickAndMortyGradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                RickAndMortyTopBar(
                    title = stringResource(R.string.characters_list_title),
                    showBack = false,
                    onBackClick = null,
                    bottomAccentBrush = AppTextStyles.multiverseTitle(),
                )
            },
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) {
                RickAndMortySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_characters),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    onSearch = {
                        if (searchQuery.isNotEmpty()) {
                            viewModel.applyFilter(
                                CharactersFilterModel(
                                    name = searchQuery,
                                    status = null,
                                    species = null,
                                    type = null,
                                    gender = null
                                )
                            )
                        } else {
                            viewModel.clearFilter()
                        }
                    },
                    showGradientBorder = true
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    val refresh = characters.loadState.refresh
                    val isEmpty = characters.itemCount == 0

                    val isNoResultsError =
                        refresh is LoadState.Error && refresh.error == AppError.NoResultsFound

                    when {
                        isEmpty && refresh is LoadState.Loading -> {
                            LoadingStateView(
                                useMultiverseLoader = true,
                                showMessage = false
                            )
                        }

                        isEmpty && isNoResultsError -> {
                            EmptyStateView(
                                message = stringResource(R.string.characters_list_empty_placeholder)
                            )
                        }

                        isEmpty && refresh is LoadState.Error -> {
                            ErrorStateView(
                                message = stringResource(R.string.characters_list_error_placeholder),
                                onRetry = { characters.retry() },
                                retryButtonText = stringResource(R.string.characters_list_retry)
                            )
                        }

                        isEmpty && refresh is LoadState.NotLoading -> {
                            EmptyStateView(
                                message = stringResource(R.string.characters_list_empty_placeholder)
                            )
                        }

                        else -> {
                            CharactersGrid(
                                items = characters,
                                onCharacterClick = onCharacterClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharactersGrid(
    items: androidx.paging.compose.LazyPagingItems<CharacterModel>,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey { it.id },
            contentType = items.itemContentType { CONTENT_TYPE_CHARACTER }
        ) { index ->
            val character = items[index]
            if (character != null) {
                RickAndMortyCharacterCard(
                    characterName = character.name,
                    status = character.status.name,
                    species = character.species,
                    imageUrl = character.imageUrl,
                    onClick = { onCharacterClick(character.id) }
                )
            }
        }

        val appendState = items.loadState.append
        if (appendState is LoadState.Loading) {
            item(
                key = KEY_LOADING_FOOTER,
                span = { GridItemSpan(2) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = com.emdp.rickandmorty.core.ui.theme.PortalGreen
                    )
                }
            }
        }

        if (appendState is LoadState.Error) {
            item(
                key = KEY_ERROR_FOOTER,
                span = { GridItemSpan(2) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { items.retry() }) {
                        Text(text = stringResource(R.string.characters_list_retry))
                    }
                }
            }
        }
    }
}
