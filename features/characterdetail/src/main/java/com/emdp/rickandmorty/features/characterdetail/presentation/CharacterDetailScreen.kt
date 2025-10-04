package com.emdp.rickandmorty.features.characterdetail.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.core.ui.loader.MultiverseLoader
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.core.ui.topbar.RickAndMortyTopBar
import com.emdp.rickandmorty.features.characterdetail.R
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.CharacterDetailUiModel
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.InfoItemModel
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.UiChipModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBackClick: () -> Unit,
    viewModel: CharacterDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(characterId) {
        viewModel.load(characterId)
    }

    RickAndMortyGradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                RickAndMortyTopBar(
                    title = stringResource(R.string.character_detail_title),
                    showBack = true,
                    onBackClick = onBackClick,
                    bottomAccentBrush = AppTextStyles.multiverseTitle()
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is CharacterDetailUiState.Loading -> MultiverseLoader(showMessage = false)

                    is CharacterDetailUiState.Error ->
                        CharacterDetailError(
                            message = stringResource(R.string.character_detail_error),
                            onRetry = { viewModel.retry() }
                        )

                    is CharacterDetailUiState.Content ->
                        CharacterDetailContent(uiModel = state.uiModel)
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailError(
    message: String,
    onRetry: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text(text = stringResource(R.string.character_detail_retry))
        }
    }
}

@Composable
private fun CharacterDetailContent(uiModel: CharacterDetailUiModel) {
    val scroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        CharacterHeaderImage(
            imageUrl = uiModel.imageUrl,
            contentDescription = uiModel.imageContentDescription,
            modifier = Modifier.padding(top = 8.dp)
        )

        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = uiModel.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                UiChip(chip = uiModel.statusChip)
                UiChip(chip = uiModel.speciesChip)
            }

            Spacer(Modifier.height(16.dp))

            InfoPanel(items = uiModel.infoItems)

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CharacterHeaderImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    ratio: Float = 1.2f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .shadow(elevation = 4.dp, shape = RectangleShape, clip = false)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f)
                        ),
                        startY = size.height * 0.75f,
                        endY = size.height
                    )
                )
            }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun UiChip(chip: UiChipModel) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = chip.text,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = chip.containerColor,
            labelColor = chip.labelColor,
            disabledContainerColor = chip.containerColor,
            disabledLabelColor = chip.labelColor
        )
    )
}

@Composable
private fun InfoPanel(
    items: List<InfoItemModel>,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            items.forEachIndexed { index, item ->
                InfoRow(item = item)
                if (index != items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        thickness = DividerDefaults.Thickness,
                        color = Color.White.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(item: InfoItemModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}