// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429package com.example.cenphone

package com.example.cenphone
import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
