package com.emdp.rickandmorty.features.advancedsearch.presentation.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emdp.rickandmorty.core.ui.card.RickAndMortyCharacterCard
import com.emdp.rickandmorty.core.ui.stateviews.EmptyStateView
import com.emdp.rickandmorty.core.ui.stateviews.ErrorStateView
import com.emdp.rickandmorty.core.ui.stateviews.LoadingStateView
import com.emdp.rickandmorty.features.advancedsearch.R
import com.emdp.rickandmorty.features.advancedsearch.presentation.AdvancedSearchUiState

@Composable
fun CharactersGridSection(
    uiState: AdvancedSearchUiState,
    gridState: LazyGridState,
    onCharacterClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is AdvancedSearchUiState.Idle -> {
            EmptyStateView(
                message = stringResource(R.string.advanced_search_empty_state),
                modifier = modifier
            )
        }

        is AdvancedSearchUiState.Loading -> {
            LoadingStateView(
                modifier = modifier,
                useMultiverseLoader = true,
                showMessage = false
            )
        }

        is AdvancedSearchUiState.Success -> {
            CharactersGrid(
                uiState = uiState,
                gridState = gridState,
                onCharacterClick = onCharacterClick,
                onLoadMore = onLoadMore,
                modifier = modifier
            )
        }

        is AdvancedSearchUiState.Error -> {
            ErrorStateView(
                message = stringResource(uiState.messageRes),
                onRetry = onRetry,
                retryButtonText = stringResource(R.string.advanced_search_retry),
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CharactersGrid(
    uiState: AdvancedSearchUiState.Success,
    gridState: LazyGridState,
    onCharacterClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount

            lastVisibleItem != null &&
                    lastVisibleItem.index >= totalItems - LOAD_MORE_THRESHOLD &&
                    uiState.hasMorePages &&
                    !uiState.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    if (uiState.characters.isEmpty() && !uiState.isLoadingMore) {
        EmptyStateView(
            message = stringResource(R.string.advanced_search_no_results),
            modifier = modifier
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            state = gridState,
            contentPadding = PaddingValues(
                horizontal = HORIZONTAL_PADDING.dp,
                vertical = VERTICAL_PADDING.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(GRID_SPACING.dp),
            verticalArrangement = Arrangement.spacedBy(GRID_SPACING.dp),
            modifier = modifier
        ) {
            items(
                items = uiState.characters,
                key = { it.id }
            ) { character ->
                RickAndMortyCharacterCard(
                    characterName = character.name,
                    imageUrl = character.imageUrl,
                    onClick = { onCharacterClick(character.id) }
                )
            }

            if (uiState.isLoadingMore || uiState.hasMorePages) {
                item(
                    key = "loading_footer",
                    span = { androidx.compose.foundation.lazy.grid.GridItemSpan(GRID_COLUMNS) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HORIZONTAL_PADDING.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

private const val GRID_COLUMNS = 2
private const val LOAD_MORE_THRESHOLD = 3
private const val HORIZONTAL_PADDING = 16
private const val VERTICAL_PADDING = 8
private const val GRID_SPACING = 12