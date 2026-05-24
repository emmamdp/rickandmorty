package com.emdp.rickandmorty.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emdp.rickandmorty.core.ui.R
import com.emdp.rickandmorty.core.ui.theme.Neutral10
import com.emdp.rickandmorty.core.ui.theme.Neutral98
import com.emdp.rickandmorty.core.ui.theme.PortalGreen
import com.emdp.rickandmorty.core.ui.theme.RickCyan

@Composable
fun RickAndMortyCharacterCard(
    characterName: String,
    status: String,
    species: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    val portalBrush = Brush.sweepGradient(
        colors = listOf(PortalGreen, RickCyan, PortalGreen)
    )

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Neutral98),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .semantics {
                role = Role.Button
                contentDescription = characterName
            }
            .border(1.dp, Color.LightGray.copy(alpha = 0.2f), MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .memoryCacheKey(imageUrl)
                        .diskCacheKey(imageUrl)
                        .build(),
                    contentDescription = stringResource(R.string.character_item_image_cd, characterName),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, portalBrush, CircleShape),
                    loading = {
                        PortalPlaceholder(characterName, portalBrush)
                    },
                    error = {
                        PortalPlaceholder(characterName, portalBrush)
                    }
                )
            }

            Text(
                text = characterName,
                style = MaterialTheme.typography.titleMedium,
                color = Neutral10,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statusColor = when (status.lowercase()) {
                    "alive" -> PortalGreen
                    "dead" -> Color(0xFFEF5350)
                    else -> Color.Gray
                }
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                Text(
                    text = "$status - $species",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PortalPlaceholder(name: String, brush: Brush) {
    Box(
        modifier = Modifier.fillMaxSize().clip(CircleShape).background(brush),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(0.85f).clip(CircleShape).background(Neutral98),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = PortalGreen,
                textAlign = TextAlign.Center
            )
        }
    }
}
