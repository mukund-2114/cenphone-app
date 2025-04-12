package com.mukund.cenphonenew.ui.model

/**
 * Class to store delivery information during checkout
 */
data class DeliveryInfo(
    val name: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val phoneNumber: String,
    val deliveryOption: DeliveryOption,
    val totalAmount: Double
)

/**
 * Enum class for delivery options
 */
enum class DeliveryOption(val displayName: String, val price: Double) {
    Standard("Standard Delivery (3-5 business days)", 5.99),
    Express("Express Delivery (1-2 business days)", 12.99),
    SameDay("Same-Day Delivery (where available)", 19.99)
} 