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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCharacters()
    }

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
                    val isEmpty = state.characters.isEmpty()

                    when {
                        isEmpty && state.isLoading -> {
                            LoadingStateView(
                                useMultiverseLoader = true,
                                showMessage = false
                            )
                        }

                        isEmpty && state.error != null -> {
                            ErrorStateView(
                                message = stringResource(R.string.characters_list_error_placeholder),
                                onRetry = { viewModel.loadCharacters(refresh = true) },
                                retryButtonText = stringResource(R.string.characters_list_retry)
                            )
                        }

                        isEmpty && !state.isLoading -> {
                            EmptyStateView(
                                message = stringResource(R.string.characters_list_empty_placeholder)
                            )
                        }

                        else -> {
                            CharactersGrid(
                                characters = state.characters,
                                isLoadingMore = state.isLoadingMore,
                                hasMore = state.hasMore,
                                error = state.error,
                                onCharacterClick = onCharacterClick,
                                onLoadMore = { viewModel.loadMore() },
                                onRetry = { viewModel.loadMore() },
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
    characters: List<CharacterModel>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    error: com.emdp.rickandmorty.core.common.result.AppError?,
    onCharacterClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount

            lastVisibleItem != null &&
                    lastVisibleItem.index >= totalItems - 3 &&
                    hasMore &&
                    !isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            RickAndMortyCharacterCard(
                characterName = character.name,
                imageUrl = character.imageUrl,
                onClick = { onCharacterClick(character.id) }
            )
        }

        if (isLoadingMore) {
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

        if (error != null && !isLoadingMore && characters.isNotEmpty()) {
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
                    Button(onClick = onRetry) {
                        Text(text = stringResource(R.string.characters_list_retry))
                    }
                }
            }
        }
    }
}