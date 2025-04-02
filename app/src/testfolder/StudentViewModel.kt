package com.vinay.roommvvmapp

/*
  MAPD 711- Samsung Android App Development
  Week 8 lab - Room Database
  Professor: Vinay
 */

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class StudentViewModel: ViewModel(){
    // calling repository tasks and
    // sending the results to the Activity
    var liveDataStudent: LiveData<StudentModel>? = null

    //
    fun insertStudent(context: Context, studentname: String, course: String) {
        StudentRepository.insertStudent(context, studentname, course)
    }

    fun getStudents(context: Context, studentname: String) : LiveData<StudentModel>? {
        liveDataStudent = StudentRepository.getStudents(context, studentname)
        return liveDataStudent
    }
}