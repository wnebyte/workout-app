package com.github.wnebyte.workoutapp.database

import androidx.room.TypeConverter
import java.util.*

class TypeConverter {

    @TypeConverter
    fun toUUID(uuid: String?) : UUID? =
        if (uuid != null) {
            UUID.fromString(uuid)
        } else {
            null
        }

    @TypeConverter
    fun fromUUID(uuid: UUID?) : String? =
        uuid?.toString()

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? =
        millisSinceEpoch?.let{
            Date(it)
        }

    @TypeConverter
    fun fromDate(date: Date?) : Long? =
            date?.time
}