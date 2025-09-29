package com.emdp.rickandmorty.core.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emdp.rickandmorty.core.ui.R
import com.emdp.rickandmorty.core.ui.text.GradientText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RickAndMortyTopBar(
    title: String,
    showBack: Boolean,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleBrush: Brush? = null,
    showBottomAccent: Boolean = true,
    bottomAccentBrush: Brush? = null,
    scrimEnabled: Boolean = true,
    scrimAlphaStart: Float = 0.18f
) {
    Column(modifier = modifier) {
        Box {
            if (scrimEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = scrimAlphaStart),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            CenterAlignedTopAppBar(
                title = {
                    if (titleBrush == null) {
                        Text(
                            text = title,
                            style = titleStyle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics { heading() }
                        )
                    } else {
                        GradientText(text = title, brush = titleBrush, style = titleStyle)
                    }
                },
                navigationIcon = {
                    if (showBack && onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.topbar_back)
                            )
                        }
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        if (showBottomAccent && bottomAccentBrush != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(bottomAccentBrush)
            )
        }
    }
}