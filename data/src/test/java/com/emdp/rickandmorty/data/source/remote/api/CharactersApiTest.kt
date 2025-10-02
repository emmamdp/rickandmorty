package com.emdp.rickandmorty.data.source.remote.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.javaMethod

internal class CharactersApiTest {

    @Test
    fun `getCharacters must use GET character with expected query params`() {
        val method = CharactersApi::class.declaredFunctions
            .first { it.name == "getCharacters" }.javaMethod
        val get = method?.getAnnotation(GET::class.java)

        assertNotNull(get)
        assertEquals("character", get?.value)

        val params = method?.parameters
        val queryNames = params?.mapNotNull { p ->
            p.getAnnotation(Query::class.java)?.value
        }

        assertEquals(
            setOf("page", "name", "status", "species", "type", "gender"),
            queryNames?.toSet()
        )
    }

    @Test
    fun `getCharacterById must use GET character id path param`() {
        val method = CharactersApi::class.declaredFunctions
            .first { it.name == "getCharacterById" }.javaMethod
        val get = method?.getAnnotation(GET::class.java)

        assertNotNull(get)
        assertEquals("character/{id}", get?.value)

        val hasPathId = method?.parameters?.any { p ->
            p.getAnnotation(Path::class.java)?.value == "id"
        }
        assertTrue(hasPathId == true)
    }
}