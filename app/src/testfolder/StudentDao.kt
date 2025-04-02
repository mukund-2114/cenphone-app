package com.vinay.roommvvmapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StudentDao {
    //defining an insert method using @Insert Annotation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(studentModel: StudentModel)

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM student WHERE StudentName =:studentname")
    fun getStudents(studentname: String?) : LiveData<StudentModel>

}