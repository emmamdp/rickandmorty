package com.emdp.rickandmorty.navigation.bottombar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emdp.rickandmorty.core.ui.theme.MortyYellow
import com.emdp.rickandmorty.core.ui.theme.Neutral10
import com.emdp.rickandmorty.core.ui.theme.Neutral30
import com.emdp.rickandmorty.core.ui.theme.Neutral90
import com.emdp.rickandmorty.core.ui.theme.Neutral98
import com.emdp.rickandmorty.core.ui.theme.PortalGreen
import com.emdp.rickandmorty.core.ui.theme.RickCyan
import com.emdp.rickandmorty.navigation.bottombar.RickAndMortyBottomNavItem.PainterNavItem
import com.emdp.rickandmorty.navigation.bottombar.RickAndMortyBottomNavItem.VectorNavItem

@Composable
fun RickAndMortyBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val items = bottomItems()
    val glassColor = if (isDark) Neutral10.copy(alpha = 0.62f) else Neutral98.copy(alpha = 0.78f)
    val selectedColor = MortyYellow
    val unselectedColor = if (isDark) Neutral90.copy(alpha = 0.75f) else Neutral30
    val barShape = RoundedCornerShape(28.dp)

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .navigationBarsPadding()
            .shadow(elevation = 10.dp, shape = barShape, clip = false)
            .clip(barShape)
            .height(50.dp),
        containerColor = glassColor,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "IconScale"
            )
            val pillAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "PillAlpha"
            )

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Box(
                        modifier = Modifier.height(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                RickCyan.copy(alpha = 0.16f * pillAlpha),
                                                PortalGreen.copy(alpha = 0.10f * pillAlpha)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                GetIconBottomBar(
                                    item = item,
                                    tintColor = selectedColor,
                                    scale = scale
                                )
                            }
                        } else {
                            GetIconBottomBar(
                                item = item,
                                tintColor = unselectedColor,
                                scale = scale
                            )
                        }

                        BottomBarIndicator(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            selected = selected,
                            selectedColor = selectedColor
                        )
                    }
                },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 4.dp),
                enabled = true
            )
        }
    }
}

@Composable
private fun GetIconBottomBar(
    item: RickAndMortyBottomNavItem,
    tintColor: Color,
    scale: Float
) = when (item) {
    is PainterNavItem -> Icon(
        painter = painterResource(id = item.painterResId),
        contentDescription = stringResource(item.labelResId),
        tint = tintColor,
        modifier = Modifier
            .size(26.dp)
            .scale(scale)
    )

    is VectorNavItem -> Icon(
        imageVector = item.icon,
        contentDescription = stringResource(item.labelResId),
        tint = tintColor,
        modifier = Modifier
            .size(28.dp)
            .scale(scale)
    )
}

@Composable
private fun BottomBarIndicator(
    modifier: Modifier,
    selected: Boolean,
    selectedColor: Color
) {
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 14.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "IndicatorWidth"
    )
    Box(
        modifier = modifier
            .padding(bottom = 2.dp)
            .height(3.dp)
            .width(indicatorWidth)
            .clip(RoundedCornerShape(2.dp))
            .background(selectedColor)
    )
}