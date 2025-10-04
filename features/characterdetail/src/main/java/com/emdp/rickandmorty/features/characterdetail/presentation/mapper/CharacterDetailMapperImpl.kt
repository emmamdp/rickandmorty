package com.emdp.rickandmorty.features.characterdetail.presentation.mapper

import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import com.emdp.rickandmorty.core.ui.theme.ChipBlue
import com.emdp.rickandmorty.core.ui.theme.ChipGray
import com.emdp.rickandmorty.core.ui.theme.ChipGreen
import com.emdp.rickandmorty.core.ui.theme.ChipPurple
import com.emdp.rickandmorty.core.ui.theme.ChipRed
import com.emdp.rickandmorty.core.ui.theme.ChipTeal
import com.emdp.rickandmorty.core.ui.theme.onFor
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.FEMALE
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.GENDERLESS
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.MALE
import com.emdp.rickandmorty.domain.models.enums.CharacterGender.UNKNOWN as GENDER_UNKNOWN
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus.ALIVE
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus.DEAD
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus.UNKNOWN
import com.emdp.rickandmorty.features.characterdetail.R
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.CharacterDetailUiModel
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.InfoItemModel
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.UiChipModel

class CharacterDetailMapperImpl(
    private val resources: Resources
) : CharacterDetailMapper {

    override fun getUiModel(model: CharacterModel): CharacterDetailUiModel {
        val name = model.name
        val imageUrl = model.imageUrl
        val imageCd = resources.getString(R.string.character_detail_image_cd, name)

        val statusChip = buildStatusChip(model.status)
        val speciesChip = buildSpeciesChip(model.species)

        val infoItems = buildInfoItems(
            gender = model.gender,
            type = model.type,
            origin = model.originName,
            location = model.locationName,
            episodes = model.episodeUrls.size
        )

        return CharacterDetailUiModel(
            name = name,
            imageUrl = imageUrl,
            imageContentDescription = imageCd,
            statusChip = statusChip,
            speciesChip = speciesChip,
            infoItems = infoItems
        )
    }

    private fun buildStatusChip(status: CharacterStatus): UiChipModel {
        val (text, bg) = when (status) {
            ALIVE -> R.string.character_status_alive.getString() to ChipGreen
            DEAD -> R.string.character_status_dead.getString() to ChipRed
            UNKNOWN -> R.string.character_status_unknown.getString() to ChipGray
        }
        return UiChipModel(text = text, containerColor = bg, labelColor = onFor(bg))
    }

    private fun buildSpeciesChip(speciesRaw: String): UiChipModel {
        val species = speciesRaw.ifBlank { R.string.character_detail_unknown.getString() }
        val bg: Color = when (speciesRaw.lowercase()) {
            "human" -> ChipBlue
            "alien" -> ChipPurple
            "robot" -> ChipGray
            "humanoid" -> ChipTeal
            "unknown" -> ChipGray
            else -> ChipGreen
        }
        return UiChipModel(text = species, containerColor = bg, labelColor = onFor(bg))
    }

    private fun buildInfoItems(
        gender: CharacterGender,
        type: String,
        origin: String,
        location: String,
        episodes: Int
    ): List<InfoItemModel> {
        val unknown = R.string.character_detail_unknown.getString()
        val genderText = when (gender) {
            FEMALE -> R.string.character_gender_female.getString()
            MALE -> R.string.character_gender_male.getString()
            GENDERLESS -> R.string.character_gender_genderless.getString()
            GENDER_UNKNOWN -> R.string.character_gender_unknown.getString()
        }

        return listOf(
            InfoItemModel(
                label = R.string.character_detail_gender.getString(),
                value = genderText
            ),
            InfoItemModel(
                label = R.string.character_detail_type.getString(),
                value = type.ifBlank { unknown }
            ),
            InfoItemModel(
                label = R.string.character_detail_origin.getString(),
                value = origin.ifBlank { unknown }
            ),
            InfoItemModel(
                label = R.string.character_detail_location.getString(),
                value = location.ifBlank { unknown }
            ),
            InfoItemModel(
                label = R.string.character_detail_episodes.getString(),
                value = episodes.toString()
            )
        )
    }

    private fun Int.getString(): String = resources.getString(this)
}