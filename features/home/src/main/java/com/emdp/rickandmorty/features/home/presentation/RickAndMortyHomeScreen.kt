package com.emdp.rickandmorty.features.home.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.core.ui.theme.MortyYellow
import com.emdp.rickandmorty.core.ui.theme.PortalGreen
import com.emdp.rickandmorty.core.ui.theme.RickCyan
import com.emdp.rickandmorty.core.ui.theme.TextPrimaryDark
import com.emdp.rickandmorty.core.ui.theme.TextPrimaryLight
import com.emdp.rickandmorty.core.ui.theme.TextSecondaryDark
import com.emdp.rickandmorty.core.ui.theme.TextSecondaryLight
import com.emdp.rickandmorty.core.ui.theme.TextTertiaryDark
import com.emdp.rickandmorty.core.ui.theme.TextTertiaryLight
import com.emdp.rickandmorty.core.ui.topbar.RickAndMortyTopBar
import com.emdp.rickandmorty.features.home.R
import kotlinx.coroutines.delay

@Composable
fun RickAndMortyHomeScreen(
    onNavigateToCharacters: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    RickAndMortyGradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                RickAndMortyTopBar(
                    title = stringResource(R.string.app_name),
                    showBack = false,
                    onBackClick = null,
                    bottomAccentBrush = AppTextStyles.multiverseTitle(),
                    actions = { GetSettingsIcon(context, isDark) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WelcomeCard(isDark = isDark)
                getHomeCards(isDark, onNavigateToCharacters, onNavigateToSearch)
            }
        }
    }
}

@Composable
private fun getHomeCards(
    isDark: Boolean,
    onNavigateToCharacters: () -> Unit,
    onNavigateToSearch: () -> Unit
) = listOf(
    QuickAccessCard(
        title = stringResource(R.string.home_explore_characters),
        description = stringResource(R.string.home_explore_characters_desc),
        imageRes = R.drawable.characters,
        onClick = onNavigateToCharacters,
        isDark = isDark
    ),
    QuickAccessCard(
        title = stringResource(R.string.home_advanced_search),
        description = stringResource(R.string.home_advanced_search_desc),
        imageRes = R.drawable.search,
        onClick = onNavigateToSearch,
        isDark = isDark
    )
)

@Composable
private fun GetSettingsIcon(context: Context, isDark: Boolean) {
    val toastMessage = stringResource(R.string.settings_toast_message)
    IconButton(
        onClick = {
            Toast.makeText(
                context,
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings),
            tint = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
    }
}

@Composable
private fun WelcomeCard(isDark: Boolean) {
    val scale = remember { Animatable(0.9f) }
    GetCardLaunchedEffect(scale)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            RickCyan.copy(alpha = 0.6f),
                            PortalGreen.copy(alpha = 0.5f),
                            MortyYellow.copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_welcome),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = getTextColor(isDark)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = getSubtitleColor(isDark)
                )
            }
        }
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val scale = remember { Animatable(0.9f) }
    GetCardLaunchedEffect(scale)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = getTextColor(isDark)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = getDescriptionColor(isDark)
                )
            }
        }
    }
}

private fun getTextColor(isDark: Boolean) =
    if (isDark) TextPrimaryDark else TextPrimaryLight

private fun getSubtitleColor(isDark: Boolean) =
    if (isDark) TextSecondaryDark else TextSecondaryLight

private fun getDescriptionColor(isDark: Boolean) =
    if (isDark) TextTertiaryDark else TextTertiaryLight

@Composable
private fun GetCardLaunchedEffect(scale: Animatable<Float, AnimationVector1D>) =
    LaunchedEffect(Unit) {
        delay(100)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }