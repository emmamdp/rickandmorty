package com.emdp.rickandmorty.features.advancedsearch.presentation.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.emdp.rickandmorty.core.ui.chip.RickAndMortyFilterChip
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.features.advancedsearch.R

@Composable
fun getChipStatus(
    filters: CharactersFilterModel,
    onStatusChange: (String?) -> Unit
) = listOf(
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_status_alive),
        selected = filters.status == STATUS_ALIVE,
        onClick = {
            onStatusChange(if (filters.status == STATUS_ALIVE) null else STATUS_ALIVE)
        },
        showGradientBorder = true
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_status_dead),
        selected = filters.status == STATUS_DEAD,
        onClick = {
            onStatusChange(if (filters.status == STATUS_DEAD) null else STATUS_DEAD)
        },
        showGradientBorder = true
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_status_unknown),
        selected = filters.status == STATUS_UNKNOWN,
        onClick = {
            onStatusChange(if (filters.status == STATUS_UNKNOWN) null else STATUS_UNKNOWN)
        },
        showGradientBorder = true
    )
)

@Composable
fun getChipSpecies(
    filters: CharactersFilterModel,
    onSpeciesChange: (String?) -> Unit
) = listOf(
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_species_human),
        selected = filters.species == SPECIES_HUMAN,
        onClick = {
            onSpeciesChange(if (filters.species == SPECIES_HUMAN) null else SPECIES_HUMAN)
        },
        showGradientBorder = true
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_species_alien),
        selected = filters.species == SPECIES_ALIEN,
        onClick = {
            onSpeciesChange(if (filters.species == SPECIES_ALIEN) null else SPECIES_ALIEN)
        },
        showGradientBorder = true
    )
)

@Composable
fun getChipGender(
    filters: CharactersFilterModel,
    onGenderChange: (String?) -> Unit
) = listOf(
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_male),
        selected = filters.gender == GENDER_MALE,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_MALE) null else GENDER_MALE)
        },
        showGradientBorder = true
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_female),
        selected = filters.gender == GENDER_FEMALE,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_FEMALE) null else GENDER_FEMALE)
        },
        showGradientBorder = true
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_genderless),
        selected = filters.gender == GENDER_GENDERLESS,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_GENDERLESS) null else GENDER_GENDERLESS)
        },
        showGradientBorder = true
    ),
    RickAndMortyFilterChip(
        label = stringResource(R.string.filter_gender_unknown),
        selected = filters.gender == GENDER_UNKNOWN,
        onClick = {
            onGenderChange(if (filters.gender == GENDER_UNKNOWN) null else GENDER_UNKNOWN)
        },
        showGradientBorder = true
    )
)

private const val STATUS_ALIVE = "alive"
private const val STATUS_DEAD = "dead"
private const val STATUS_UNKNOWN = "unknown"
private const val SPECIES_HUMAN = "Human"
private const val SPECIES_ALIEN = "Alien"
private const val GENDER_MALE = "male"
private const val GENDER_FEMALE = "female"
private const val GENDER_GENDERLESS = "genderless"
private const val GENDER_UNKNOWN = "unknown"