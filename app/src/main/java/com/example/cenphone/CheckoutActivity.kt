// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429

package com.example.cenphone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    private lateinit var currentUser: Customer // Assuming you have a Customer data class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)

        // Handle window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageView>(R.id.backButton)

        // Set an OnClickListener to navigate back
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Load user data
        loadUserData()

        // Set up the country spinner
        val spinnerCountry: Spinner = findViewById(R.id.spinnerCountry)
        val countries = resources.getStringArray(R.array.countries_array) // Fetch countries from resources
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = countryAdapter

        // Set up the province spinner (for Canada)
        val spinnerProvince: Spinner = findViewById(R.id.spinnerProvince)
        val province = resources.getStringArray(R.array.canada_provinces_array) // Fetch provinces from resources
        val provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, province)
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProvince.adapter = provinceAdapter

        // Set up the city spinner (for Canada)
        val spinnerCity: Spinner = findViewById(R.id.spinnerCity)
        val cities = resources.getStringArray(R.array.canada_cities_array) // Fetch cities from resources
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = cityAdapter

        // Add listener to country spinner to handle province and city visibility
        spinnerCountry.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (countries[position] == "Canada") {
                    spinnerProvince.visibility = View.VISIBLE
                    spinnerCity.visibility = View.VISIBLE
                } else {
                    spinnerProvince.visibility = View.GONE
                    spinnerCity.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences("CenPhonePrefs", Context.MODE_PRIVATE)
            val userName = sharedPreferences.getString("userName", null)

            if (userName != null) {
                // Assuming you have a method to get the user from the database
                currentUser = AppDatabase.getDatabase(this@CheckoutActivity).customerDao().getCustomerByUsername(userName) ?: return@launch

                // Set the user data in the EditText fields
                findViewById<EditText>(R.id.editTextFirstName).setText(currentUser.firstname)
                findViewById<EditText>(R.id.editTextLastName).setText(currentUser.lastname)
                findViewById<EditText>(R.id.editTextEmail).setText(currentUser.userName)
                // You can also set the phone number if available
//                findViewById<EditText>(R.id.editTextPhone).setText(currentUser.phoneNumber) // Assuming you have this field
                findViewById<EditText>(R.id.editTextAddress).setText(currentUser.address)
                findViewById<EditText>(R.id.editTextZipCode).setText(currentUser.postalCode)
//                findViewById<EditText>(R.id.editTextHouseNo).setText(currentUser.houseNo) // Assuming you have this field
//                findViewById<EditText>(R.id.editTextLandmark).setText(currentUser.landmark) // Assuming you have this field
                // Set the country, province, and city if available
                // You may need to set the spinner selections based on the user data
            }
        }
    }

    fun makePaymentButton(view: View) {
        // Validate input fields
        val firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
        val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val mobileNumber = findViewById<EditText>(R.id.editTextPhone).text.toString()
        val addressLine = findViewById<EditText>(R.id.editTextAddress).text.toString()
        val houseNo = findViewById<EditText>(R.id.editTextHouseNo).text.toString()
        val landmark = findViewById<EditText>(R.id.editTextLandmark).text.toString()
        val zipCode = findViewById<EditText>(R.id.editTextZipCode).text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || mobileNumber.isEmpty() ||
            addressLine.isEmpty() || houseNo.isEmpty() || landmark.isEmpty() || zipCode.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        if (mobileNumber.length != 10) {
            Toast.makeText(this, "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect data from spinners
        val country = findViewById<Spinner>(R.id.spinnerCountry).selectedItem.toString()
        if (country == "Select Country") {
            Toast.makeText(this, "Please select a country.", Toast.LENGTH_SHORT).show()
            return
        }

        val province = findViewById<Spinner>(R.id.spinnerProvince).selectedItem.toString()
        if (province == "Select Province") {
            Toast.makeText(this, "Please select a province.", Toast.LENGTH_SHORT).show()
            return
        }

        val city = findViewById<Spinner>(R.id.spinnerCity).selectedItem.toString()
        if (city == "Select City") {
            Toast.makeText(this, "Please select a city.", Toast.LENGTH_SHORT).show()
            return
        }

        // Save data to SharedPreferences
        val sharedPreferences = getSharedPreferences("CheckoutData", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putString("fullName", "$firstName $lastName")
        editor.putString("address", addressLine)
        editor.putString("country", country)
        editor.putString("province", province)
        editor.putString("city", city)
        editor.putString("zipCode", zipCode)

        editor.apply()

        // Send data to the next activity
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
        finish() // Close CheckoutActivity after proceeding to PaymentActivity
    }
}