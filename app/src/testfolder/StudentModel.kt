package com.vinay.roommvvmapp
/*
  MAPD 711- Samsung Android App Development
  Week 8 lab - Room Database
  Professor: Vinay
 */

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class StudentModel(
    //defining a column StudentName
    @ColumnInfo(name = "studentname")
    var StudentName: String,
    //defining a column Course
    @ColumnInfo(name = "course")
    var Course: String

    )
    {
        //defining a primary key field Id
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var Id: Int? = null
    }


