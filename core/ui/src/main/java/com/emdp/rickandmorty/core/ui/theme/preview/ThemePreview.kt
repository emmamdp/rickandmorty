package com.emdp.rickandmorty.core.ui.theme.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emdp.rickandmorty.core.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeGallery(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rick & Morty – Theme Gallery") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                    rememberTopAppBarState()
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Headings over surface
            Text("Headings & Body on Surface", style = MaterialTheme.typography.headlineSmall)
            Text(
                "This is body text on surface to quickly check contrast with onSurface.",
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Buttons row
            Text("Buttons", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {}) { Text("Primary") }
                OutlinedButton(onClick = {}) { Text("Outlined") }
                TextButton(onClick = {}) { Text("Text") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {}, enabled = false) { Text("Disabled") }
                OutlinedButton(onClick = {}, enabled = false) { Text("Disabled") }
                TextButton(onClick = {}, enabled = false) { Text("Disabled") }
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Cards & containers
            Text("Cards & Containers", style = MaterialTheme.typography.titleMedium)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ElevatedCard (surface)", style = MaterialTheme.typography.titleSmall)
                    Text("Use this to verify surface/onSurface in both themes.")
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Card (surfaceVariant)", style = MaterialTheme.typography.titleSmall)
                    Text("Check surfaceVariant/onSurfaceVariant and outline contrast.")
                }
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Text fields (normal + error)
            Text("Text fields", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = "Wubba lubba dub dub",
                onValueChange = {},
                label = { Text("Label") },
                supportingText = { Text("Supporting text") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "Portal 42",
                onValueChange = {},
                isError = true,
                label = { Text("Error label") },
                supportingText = { Text("Something went wrong") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Icon buttons on primary & on surface
            Text("Icon buttons", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = {}) {
                    // Replace with any vector from your project if you have one.
                    // If you don't have icons set up yet, this placeholder avoids compile issues:
                    Text("☆")
                }
                IconButton(
                    onClick = {},
                    enabled = false,
                    colors = IconButtonDefaults.iconButtonColors()
                ) {
                    Text("☆")
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Primary/Secondary/Tertiary sample",
                style = MaterialTheme.typography.titleMedium
            )
            ColorSwatchesRow()
        }
    }
}

@Composable
private fun ColorSwatchesRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ColorSwatchBox(
            title = "Primary",
            container = MaterialTheme.colorScheme.primaryContainer,
            onContainer = MaterialTheme.colorScheme.onPrimaryContainer
        )
        ColorSwatchBox(
            title = "Secondary",
            container = MaterialTheme.colorScheme.secondaryContainer,
            onContainer = MaterialTheme.colorScheme.onSecondaryContainer
        )
        ColorSwatchBox(
            title = "Tertiary",
            container = MaterialTheme.colorScheme.tertiaryContainer,
            onContainer = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
private fun ColorSwatchBox(
    title: String,
    container: Color,
    onContainer: Color
) {
    Card(colors = CardDefaults.cardColors(container)) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .width(120.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, color = onContainer, style = MaterialTheme.typography.titleSmall)
            Text(
                "Container sample text",
                color = onContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(name = "Light – Theme Gallery", showBackground = true)
@Composable
fun ThemeGalleryLightPreview() {
    RickAndMortyTheme(darkTheme = false) {
        ThemeGallery()
    }
}

@Preview(name = "Dark – Theme Gallery", showBackground = true)
@Composable
fun ThemeGalleryDarkPreview() {
    RickAndMortyTheme(darkTheme = true) {
        ThemeGallery()
    }
}