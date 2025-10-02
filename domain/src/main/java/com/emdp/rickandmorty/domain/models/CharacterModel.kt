package com.emdp.rickandmorty.domain.models

import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus

data class CharacterModel(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: CharacterGender,
    val originName: String,
    val locationName: String,
    val imageUrl: String,
    val episodeUrls: List<String>,
    val createdIso: String
)