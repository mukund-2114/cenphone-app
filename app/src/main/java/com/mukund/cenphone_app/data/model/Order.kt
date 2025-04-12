package com.mukund.cenphonenew.data.model

import java.util.Date

data class Order(
    val orderId: Int = 0,
    val custId: Int,
    val productId: Long,
    val orderDate: Date,
    val totalAmount: Double,
    val status: String
) 