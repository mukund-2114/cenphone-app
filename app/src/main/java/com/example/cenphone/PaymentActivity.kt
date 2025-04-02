package com.example.cenphone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    private lateinit var paymentIntentClientSecret: String
    private var total = "0"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val sharedPreferences = getSharedPreferences("CheckoutData", Context.MODE_PRIVATE)

        val subtotal = sharedPreferences.getString("subtotal", "")
        total = sharedPreferences.getString("total", "").toString()

        val subtotalValue = findViewById<TextView>(R.id.checkoutsubtotal)
        subtotalValue.text = "$$subtotal"

        val totalValue = findViewById<TextView>(R.id.totalcost)
        totalValue.text = "$$total"

        // Initialize PaymentSheet
        paymentSheet = PaymentSheet(this) { paymentSheetResult ->
            handlePaymentResult(paymentSheetResult)
        }

        // Fetch Payment Intent and Customer Configuration
        fetchPaymentIntentAndConfig()

        // Set up "Make Payment" button
        val makePaymentButton = findViewById<Button>(R.id.makePaymentButton)
        makePaymentButton.setOnClickListener {
            presentPaymentSheet()
        }
    }

    private fun fetchPaymentIntentAndConfig() {
        val backendUrl = "https://stripe-server-pkxv.onrender.com/payment-sheet" // Replace with your actual backend URL

        val totalAmountInCents = (total.toDoubleOrNull() ?: 0.0) * 100 // Convert total to cents
        val jsonBody = JSONObject().apply {
            put("amount", totalAmountInCents.toInt()) // Ensure the amount is an integer
        }

        backendUrl.httpPost()
            .body(jsonBody.toString())
            .header("Content-Type", "application/json")
            .responseJson { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        try {
                            val responseJson = result.get().obj()
                            if (responseJson.has("paymentIntent") &&
                                responseJson.has("customer") &&
                                responseJson.has("ephemeralKey") &&
                                responseJson.has("publishableKey")
                            ) {
                                paymentIntentClientSecret = responseJson.getString("paymentIntent")
                                customerConfig = PaymentSheet.CustomerConfiguration(
                                    id = responseJson.getString("customer"),
                                    ephemeralKeySecret = responseJson.getString("ephemeralKey")
                                )
                                val publishableKey = responseJson.getString("publishableKey")
                                PaymentConfiguration.init(this, publishableKey)

                                Log.d("PaymentActivity", "Payment setup complete")
                                Log.d("PaymentActivity", "PaymentIntent: $paymentIntentClientSecret")
                                Log.d("PaymentActivity", "CustomerConfig: $customerConfig")
                                Log.d("PaymentActivity", "Publishable Key: $publishableKey")
                            } else {
                                throw Exception("Incomplete response: ${responseJson.toString()}")
                            }
                        } catch (e: Exception) {
                            Log.e("PaymentActivity", "Error parsing response: ${e.message}")
                            Toast.makeText(
                                this,
                                "Failed to set up payment. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is Result.Failure -> {
                        Log.e("PaymentActivity", "Error fetching payment intent: ${result.error}")
                        Toast.makeText(
                            this,
                            "Failed to set up payment. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun presentPaymentSheet() {
        if (!this::paymentIntentClientSecret.isInitialized || !this::customerConfig.isInitialized) {
            Log.e("PaymentActivity", "Payment setup is incomplete")
            Toast.makeText(
                this,
                "Payment setup is incomplete. Please try again later.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Your Merchant Name",
                customer = customerConfig,
                googlePay = PaymentSheet.GooglePayConfiguration(
                    environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
                    countryCode = "US"
                ),
                allowsDelayedPaymentMethods = true
            )
        )
    }

    private fun handlePaymentResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show()
                Log.d("PaymentActivity", "Payment canceled")
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(this, "Payment failed: ${paymentSheetResult.error.message}", Toast.LENGTH_SHORT).show()
                Log.e("PaymentActivity", "Payment failed: ${paymentSheetResult.error.message}")
            }
            is PaymentSheetResult.Completed -> {
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()
                Log.d("PaymentActivity", "Payment successful")

                val intent = Intent(this, OrderConfirmationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
