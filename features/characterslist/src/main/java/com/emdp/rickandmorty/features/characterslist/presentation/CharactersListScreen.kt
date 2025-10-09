package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
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

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    val refresh = characters.loadState.refresh
                    val isEmpty = characters.itemCount == 0

                    when {
                        isEmpty && refresh is LoadState.Loading -> {
                            LoadingStateView(
                                useMultiverseLoader = true,
                                showMessage = false
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(maxHeight)
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
            key = { index -> items[index]?.id ?: "placeholder_$index" },
            contentType = { "character" }
        ) { index ->
            val character = items[index] ?: return@items
            with(character) {
                RickAndMortyCharacterCard(
                    characterName = name,
                    imageUrl = imageUrl,
                    onClick = { onCharacterClick(id) }
                )
            }

        }

        val appendState = items.loadState.append
        if (appendState is LoadState.Loading) {
            item(
                key = "loading_footer",
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
        }

        if (appendState is LoadState.Error) {
            item(
                key = "error_footer",
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }
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