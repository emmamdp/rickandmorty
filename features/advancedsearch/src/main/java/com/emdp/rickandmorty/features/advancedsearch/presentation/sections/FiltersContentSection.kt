package com.emdp.rickandmorty.features.advancedsearch.presentation.sections

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.emdp.rickandmorty.core.ui.text.AppTextStyles
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.features.advancedsearch.R
import com.emdp.rickandmorty.features.advancedsearch.presentation.uimodel.CharacterType
import com.emdp.rickandmorty.features.advancedsearch.presentation.uimodel.CharacterTypeConstants

@Composable
fun FiltersContentSection(
    filters: CharactersFilterModel,
    onStatusChange: (String?) -> Unit,
    onSpeciesChange: (String?) -> Unit,
    onGenderChange: (String?) -> Unit,
    onTypeChange: (String) -> Unit,
    onTypeSearch: () -> Unit,
    onClearFilters: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    val selectedType = remember(filters.type) {
        CharacterTypeConstants.ALL_TYPES.find { it.apiValue == filters.type.orEmpty() }
            ?: CharacterTypeConstants.ALL_TYPES.first()
    }

    Card(
        modifier = modifier
            .border(
                width = 2.dp,
                brush = AppTextStyles.multiverseTitle(),
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(CARD_PADDING.dp),
            verticalArrangement = Arrangement.spacedBy(CONTENT_SPACING.dp)
        ) {
            FilterHeader(onClose = onClose)

            FilterStatusSection(
                filters = filters,
                onStatusChange = onStatusChange
            )

            FilterSpeciesSection(
                filters = filters,
                onSpeciesChange = onSpeciesChange
            )

            FilterGenderSection(
                filters = filters,
                onGenderChange = onGenderChange
            )

            FilterTypeSection(
                selectedType = selectedType,
                typeDropdownExpanded = typeDropdownExpanded,
                onTypeDropdownToggle = { typeDropdownExpanded = !typeDropdownExpanded },
                onTypeChange = onTypeChange,
                onTypeSearch = onTypeSearch,
                onDismissDropdown = { typeDropdownExpanded = false }
            )

            if (hasActiveFilters(filters)) {
                ClearFiltersButton(onClick = onClearFilters)
            }
        }
    }
}

@Composable
private fun FilterHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.advanced_search_filters),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.filter_close),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FilterStatusSection(
    filters: CharactersFilterModel,
    onStatusChange: (String?) -> Unit
) {
    Text(
        text = stringResource(R.string.filter_status),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(CHIP_SPACING.dp)
    ) {
        getChipStatus(filters, onStatusChange)
    }
}

@Composable
private fun FilterSpeciesSection(
    filters: CharactersFilterModel,
    onSpeciesChange: (String?) -> Unit
) {
    Text(
        text = stringResource(R.string.filter_species),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(CHIP_SPACING.dp)
    ) {
        getChipSpecies(filters, onSpeciesChange)
    }
}

@Composable
private fun FilterGenderSection(
    filters: CharactersFilterModel,
    onGenderChange: (String?) -> Unit
) {
    Text(
        text = stringResource(R.string.filter_gender),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(CHIP_SPACING.dp)
    ) {
        getChipGender(filters, onGenderChange)
    }
}

@Composable
private fun FilterTypeSection(
    selectedType: CharacterType,
    typeDropdownExpanded: Boolean,
    onTypeDropdownToggle: () -> Unit,
    onTypeChange: (String) -> Unit,
    onTypeSearch: () -> Unit,
    onDismissDropdown: () -> Unit
) {
    Text(
        text = stringResource(R.string.filter_type),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )

    Box {
        OutlinedTextField(
            value = stringResource(selectedType.displayNameRes),
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(stringResource(R.string.filter_type_placeholder)) },
            trailingIcon = {
                IconButton(onClick = onTypeDropdownToggle) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    brush = AppTextStyles.multiverseTitle(),
                    shape = MaterialTheme.shapes.small
                ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                disabledContainerColor = Color.White.copy(alpha = 0.3f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.small
        )

        DropdownMenu(
            expanded = typeDropdownExpanded,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .heightIn(max = DROPDOWN_MAX_HEIGHT.dp)
        ) {
            CharacterTypeConstants.ALL_TYPES.forEach { type ->
                DropdownMenuItem(
                    text = { Text(stringResource(type.displayNameRes)) },
                    onClick = {
                        onTypeChange(type.apiValue)
                        onDismissDropdown()
                        if (type.apiValue.isNotEmpty()) {
                            onTypeSearch()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ClearFiltersButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.filter_clear_all),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun hasActiveFilters(filters: CharactersFilterModel): Boolean {
    return filters.status != null ||
            filters.species != null ||
            filters.gender != null ||
            filters.type != null
}

private const val CARD_PADDING = 16
private const val CONTENT_SPACING = 6
private const val CHIP_SPACING = 8
private const val DROPDOWN_MAX_HEIGHT = 300