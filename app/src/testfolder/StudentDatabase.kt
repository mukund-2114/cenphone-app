package com.vinay.roommvvmapp
/*
  MAPD 711- Samsung Android App Development
  Week 8 lab - Room Database
  Professor: Vinay
 */

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
  MAPD 711- Samsung Android App Development
  Week 8 lab - Room Database
  Professor: Vinay
 */
@Database(entities = arrayOf(StudentModel::class), version = 1, exportSchema = false)
abstract class StudentDatabase: RoomDatabase() {

    //instantiating Student DAO object
    abstract fun studentDao() : StudentDao

    //companion object means an object declaration inside a class
    companion object {

        //Volatile object or property is immediately made visible to other threads.
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        //create a database name "STUDENTDB"
        fun getDataseClient(context: Context) : StudentDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, StudentDatabase::class.java, "STUDENTDB")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }

}