// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_form, container, false)

        firstNameEditText = view.findViewById(R.id.firstName)
        lastNameEditText = view.findViewById(R.id.lastName)
        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        registerButton = view.findViewById(R.id.registerButton)

        database = AppDatabase.getDatabase(requireContext())

        registerButton.setOnClickListener {
            validateAndRegister()
        }

        return view
    }

    private fun validateAndRegister() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new Customer object
        val customer = Customer(
            custId = 0, // Auto-generated
            userName = email, // Assuming username is the email
            password = password,
            firstname = firstName,
            lastname = lastName,
            address = "", // Empty as not in layout
            city = "", // Empty as not in layout
            postalCode = "" // Empty as not in layout
        )

        // Insert into the database
        lifecycleScope.launch {
            database.customerDao().insertCustomer(customer)
            Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
            registerUser() // Assuming registration is successful, switch to the LoginFragment
            // Optionally navigate to another fragment or activity

        }
    }

    private fun registerUser() {
        // Your registration logic here
        // If registration is successful, switch to the LoginFragment

        (activity as RegisterActivity).replaceFragment(LoginFragment())

    }
}
