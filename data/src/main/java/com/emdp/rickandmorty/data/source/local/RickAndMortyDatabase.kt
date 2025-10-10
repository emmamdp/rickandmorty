package com.emdp.rickandmorty.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity

@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class RickAndMortyDatabase : RoomDatabase() {

    abstract fun charactersDao(): CharactersDao

    companion object {
        const val DATABASE_NAME: String = "rick_and_morty.db"
    }
}