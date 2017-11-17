package name.kropp.android.snooker.api

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?) = value?.let { Date(it) }

        @TypeConverter
        @JvmStatic
        fun dateToTimestamp(date: Date?) = date?.time
    }
}