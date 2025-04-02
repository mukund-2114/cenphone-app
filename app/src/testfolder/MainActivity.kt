package com.vinay.roommvvmapp
/*
  MAPD 711- Samsung Android App Development
  Week 8 lab - Room Database
  Professor: Vinay
 */

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    lateinit var studentViewModel: StudentViewModel
    lateinit var context: Context
    lateinit var strName: String
    lateinit var strCourse: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //consider the Main Activity as a current context
        context = this@MainActivity

        //initializing studentModel Object
        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        //defining reference objects for the UI controls
        var btnInsert: Button =findViewById(R.id.btnInsert)
        var btnShow: Button =findViewById(R.id.btnShow)
        var editName: EditText =findViewById(R.id.editName)
        var editCourse: EditText =findViewById(R.id.editCourse)
        var tvOutputName: TextView =findViewById(R.id.tvOutputName)
        var tvOutputCourse: TextView =findViewById(R.id.tvOutputCourse)
        var lstView: ListView =findViewById(R.id.lstView)


        // event handler for the Insert Button
        btnInsert.setOnClickListener {

            strName = editName.text.toString().trim()
            strCourse = editCourse.text.toString().trim()

            //validation for the empty values
            if (strName.isEmpty()) {
                editName.error = "Enter Student Name"
                Toast.makeText( context,"Student Name should not be empty",Toast.LENGTH_LONG).show()
            }
            else if (strCourse.isEmpty()) {
                editCourse.error = "Enter Course Name"
            }
            else {
                studentViewModel.insertStudent(context, strName, strCourse)
                Toast.makeText( context,"Data inserted successfully",Toast.LENGTH_LONG).show()
            }
        }

        //Event handler for the Show Button
        btnShow.setOnClickListener {

            strName = editName.text.toString().trim()

            //observer and observe() used to work with live-data
            studentViewModel.getStudents(context, strName)!!.observe(this, Observer {

                if (it == null) {
                    tvOutputName.text = " "
                    tvOutputCourse.text = " "
                    //Toast.makeText( context,"No Data",Toast.LENGTH_LONG).show()
                }
                else {
                    tvOutputName.text = it.StudentName
                    tvOutputCourse.text = it.Course

                }
            })
        }
    }
}