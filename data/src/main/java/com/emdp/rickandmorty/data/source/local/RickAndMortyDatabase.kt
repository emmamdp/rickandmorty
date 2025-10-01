package com.emdp.rickandmorty.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.RemoteKeysDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntity

@Database(
    entities = [CharacterEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = true
)
abstract class RickAndMortyDatabase : RoomDatabase() {

    abstract fun charactersDao(): CharactersDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME: String = "rick_and_morty.db"
    }
}