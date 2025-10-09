package com.emdp.rickandmorty.core.ui.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emdp.rickandmorty.core.ui.R
import com.emdp.rickandmorty.core.ui.theme.Neutral90

@Composable
fun RickAndMortyCharacterCard(
    characterName: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Neutral90),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .semantics {
                role = Role.Button
                contentDescription = characterName
            }
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .memoryCacheKey(imageUrl)
                    .diskCacheKey(imageUrl)
                    .build(),
                contentDescription = stringResource(
                    R.string.character_item_image_cd,
                    characterName
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
                text = characterName,
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