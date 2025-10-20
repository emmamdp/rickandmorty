package com.emdp.rickandmorty.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.CharactersTotalPagesDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharactersTotalPagesEntity

@Database(
    entities = [
        CharacterEntity::class,
        CharactersTotalPagesEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class RickAndMortyDatabase : RoomDatabase() {

    abstract fun charactersDao(): CharactersDao
    abstract fun charactersTotalPagesDao(): CharactersTotalPagesDao

    companion object {
        const val DATABASE_NAME: String = "rick_and_morty.db"
    }
}