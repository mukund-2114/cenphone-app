
// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrandsActivity : AppCompatActivity() {

    private lateinit var brandsRecyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneDao: PhoneDao
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brands)
        database = AppDatabase.getDatabase(this)
        // Initialize RecyclerView
        brandsRecyclerView = findViewById(R.id.brandsRecyclerView)
        brandsRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("CheckoutData", MODE_PRIVATE)
        val profileButton = findViewById<ImageView>(R.id.profileButton)


        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        // Get PhoneDao from AppDatabase
        val database = AppDatabase.getDatabase(this)
        phoneDao = database.phoneDao()
//        populateDatabaseWithModels()
        // Fetch and display brands
        fetchAndDisplayBrands()
    }


    private fun fetchAndDisplayBrands() {
        lifecycleScope.launch {
            try {
                val brands = withContext(Dispatchers.IO) {
                    phoneDao.getUniqueBrands() // Fetch unique brands from the database
                }
                if (brands.isNotEmpty()) {
                    brandsRecyclerView.adapter = BrandsAdapter(brands) { selectedBrand ->
                        saveBrandToPreferencesAndNavigate(selectedBrand)
                    }
                } else {
                    Toast.makeText(this@BrandsActivity, "No brands available", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BrandsActivity, "Error fetching brands", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveBrandToPreferencesAndNavigate(brandName: String) {
        // Save the selected brand to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("brandName", brandName)
        editor.apply()

        // Navigate to PhoneModelActivity
        val intent = Intent(this, PhoneModelActivity::class.java)
        startActivity(intent)
    }

    private fun populateDatabaseWithModels() {
        lifecycleScope.launch {
//            database.phoneDao().deleteAllPhones()
//            val existingPhones = database.phoneDao().getAllPhones()

//            if (existingPhones.isEmpty()) {
                val phonesToInsert = listOf(
                    // iPhone Models
                    Phone(phoneMake = "iPhone", phoneModel = "iPhone 16", phoneColor = "", storageCapacity = "64GB", price = 1500.0),
                    Phone(phoneMake = "iPhone", phoneModel = "iPhone 15", phoneColor = "", storageCapacity = "128GB", price = 1700.0),
                    Phone(phoneMake = "iPhone", phoneModel = "iPhone 14", phoneColor = "", storageCapacity = "256GB", price = 1900.0),

                    // Samsung Models
                    Phone(phoneMake = "Samsung", phoneModel = "Galaxy S23", phoneColor = "", storageCapacity = "64GB", price = 1200.0),
                    Phone(phoneMake = "Samsung", phoneModel = "Galaxy S22", phoneColor = "", storageCapacity = "128GB", price = 1400.0),
                    Phone(phoneMake = "Samsung", phoneModel = "Galaxy Note 20", phoneColor = "", storageCapacity = "256GB", price = 1600.0),

                    // Google Pixel Models
                    Phone(phoneMake = "Google Pixel", phoneModel = "Pixel 8", phoneColor = "", storageCapacity = "64GB", price = 899.0),
                    Phone(phoneMake = "Google Pixel", phoneModel = "Pixel 7", phoneColor = "", storageCapacity = "128GB", price = 999.0),
                    Phone(phoneMake = "Google Pixel", phoneModel = "Pixel 6", phoneColor = "", storageCapacity = "256GB", price = 1099.0),

                    // Xiaomi Models
                    Phone(phoneMake = "Xiaomi", phoneModel = "Xiaomi 12 Pro", phoneColor = "", storageCapacity = "64GB", price = 699.0),
                    Phone(phoneMake = "Xiaomi", phoneModel = "Xiaomi 11 Ultra", phoneColor = "", storageCapacity = "128GB", price = 799.0),
                    Phone(phoneMake = "Xiaomi", phoneModel = "Xiaomi 11X", phoneColor = "", storageCapacity = "256GB", price = 899.0)
                )

                database.phoneDao().apply {
                    phonesToInsert.forEach { phone -> insert(phone) }
                }

                Log.d("PhoneModelActivity", "Database populated with phone models.")
//            }
        }
    }
}
