package com.emdp.rickandmorty.features.characterdetail.presentation.mapper

import android.content.res.Resources
import com.emdp.rickandmorty.core.ui.theme.ChipBlue
import com.emdp.rickandmorty.core.ui.theme.ChipGray
import com.emdp.rickandmorty.core.ui.theme.ChipGreen
import com.emdp.rickandmorty.core.ui.theme.ChipRed
import com.emdp.rickandmorty.core.ui.theme.onFor
import com.emdp.rickandmorty.features.characterdetail.R
import com.emdp.rickandmorty.features.characterdetail.domain.models.CharacterModelMother
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CharacterDetailMapperImplTest {

    private val resources: Resources = mock()
    private lateinit var mapper: CharacterDetailMapper

    @BeforeEach
    fun setUp() {
        mapper = CharacterDetailMapperImpl(resources)

        with(resources) {
            whenever(getString(eq(R.string.character_detail_image_cd), any()))
                .thenAnswer { inv ->
                    val nameArg = inv.arguments[1]
                    "Imagen de $nameArg"
                }

            whenever(getString(R.string.character_detail_image_cd, "Rick Sanchez"))
                .thenReturn("Imagen de Rick Sanchez")
            whenever(getString(R.string.character_detail_image_cd, "Morty Smith"))
                .thenReturn("Imagen de Morty Smith")

            whenever(getString(R.string.character_detail_gender)).thenReturn("Género")
            whenever(getString(R.string.character_detail_type)).thenReturn("Tipo")
            whenever(getString(R.string.character_detail_origin)).thenReturn("Origen")
            whenever(getString(R.string.character_detail_location)).thenReturn("Ubicación")
            whenever(getString(R.string.character_detail_episodes)).thenReturn("Episodios")

            whenever(getString(R.string.character_detail_unknown)).thenReturn("Desconocido")

            whenever(getString(R.string.character_status_alive)).thenReturn("Vivo")
            whenever(getString(R.string.character_status_dead)).thenReturn("Muerto")
            whenever(getString(R.string.character_status_unknown)).thenReturn("Desconocido")

            whenever(getString(R.string.character_gender_male)).thenReturn("Hombre")
            whenever(getString(R.string.character_gender_female)).thenReturn("Mujer")
            whenever(getString(R.string.character_gender_genderless)).thenReturn("Sin género")
            whenever(getString(R.string.character_gender_unknown)).thenReturn("Desconocido")
        }

    }

    @Test
    fun `map with alive human full data builds proper UiModel`() {
        val domain = CharacterModelMother.mockRick()

        val ui = mapper.getUiModel(domain)

        assertEquals("Rick", ui.name)
        assertEquals("https://example.com/rick.png", ui.imageUrl)
        assertEquals("Imagen de Rick", ui.imageContentDescription)

        assertEquals("Vivo", ui.statusChip.text)
        assertEquals(ChipGreen, ui.statusChip.containerColor)
        assertEquals(onFor(ChipGreen), ui.statusChip.labelColor)

        assertEquals("Human", ui.speciesChip.text)
        assertEquals(ChipBlue, ui.speciesChip.containerColor)
        assertEquals(onFor(ChipBlue), ui.speciesChip.labelColor)

        assertEquals(5, ui.infoItems.size)
        assertEquals("Género", ui.infoItems[0].label)
        assertEquals("Hombre", ui.infoItems[0].value)

        assertEquals("Tipo", ui.infoItems[1].label)
        assertEquals("Desconocido", ui.infoItems[1].value)

        assertEquals("Origen", ui.infoItems[2].label)
        assertEquals("Earth (C-137)", ui.infoItems[2].value)

        assertEquals("Ubicación", ui.infoItems[3].label)
        assertEquals("Earth (Replacement Dimension)", ui.infoItems[3].value)

        assertEquals("Episodios", ui.infoItems[4].label)
        assertEquals("3", ui.infoItems[4].value)
    }

    @Test
    fun `map with dead alien blanks uses unknown strings and colors`() {
        val domain = CharacterModelMother.mockMorty()

        val ui = mapper.getUiModel(domain)

        assertEquals("Imagen de Morty", ui.imageContentDescription)

        assertEquals("Muerto", ui.statusChip.text)
        assertEquals(ChipRed, ui.statusChip.containerColor)
        assertEquals(onFor(ChipRed), ui.statusChip.labelColor)

        assertEquals("Desconocido", ui.speciesChip.text)
        assertEquals(ChipGreen, ui.speciesChip.containerColor)
        assertEquals(onFor(ChipGreen), ui.speciesChip.labelColor)

        assertEquals("Desconocido", ui.infoItems[0].value)
        assertEquals("Desconocido", ui.infoItems[1].value)
        assertEquals("Desconocido", ui.infoItems[2].value)
        assertEquals("Desconocido", ui.infoItems[3].value)
        assertEquals("0", ui.infoItems[4].value)
    }

    @Test
    fun `map status unknown uses gray chip`() {
        val domain = CharacterModelMother.mockOther()

        val ui = mapper.getUiModel(domain)

        assertEquals("Desconocido", ui.statusChip.text)
        assertEquals(ChipGray, ui.statusChip.containerColor)
        assertEquals(onFor(ChipGray), ui.statusChip.labelColor)

        assertEquals("Robot", ui.speciesChip.text)
        assertEquals(ChipGray, ui.speciesChip.containerColor)
        assertEquals(onFor(ChipGray), ui.speciesChip.labelColor)
    }
}