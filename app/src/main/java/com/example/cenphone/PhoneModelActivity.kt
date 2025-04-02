package com.example.cenphone

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhoneModelActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneViewModel: PhoneViewModel
    private var basePrice = 0
    private var storagePrice = 0
    private var totalPrice = 0
    private lateinit var modelPriceTextView: TextView
    private lateinit var storageGroup: RadioGroup
    private lateinit var colorGroup: RadioGroup
    private var selectedModel: String? = null
    private var selectedColor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_model)

        // Initialize ViewModel
        val phoneDao = AppDatabase.getDatabase(this).phoneDao()
        val phoneRepository = PhoneRepository(phoneDao)
        phoneViewModel = ViewModelProvider(this).get(PhoneViewModel::class.java)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("CheckoutData", MODE_PRIVATE)

        val brandName = sharedPreferences.getString("brandName", "").toString()
        setupUI(brandName)
        loadPhoneModels(brandName)
    }

    private fun setupUI(brandName: String) {
        val modelTitle = findViewById<TextView>(R.id.modelName)
        findViewById<TextView>(R.id.modelTitle).text = brandName
        modelTitle.text = brandName

        val modelImage = findViewById<ImageView>(R.id.modelImage)
        val imageRes = when (brandName) {
            "iPhone" -> R.mipmap.iphonemodel_foreground
            "Samsung" -> R.mipmap.samsungmodel_foreground
            "Google Pixel" -> R.mipmap.googlemodel_foreground
            "Xiaomi" -> R.mipmap.xiaomi_model_foreground
            else -> R.mipmap.iphonemodel_foreground
        }
        modelImage.setImageResource(imageRes)

        storageGroup = findViewById(R.id.storageOptions)
        storageGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedStorage = findViewById<RadioButton>(checkedId)
            storagePrice = when (selectedStorage.text.toString()) {
                "64 GB" -> 0
                "128 GB" -> 100
                "256 GB" -> 200
                else -> 0
            }
            updateTotalPrice()
        }

        colorGroup = findViewById(R.id.colorOptionsContainer)
        colorGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedColor = findViewById<RadioButton>(checkedId).tag.toString()
        }
    }

    private fun loadPhoneModels(brandName: String) {
        phoneViewModel.getPhonesForBrand(brandName) { phoneData ->
            if (phoneData.isNotEmpty()) {
                basePrice = phoneData.first().price.toInt()
                totalPrice = basePrice
                updatePriceDisplay()

                val customAdapter = CustomAdapter(phoneData) { model, selectedPrice, phoneId ->
                    selectedModel = model
                    basePrice = selectedPrice
                    updateTotalPrice()

                    // Save selected phone in SharedPreferences
                    sharedPreferences.edit().apply {
                        putLong("selectedPhoneId", phoneId)
                        apply()
                    }
                }

                val recyclerView: RecyclerView = findViewById(R.id.modelRecyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = customAdapter
            } else {
                Toast.makeText(this, "No phones found for the selected brand.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalPrice() {
        totalPrice = basePrice + storagePrice
        updatePriceDisplay()
    }

    private fun updatePriceDisplay() {
        modelPriceTextView = findViewById(R.id.modelPrice)
        modelPriceTextView.text = "$$totalPrice"
    }

    fun BuyNowButton(view: View) {
        val selectedStorageId = storageGroup.checkedRadioButtonId
        val selectedColorId = colorGroup.checkedRadioButtonId

        if (selectedModel == null) {
            Toast.makeText(this, "Please select a model.", Toast.LENGTH_SHORT).show()
        } else if (selectedStorageId == -1) {
            Toast.makeText(this, "Please select a storage option.", Toast.LENGTH_SHORT).show()
        } else if (selectedColorId == -1) {
            Toast.makeText(this, "Please select a color.", Toast.LENGTH_SHORT).show()
        } else {
            val selectedStorage = findViewById<RadioButton>(selectedStorageId).text.toString()

            sharedPreferences.edit().apply {
                putString("selectedModel", selectedModel)
                putString("selectedStorage", selectedStorage)
                putString("selectedColor", selectedColor)
                putString("selectedPrice", totalPrice.toString())
                apply()
            }

            val intent = Intent(this, DeliveryType::class.java)
            startActivity(intent)
        }
    }
}
