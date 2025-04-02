// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var database: AppDatabase

    private val TAG = "LoginFragment" // Tag for logging

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_form, container, false)

        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.loginButton)

        database = AppDatabase.getDatabase(requireContext())

        loginButton.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            validateAndLogin() // this is commemeted just to bypass login for testing purupose
//            redirectToMainActivity()

        }

        return view
    }

    private fun validateAndLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "Validation failed: Email or password is empty")
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to login with email: $email")

        // Check credentials in the database
        lifecycleScope.launch {
            val customer = database.customerDao().getCustomerByUsername(email)
            if (customer != null) {
                Log.d(TAG, "Customer found: ${customer.userName}")
                if (customer.password == password) {
                    // Successful login
                    Log.d(TAG, "Login successful for user: ${customer.userName}")
                    saveUserToPreferences(customer)
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                    redirectToMainActivity()
                } else {
                    // Invalid password
                    Log.w(TAG, "Invalid password for user: $email")
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Customer not found
                Log.w(TAG, "No customer found with email: $email")
                Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToPreferences(customer: Customer) {
        val sharedPreferences = requireActivity().getSharedPreferences("CenPhonePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userName", customer.userName)
        editor.putString("firstName", customer.firstname)
        editor.putString("lastName", customer.lastname)
        editor.apply()
        Log.d(TAG, "User data saved to preferences for user: ${customer.userName}")
    }

    private fun redirectToMainActivity() {
        Log.d(TAG, "Redirecting to MainActivity")
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close the login activity/fragment
    }
}
