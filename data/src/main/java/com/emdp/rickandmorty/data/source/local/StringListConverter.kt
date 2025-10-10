package com.emdp.rickandmorty.data.source.local

import androidx.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")?.filter { it.isNotBlank() }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}