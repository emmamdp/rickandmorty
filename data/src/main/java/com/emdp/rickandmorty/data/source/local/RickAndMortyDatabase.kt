package com.emdp.rickandmorty.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emdp.rickandmorty.data.source.local.dao.CharacterRemoteKeysDao
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterRemoteKeysEntity

@Database(
    entities = [
        CharacterEntity::class,
        CharacterRemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class RickAndMortyDatabase : RoomDatabase() {

    abstract fun charactersDao(): CharactersDao
    abstract fun remoteKeysDao(): CharacterRemoteKeysDao

    companion object {
        const val DATABASE_NAME: String = "rick_and_morty.db"
    }
}
