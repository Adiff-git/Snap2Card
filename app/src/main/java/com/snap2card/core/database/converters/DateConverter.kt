package com.snap2card.core.database.converters

import androidx.room.TypeConverter

/** Room TypeConverter for Long timestamps ↔ Long (no-op, but documents intent). */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Long? = value

    @TypeConverter
    fun toTimestamp(value: Long?): Long? = value
}
