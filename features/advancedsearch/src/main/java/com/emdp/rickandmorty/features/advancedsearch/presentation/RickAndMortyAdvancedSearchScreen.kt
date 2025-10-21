package com.emdp.rickandmorty.features.advancedsearch.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.core.ui.searchbar.RickAndMortySearchBar
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.core.ui.theme.PortalGreen
import com.emdp.rickandmorty.core.ui.topbar.RickAndMortyTopBar
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.features.advancedsearch.R
import com.emdp.rickandmorty.features.advancedsearch.presentation.sections.CharactersGridSection
import com.emdp.rickandmorty.features.advancedsearch.presentation.sections.FiltersContentSection
import org.koin.androidx.compose.koinViewModel

@Composable
fun RickAndMortyAdvancedSearchScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: RickAndMortyAdvancedSearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    RickAndMortyGradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                RickAndMortyTopBar(
                    title = stringResource(R.string.advanced_search_title),
                    showBack = false,
                    onBackClick = null,
                    bottomAccentBrush = AppTextStyles.multiverseTitle(),
                )
            },
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            AdvancedSearchContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding),
                uiState = uiState,
                searchQuery = filters.name.orEmpty(),
                filters = filters,
                onSearchQueryChange = viewModel::updateName,
                onStatusChange = viewModel::updateStatus,
                onSpeciesChange = viewModel::updateSpecies,
                onGenderChange = viewModel::updateGender,
                onTypeChange = viewModel::updateType,
                onClearFilters = viewModel::clearFilters,
                onLoadMore = viewModel::loadMore,
                onCharacterClick = onCharacterClick,
                onRetry = viewModel::search,
                onSearch = viewModel::search
            )
        }
    }
}

@Composable
private fun AdvancedSearchContent(
    modifier: Modifier = Modifier,
    uiState: AdvancedSearchUiState,
    searchQuery: String,
    filters: CharactersFilterModel,
    onSearchQueryChange: (String) -> Unit,
    onStatusChange: (String?) -> Unit,
    onSpeciesChange: (String?) -> Unit,
    onGenderChange: (String?) -> Unit,
    onTypeChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onLoadMore: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onSearch: () -> Unit
) {
    var showFilters by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    Column(modifier = modifier) {
        SearchBarWithFilterButton(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onSearch = onSearch,
            showFilters = showFilters,
            onToggleFilters = { showFilters = !showFilters }
        )

        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            FiltersContentSection(
                filters = filters,
                onStatusChange = onStatusChange,
                onSpeciesChange = onSpeciesChange,
                onGenderChange = onGenderChange,
                onTypeChange = onTypeChange,
                onTypeSearch = onSearch,
                onClearFilters = onClearFilters,
                onClose = { showFilters = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp, vertical = VERTICAL_PADDING.dp)
            )
        }

        Spacer(modifier = Modifier.height(VERTICAL_PADDING.dp))

        CharactersGridSection(
            uiState = uiState,
            gridState = gridState,
            onCharacterClick = onCharacterClick,
            onRetry = onRetry,
            onLoadMore = onLoadMore,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SearchBarWithFilterButton(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    showFilters: Boolean,
    onToggleFilters: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HORIZONTAL_PADDING.dp, vertical = VERTICAL_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VERTICAL_PADDING.dp)
    ) {
        RickAndMortySearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = stringResource(R.string.advanced_search_placeholder),
            modifier = Modifier.weight(1f),
            showGradientBorder = true,
            searchOnType = false,
            onSearch = onSearch
        )

        IconButton(
            onClick = onToggleFilters,
            modifier = Modifier
                .background(
                    color = if (showFilters) PortalGreen else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = stringResource(R.string.advanced_search_filters),
                tint = if (showFilters) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private const val HORIZONTAL_PADDING = 16
private const val VERTICAL_PADDING = 8