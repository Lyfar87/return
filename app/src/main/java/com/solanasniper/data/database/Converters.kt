package com.solanasniper.data.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromDexType(value: String?) = value?.let { enumValueOf<DexType>(it) }

    @TypeConverter
    fun dexTypeToString(type: DexType?) = type?.name
}

enum class DexType {
    RAYDIUM, JUPITER, ORCA
}