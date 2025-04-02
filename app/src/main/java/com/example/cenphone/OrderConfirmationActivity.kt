package com.example.cenphone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var orderViewModel: OrderViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        // Initialize ViewModel
        val orderDao = AppDatabase.getDatabase(this).orderDao()
        val orderRepository = OrderRepository(orderDao)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        setupUI()
        saveOrderToDatabase()
    }

    private fun setupUI() {
        val sharedPreferences = getSharedPreferences("CheckoutData", Context.MODE_PRIVATE)

        // Retrieve data from SharedPreferences
        val itemName = findViewById<TextView>(R.id.itemName)
        val itemPrice = findViewById<TextView>(R.id.itemPrice)
        val itemStorage = findViewById<TextView>(R.id.itemStorage)
        val orderTotal = findViewById<TextView>(R.id.orderTotal)
        val items = findViewById<TextView>(R.id.items)
        val pickupSection = findViewById<LinearLayout>(R.id.pickupSection)
        val pickup = findViewById<TextView>(R.id.pickup)
        val location = sharedPreferences.getString("storeLocation", "")

        itemName.text = sharedPreferences.getString("selectedModel", "N/A")
        itemPrice.text = "Price: $" + sharedPreferences.getString("selectedPrice", "0")
        itemStorage.text = "Storage: " + sharedPreferences.getString("selectedStorage", "N/A")
        orderTotal.text = "$" + sharedPreferences.getString("total", "0")
        items.text = "Items (" + sharedPreferences.getString("quantity", "") + ")"

        if (location != "") {
            pickupSection.visibility = View.VISIBLE
            pickup.text = "Pickup Location: $location"
        }

        val getDirectionButton = findViewById<Button>(R.id.getDirectionButton)
        getDirectionButton.setOnClickListener {
            val uri = Uri.parse("geo:0,0?q=$location")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Unable to find a map app.", Toast.LENGTH_SHORT).show()
            }
        }

        val proceedToCheckoutButton = findViewById<Button>(R.id.proceedToCheckoutButton)
        proceedToCheckoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setModelImage(sharedPreferences)
        setShippingInformation(sharedPreferences)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveOrderToDatabase() {
        val sharedPreferences = getSharedPreferences("CenPhonePrefs", Context.MODE_PRIVATE)
        val checkoutPrefs = getSharedPreferences("CheckoutData", Context.MODE_PRIVATE)

        val userName = sharedPreferences.getString("userName", null)
        val totalAmount = checkoutPrefs.getString("total", "0")?.toDoubleOrNull() ?: 0.0
        val selectedModel = checkoutPrefs.getString("selectedModel", null)
        val brandName = checkoutPrefs.getString("brandName", null)

        if (userName != null && selectedModel != null && brandName != null) {
            lifecycleScope.launch {
                try {
                    val customer = AppDatabase.getDatabase(applicationContext)
                        .customerDao().getCustomerByUsername(userName)
                    val phone = AppDatabase.getDatabase(applicationContext)
                        .phoneDao().getPhoneByModelAndBrand(selectedModel, brandName)

                    if (customer != null && phone != null) {
                        val order = Order(
                            custId = customer.custId,
                            productId = phone.productId.toLong(),
                            orderDate = Date(),
                            totalAmount = totalAmount,
                            status = "Confirmed"
                        )

                        orderViewModel.insertOrder(order, {
                            Toast.makeText(
                                this@OrderConfirmationActivity,
                                "Order placed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            checkoutPrefs.edit().clear().apply()
                        }, { e ->
                            Log.e("OrderConfirmation", "Error saving order", e)
                            Toast.makeText(
                                this@OrderConfirmationActivity,
                                "Error saving order: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    } else {
                        Toast.makeText(
                            this@OrderConfirmationActivity,
                            "Error: Customer or phone details not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("OrderConfirmation", "Error while saving order", e)
                    Toast.makeText(
                        this@OrderConfirmationActivity,
                        "An error occurred: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Error: Missing user or order information",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setModelImage(sharedPreferences: SharedPreferences) {
        val modelImage = findViewById<ImageView>(R.id.orderImage)
        val imageRes = when (sharedPreferences.getString("brandName", "")) {
            "iPhone" -> R.mipmap.iphonemodel_foreground
            "Samsung" -> R.mipmap.samsungmodel_foreground
            "Google Pixel" -> R.mipmap.googlemodel_foreground
            "Black Berry" -> R.mipmap.blackberrymodel_foreground
            else -> R.mipmap.iphonemodel_foreground // Default image
        }
        modelImage.setImageResource(imageRes)
    }

    private fun setShippingInformation(sharedPreferences: SharedPreferences) {
        val shippingName = findViewById<TextView>(R.id.shippingName)
        val shippingAddress = findViewById<TextView>(R.id.shippingAddress)
        val shippingCountry = findViewById<TextView>(R.id.shippingCountry)
        val shippingCity = findViewById<TextView>(R.id.shippingCity)
        val shippingProvince = findViewById<TextView>(R.id.shippingProvince)
        val shippingPostalCode = findViewById<TextView>(R.id.shippingPincode)
        val finalTotal = findViewById<TextView>(R.id.finalTotal)

        shippingName.text = sharedPreferences.getString("fullName", "N/A")
        shippingAddress.text = sharedPreferences.getString("address", "N/A")
        shippingCountry.text = sharedPreferences.getString("country", "N/A")
        shippingCity.text = sharedPreferences.getString("city", "N/A")
        shippingProvince.text = sharedPreferences.getString("province", "N/A")
        shippingPostalCode.text = sharedPreferences.getString("zipCode", "N/A")
        finalTotal.text = "$" + sharedPreferences.getString("total", "0")
    }
}
