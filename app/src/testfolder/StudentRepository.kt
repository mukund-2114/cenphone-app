package com.vinay.roommvvmapp

/*
  MAPD 711- Samsung Android App Development
  Week 8 lab - Room Database
  Professor: Vinay
 */

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class StudentRepository {

    //defining database and live data object as companion objects
    companion object {
        var studentDatabase: StudentDatabase? = null
        var studentModel: LiveData<StudentModel>? = null

        //initialize database
        fun initializeDB(context: Context) : StudentDatabase {
            return StudentDatabase.getDataseClient(context)
        }

        //Initialize insertStudent()
        fun insertStudent(context: Context, studentname: String, course: String) {
            studentDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val studentDetails = StudentModel(studentname, course)
                studentDatabase!!.studentDao().insertStudent(studentDetails)
            }

        }

        //Initialize getStudents()
        fun getStudents(context: Context, studentname: String) : LiveData<StudentModel>? {

            studentDatabase = initializeDB(context)
            studentModel = studentDatabase!!.studentDao().getStudents(studentname)
            return studentModel
        }

    }
}