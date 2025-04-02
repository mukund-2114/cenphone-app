
// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context


@Database(entities = [Customer::class, Phone::class, Order::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun phoneDao(): PhoneDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null   

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cenphone_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
