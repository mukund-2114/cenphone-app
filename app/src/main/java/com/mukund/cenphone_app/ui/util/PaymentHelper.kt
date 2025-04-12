package com.mukund.cenphonenew.ui.util

import android.util.Log
import androidx.activity.ComponentActivity
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.mukund.cenphonenew.ui.util.ToastManager
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * Helper class to handle Stripe payment sheet initialization and presentation
 * outside of Compose lifecycle
 */
class PaymentHelper private constructor(activity: ComponentActivity) {
    
    // Keep a weak reference to the activity to avoid memory leaks
    private val activityRef = WeakReference(activity)
    
    // Initialize payment sheet early in the lifecycle
    private val paymentSheet = PaymentSheet(
        activity,
        ::handlePaymentResult
    )
    
    // Payment state
    private var paymentIntentClientSecret: String? = null
    private var customerConfig: PaymentSheet.CustomerConfiguration? = null
    
    // Callback to be set by the UI when needed
    var onPaymentCompleted: (() -> Unit)? = null
    var onPaymentCancelled: (() -> Unit)? = null
    var onPaymentFailed: ((String) -> Unit)? = null
    var onLoadingStateChanged: ((Boolean) -> Unit)? = null
    
    init {
        Log.d("PaymentHelper", "Initialized with activity: ${activity.javaClass.simpleName}")
    }
    
    companion object {
        private var instance: PaymentHelper? = null
        
        /**
         * Get or create a PaymentHelper instance
         * This should be called early in the lifecycle (during onCreate)
         */
        fun getInstance(activity: ComponentActivity): PaymentHelper {
            if (instance?.activityRef?.get() !== activity) {
                // Create new instance if activity changed or instance is null
                instance = PaymentHelper(activity)
            }
            return instance!!
        }
    }
    
    fun fetchPaymentIntent(amount: Double) {
        onLoadingStateChanged?.invoke(true)
        
        val backendUrl = "https://stripe-server-pkxv.onrender.com/payment-sheet"
        val totalAmountInCents = (amount * 100).toInt()
        
        val jsonBody = JSONObject().apply {
            put("amount", totalAmountInCents)
        }
        
        backendUrl.httpPost()
            .body(jsonBody.toString())
            .header("Content-Type", "application/json")
            .responseJson { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        try {
                            val responseJson = result.get().obj()
                            paymentIntentClientSecret = responseJson.getString("paymentIntent")
                            
                            val customerId = responseJson.getString("customer")
                            val ephemeralKey = responseJson.getString("ephemeralKey")
                            customerConfig = PaymentSheet.CustomerConfiguration(customerId, ephemeralKey)
                            
                            val publishableKey = responseJson.getString("publishableKey")
                            activityRef.get()?.let { activity ->
                                PaymentConfiguration.init(activity, publishableKey)
                            }
                            
                            Log.d("PaymentHelper", "Payment setup complete")
                            onLoadingStateChanged?.invoke(false)
                        } catch (e: Exception) {
                            Log.e("PaymentHelper", "Error parsing response: ${e.message}")
                            onPaymentFailed?.invoke("Error setting up payment: ${e.message}")
                            onLoadingStateChanged?.invoke(false)
                        }
                    }
                    is Result.Failure -> {
                        Log.e("PaymentHelper", "Error fetching payment intent: ${result.error}")
                        onPaymentFailed?.invoke("Network error: ${result.error.message}")
                        onLoadingStateChanged?.invoke(false)
                    }
                }
            }
    }
    
    fun presentPaymentSheet() {
        val intent = paymentIntentClientSecret
        val config = customerConfig
        
        if (intent != null && config != null) {
            onLoadingStateChanged?.invoke(true)
            paymentSheet.presentWithPaymentIntent(
                intent,
                PaymentSheet.Configuration(
                    merchantDisplayName = "CenPhone Store",
                    customer = config,
                    // Temporarily disable Google Pay until proper setup is complete
                    // googlePay = createGooglePayConfig(),
                    allowsDelayedPaymentMethods = true
                )
            )
        } else {
            onPaymentFailed?.invoke("Payment setup is incomplete. Please try again.")
        }
    }
    
    /**
     * Creates Google Pay configuration with proper error handling
     * Returns null if the device/environment doesn't support Google Pay
     */
    private fun createGooglePayConfig(): PaymentSheet.GooglePayConfiguration? {
        return try {
            PaymentSheet.GooglePayConfiguration(
                environment = PaymentSheet.GooglePayConfiguration.Environment.Test, // Use .Production for live environment
                countryCode = "CA", // Canada country code
                currencyCode = "CAD" // Canadian Dollar
            )
        } catch (e: Exception) {
            // Log error but don't crash if Google Pay configuration fails
            Log.e("PaymentHelper", "Error creating Google Pay configuration: ${e.message}")
            null // Return null to gracefully fall back to regular payment methods
        }
    }
    
    fun isPaymentReady(): Boolean {
        return paymentIntentClientSecret != null && customerConfig != null
    }
    
    private fun handlePaymentResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("PaymentHelper", "Payment canceled")
                ToastManager.showInfoToast("Payment canceled")
                onPaymentCancelled?.invoke()
                onLoadingStateChanged?.invoke(false)
            }
            is PaymentSheetResult.Failed -> {
                Log.e("PaymentHelper", "Payment failed: ${paymentSheetResult.error.message}")
                ToastManager.showErrorToast("Payment failed: ${paymentSheetResult.error.message}")
                onPaymentFailed?.invoke(paymentSheetResult.error.message ?: "Unknown error")
                onLoadingStateChanged?.invoke(false)
            }
            is PaymentSheetResult.Completed -> {
                Log.d("PaymentHelper", "Payment successful")
                ToastManager.showSuccessToast("Payment successful!")
                onPaymentCompleted?.invoke()
                onLoadingStateChanged?.invoke(false)
            }
        }
    }
    
    fun cleanup() {
        // Clear callbacks to prevent memory leaks
        onPaymentCompleted = null
        onPaymentCancelled = null
        onPaymentFailed = null
        onLoadingStateChanged = null
    }
} 