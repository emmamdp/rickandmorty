package com.emdp.rickandmorty.features.advancedsearch.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.core.ui.card.RickAndMortyCharacterCard
import com.emdp.rickandmorty.core.ui.chip.RickAndMortyFilterChip
import com.emdp.rickandmorty.core.ui.searchbar.RickAndMortySearchBar
import com.emdp.rickandmorty.core.ui.stateviews.EmptyStateView
import com.emdp.rickandmorty.core.ui.stateviews.ErrorStateView
import com.emdp.rickandmorty.core.ui.stateviews.LoadingStateView
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.core.ui.topbar.RickAndMortyTopBar
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.features.advancedsearch.R
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

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState is AdvancedSearchUiState.Success && uiState.hasMorePages) {
            onLoadMore()
        }
    }

    Column(modifier = modifier) {
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

            IconButton(onClick = { showFilters = !showFilters }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = stringResource(R.string.advanced_search_filters),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            FiltersSection(
                filters = filters,
                onStatusChange = onStatusChange,
                onSpeciesChange = onSpeciesChange,
                onGenderChange = onGenderChange,
                onTypeChange = onTypeChange,
                onTypeSearch = onSearch,
                onClearFilters = onClearFilters,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp, vertical = VERTICAL_PADDING.dp)
            )
        }

        Spacer(modifier = Modifier.height(VERTICAL_PADDING.dp))

        GetUiStateViewContent(uiState, gridState, onCharacterClick, onRetry)
    }
}

@Composable
private fun GetUiStateViewContent(
    uiState: AdvancedSearchUiState,
    gridState: LazyGridState,
    onCharacterClick: (Int) -> Unit,
    onRetry: () -> Unit
) = when (uiState) {
    is AdvancedSearchUiState.Idle -> {
        EmptyStateView(
            message = stringResource(R.string.advanced_search_empty_state),
            modifier = Modifier.fillMaxSize()
        )
    }

    is AdvancedSearchUiState.Loading -> {
        LoadingStateView(
            modifier = Modifier.fillMaxSize(),
            useMultiverseLoader = true,
            showMessage = false
        )
    }

    is AdvancedSearchUiState.Success -> {
        if (uiState.characters.isEmpty()) {
            EmptyStateView(
                message = stringResource(R.string.advanced_search_no_results),
                modifier = Modifier.fillMaxSize()
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
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.characters,
                    key = { it.id }
                ) { character ->
                    with(character) {
                        RickAndMortyCharacterCard(
                            characterName = name,
                            imageUrl = imageUrl,
                            onClick = { onCharacterClick(id) }
                        )
                    }
                }

                if (uiState.hasMorePages) {
                    item {
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

    is AdvancedSearchUiState.LoadingMore -> {}

    is AdvancedSearchUiState.Error -> ErrorStateView(
        message = stringResource(uiState.messageRes),
        onRetry = onRetry,
        retryButtonText = stringResource(R.string.advanced_search_retry),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun FiltersSection(
    filters: CharactersFilterModel,
    onStatusChange: (String?) -> Unit,
    onSpeciesChange: (String?) -> Unit,
    onGenderChange: (String?) -> Unit,
    onTypeChange: (String) -> Unit,
    onTypeSearch: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(HORIZONTAL_PADDING.dp),
            verticalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_status),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VERTICAL_PADDING.dp)
            ) {
                getChipStatus(filters, onStatusChange)
            }

            Text(
                text = stringResource(R.string.filter_species),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VERTICAL_PADDING.dp)
            ) {
                getChipSpecies(filters, onSpeciesChange)
            }

            Text(
                text = stringResource(R.string.filter_gender),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VERTICAL_PADDING.dp)
            ) {
                getChipGender(filters, onGenderChange)
            }

            Text(
                text = stringResource(R.string.filter_type),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = filters.type.orEmpty(),
                onValueChange = onTypeChange,
                placeholder = { Text(stringResource(R.string.filter_type_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onTypeSearch() }
                )
            )

            if (hasActiveFilters(filters)) {
                TextButton(
                    onClick = onClearFilters,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(stringResource(R.string.filter_clear_all))
                }
            }
        }
    }
}

@Composable
private fun getChipStatus(
    filters: CharactersFilterModel,
    onStatusChange: (String?) -> Unit
) = listOf(
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_status_alive),
        selected = filters.status == STATUS_ALIVE,
        onClick = {
            onStatusChange(if (filters.status == STATUS_ALIVE) null else STATUS_ALIVE)
        }
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_status_dead),
        selected = filters.status == STATUS_DEAD,
        onClick = {
            onStatusChange(if (filters.status == STATUS_DEAD) null else STATUS_DEAD)
        }
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_status_unknown),
        selected = filters.status == STATUS_UNKNOWN,
        onClick = {
            onStatusChange(if (filters.status == STATUS_UNKNOWN) null else STATUS_UNKNOWN)
        }
    )
)

@Composable
private fun getChipSpecies(
    filters: CharactersFilterModel,
    onSpeciesChange: (String?) -> Unit
) = listOf(
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_species_human),
        selected = filters.species == SPECIES_HUMAN,
        onClick = {
            onSpeciesChange(if (filters.species == SPECIES_HUMAN) null else SPECIES_HUMAN)
        }
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_species_alien),
        selected = filters.species == SPECIES_ALIEN,
        onClick = {
            onSpeciesChange(if (filters.species == SPECIES_ALIEN) null else SPECIES_ALIEN)
        }
    )
)

@Composable
private fun getChipGender(
    filters: CharactersFilterModel,
    onGenderChange: (String?) -> Unit
) = listOf(
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_male),
        selected = filters.gender == GENDER_MALE,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_MALE) null else GENDER_MALE)
        }
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_female),
        selected = filters.gender == GENDER_FEMALE,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_FEMALE) null else GENDER_FEMALE)
        }
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_genderless),
        selected = filters.gender == GENDER_GENDERLESS,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_GENDERLESS) null else GENDER_GENDERLESS)
        }
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_unknown),
        selected = filters.gender == GENDER_UNKNOWN,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_UNKNOWN) null else GENDER_UNKNOWN)
        }
    )
)

private fun hasActiveFilters(filters: CharactersFilterModel): Boolean {
    return filters.status != null ||
            filters.species != null ||
            filters.gender != null ||
            filters.type != null
}

private const val GRID_COLUMNS = 2
private const val LOAD_MORE_THRESHOLD = 3
private const val HORIZONTAL_PADDING = 16
private const val VERTICAL_PADDING = 8
private const val GRID_SPACING = 12

private const val STATUS_ALIVE = "alive"
private const val STATUS_DEAD = "dead"
private const val STATUS_UNKNOWN = "unknown"
private const val SPECIES_HUMAN = "Human"
private const val SPECIES_ALIEN = "Alien"
private const val GENDER_MALE = "male"
private const val GENDER_FEMALE = "female"
private const val GENDER_GENDERLESS = "genderless"
private const val GENDER_UNKNOWN = "unknown"