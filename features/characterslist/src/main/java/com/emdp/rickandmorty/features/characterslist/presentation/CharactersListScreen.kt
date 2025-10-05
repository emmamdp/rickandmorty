package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.core.ui.loader.MultiverseLoader
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.core.ui.theme.Neutral90
import com.emdp.rickandmorty.core.ui.topbar.RickAndMortyTopBar
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.features.characterslist.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersListScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersListViewModel = koinViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    val gridState = remember { LazyGridState() }

    LaunchedEffect(true) { viewModel.loadInitial() }

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
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) {
                val refresh = characters.loadState.refresh
                val isEmpty = characters.itemCount == 0

                when {
                    isEmpty && refresh is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CharactersLoading() }
                    }

                    isEmpty && refresh is LoadState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CharactersError(
                                message = stringResource(R.string.characters_list_error_placeholder),
                                onRetry = { characters.retry() }
                            )
                        }
                    }

                    isEmpty && refresh is LoadState.NotLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CharactersEmpty() }
                    }

                    else -> {
                        CharactersGrid(
                            items = characters,
                            onCharacterClick = onCharacterClick,
                            gridState = gridState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CharactersLoading() {
    MultiverseLoader(showMessage = false)
}

@Composable
private fun CharactersError(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = stringResource(R.string.characters_list_retry))
        }
    }
}

@Composable
private fun CharactersEmpty() {
    Text(
        text = stringResource(R.string.characters_list_empty_placeholder),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun CharactersGrid(
    items: androidx.paging.compose.LazyPagingItems<CharacterModel>,
    onCharacterClick: (Int) -> Unit,
    gridState: LazyGridState
) {
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey { it.id },
            contentType = { "character" }
        ) { index ->
            val character = items[index] ?: return@items
            CharacterCard(
                character = character,
                onClick = { onCharacterClick(character.id) }
            )
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

@Composable
private fun CharacterCard(
    character: CharacterModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Neutral90),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .semantics {
                role = Role.Button
                contentDescription = character.name
            }
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.imageUrl)
                    .crossfade(true)
                    .memoryCacheKey(character.imageUrl)
                    .diskCacheKey(character.imageUrl)
                    .build(),
                contentDescription = stringResource(
                    R.string.character_item_image_cd,
                    character.name
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 1f)
                    .shadow(
                        elevation = 6.dp,
                        shape = MaterialTheme.shapes.medium,
                        clip = true
                    )
                    .clip(MaterialTheme.shapes.medium)
            )

            Text(
                text = character.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
        }
    }
}