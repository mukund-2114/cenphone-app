// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var firstNameTextView: EditText
    private lateinit var lastNameTextView: EditText
    private lateinit var emailTextView: EditText
    private lateinit var addressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var postalCodeEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var logoutButton: Button
    private lateinit var database: AppDatabase
    private lateinit var currentUser: Customer
    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        // Initialize views
        firstNameTextView = findViewById(R.id.firstNameTextView)
        lastNameTextView = findViewById(R.id.lastNameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        addressEditText = findViewById(R.id.addressEditText)
        cityEditText = findViewById(R.id.cityEditText)
        postalCodeEditText = findViewById(R.id.postalCodeEditText)
        updateButton = findViewById(R.id.updateButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Initialize database
        database = AppDatabase.getDatabase(this)

        val backButton = findViewById<ImageView>(R.id.backButton)

        // Set an OnClickListener to navigate back
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Get current user from SharedPreferences
        val sharedPreferences = getSharedPreferences("CenPhonePrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", null)
        
        // Load user data if userName exists
        userName?.let {
            customerViewModel.getCurrentUser(it)
        }

        // Observe current user
        customerViewModel.currentUser.observe(this) { customer ->
            customer?.let {
                updateUI(it)
            }
        }

        updateButton.setOnClickListener {
            updateUserData()
        }

        // Set up logout button click listener
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        customerViewModel.logout()
        // Clear user session data from SharedPreferences
        val sharedPreferences = getSharedPreferences("CenPhonePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // This will remove all data from SharedPreferences
        editor.apply()

        // Create an intent to start the RegisterActivity (which contains the login fragment)
        val intent = Intent(this, RegisterActivity::class.java)
        // Clear the activity stack so the user can't go back to the profile screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close the current activity

        // Show a toast message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun updateUserData() {
        customerViewModel.currentUser.value?.let { customer ->
            customer.address = addressEditText.text.toString().trim()
            customer.city = cityEditText.text.toString().trim()
            customer.postalCode = postalCodeEditText.text.toString().trim()
            
            customerViewModel.updateCustomer(customer)
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(customer: Customer) {
        firstNameTextView.setText(customer.firstname)
        lastNameTextView.setText(customer.lastname)
        emailTextView.setText(customer.userName)
        addressEditText.setText(customer.address)
        cityEditText.setText(customer.city)
        postalCodeEditText.setText(customer.postalCode)
    }
}