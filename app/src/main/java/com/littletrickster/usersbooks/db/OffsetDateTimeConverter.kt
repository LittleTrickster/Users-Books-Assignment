package com.littletrickster.usersbooks.db

import androidx.room.TypeConverter
import java.time.OffsetDateTime

class OffsetDateTimeConverter {
    @TypeConverter
    fun toString(value: OffsetDateTime?): String? = value?.toString()

    @TypeConverter
    fun fromString(value: String?): OffsetDateTime? =
        value?.let { OffsetDateTime.parse(it) }
}