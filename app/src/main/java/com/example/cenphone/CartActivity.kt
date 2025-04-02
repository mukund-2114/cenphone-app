// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CartActivity : AppCompatActivity() {

    private lateinit var decrementButton: Button
    private lateinit var incrementButton: Button
    private lateinit var quantityTextView: TextView
    private lateinit var totalCostValue: TextView
    private lateinit var subtotalValue: TextView
    private var quantity = 1
    private var basePrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("CheckoutData", Context.MODE_PRIVATE)
        val brandName = sharedPreferences.getString("brandName", "")
        val selectedModel = sharedPreferences.getString("selectedModel", "")
        val selectedStorage = sharedPreferences.getString("selectedStorage", "")
        val selectedPrice = sharedPreferences.getString("selectedPrice", "")
        val selectedColor = sharedPreferences.getString("selectedColor", "")

        basePrice = selectedPrice?.toIntOrNull() ?: 0

        // Set data for UI elements
        val modelImage = findViewById<ImageView>(R.id.productImage)
        val imageRes = when (brandName) {
            "iPhone" -> R.mipmap.iphonemodel_foreground
            "Samsung" -> R.mipmap.samsungmodel_foreground
            "Google Pixel" -> R.mipmap.googlemodel_foreground
            "Black Berry" -> R.mipmap.blackberrymodel_foreground
            else -> R.mipmap.iphonemodel_foreground
        }
        modelImage.setImageResource(imageRes)

        val productName = findViewById<TextView>(R.id.productName)
        productName.text = selectedModel

        val productStorage = findViewById<TextView>(R.id.productStorage)
        productStorage.text = "$selectedStorage storage"

        val productPrice = findViewById<TextView>(R.id.productPrice)
        productPrice.text = "$ $selectedPrice"

        val productColor = findViewById<TextView>(R.id.productColor)
        productColor.text = selectedColor

        subtotalValue = findViewById(R.id.subtotalValue)
        totalCostValue = findViewById(R.id.totalCostValue)

        decrementButton = findViewById(R.id.decrementButton)
        incrementButton = findViewById(R.id.incrementButton)
        quantityTextView = findViewById(R.id.quantity)

        decrementButton.setOnClickListener { changeQuantity(-1) }
        incrementButton.setOnClickListener { changeQuantity(1) }

        updateQuantityAndCost()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun changeQuantity(change: Int) {
        quantity = (quantity + change).coerceIn(1, 99)
        updateQuantityAndCost()
    }

    private fun updateQuantityAndCost() {
        quantityTextView.text = quantity.toString()
        val subtotal = basePrice * quantity
        subtotalValue.text = "$$subtotal"
        val totalCost = subtotal + 3 + 5 // Adding shipping and tax
        totalCostValue.text = "$$totalCost"

        val sharedPreferences = getSharedPreferences("CheckoutData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("subtotal", subtotal.toString())
        editor.putString("total", totalCost.toString())
        editor.putString("quantity", quantity.toString())

        editor.apply()
    }

    fun processCheckoutButton(view: View) {
        println("Processing checkout...")
        val intent = Intent(this, CheckoutActivity::class.java)
        startActivity(intent)
    }
}