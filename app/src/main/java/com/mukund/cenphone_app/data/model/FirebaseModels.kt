package com.mukund.cenphonenew.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore Phone model
 */
data class FirebasePhone(
    @DocumentId
    val id: String = "",
    val phoneMake: String = "",
    val phoneModel: String = "",
    val phoneColor: String = "",
    val storageCapacity: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "" // Added for storing image references in Firebase Storage
) {
    // Convert to domain model
    fun toPhone(): Phone {
        return Phone(
            productId = id.hashCode().toLong(),
            phoneMake = phoneMake,
            phoneModel = phoneModel,
            phoneColor = phoneColor,
            storageCapacity = storageCapacity,
            price = price
        )
    }
    
    companion object {
        // Convert from domain model
        fun fromPhone(phone: Phone): FirebasePhone {
            return FirebasePhone(
                id = if (phone.productId > 0) phone.productId.toString() else "",
                phoneMake = phone.phoneMake,
                phoneModel = phone.phoneModel,
                phoneColor = phone.phoneColor,
                storageCapacity = phone.storageCapacity,
                price = phone.price
            )
        }
    }
}

/**
 * Firestore Customer model
 */
data class FirebaseCustomer(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val phoneNumber: String = ""
) {
    // Convert to domain model
    fun toCustomer(): Customer {
        return Customer(
            custId = id.hashCode(),
            userName = email,
            password = "", // Password not stored in Firestore
            firstname = firstname,
            lastname = lastname,
            address = address,
            city = city,
            postalCode = postalCode
        )
    }
    
    companion object {
        // Convert from domain model and Firebase user
        fun fromCustomer(customer: Customer, userId: String): FirebaseCustomer {
            return FirebaseCustomer(
                id = userId,
                email = customer.userName,
                firstname = customer.firstname,
                lastname = customer.lastname,
                address = customer.address,
                city = customer.city,
                postalCode = customer.postalCode
            )
        }
    }
}

/**
 * Firestore Order model
 */
data class FirebaseOrder(
    @DocumentId
    val id: String = "",
    val customerId: String = "",
    val phoneIds: List<String> = listOf(),
    @ServerTimestamp
    val orderDate: Timestamp? = null,
    val totalAmount: Double = 0.0,
    val status: String = "Pending",
    val shippingAddress: String = "",
    val paymentMethod: String = ""
) {
    // Convert to domain model
    fun toOrder(): Order {
        return Order(
            orderId = id.hashCode(),
            custId = customerId.hashCode(),
            productId = if (phoneIds.isNotEmpty()) phoneIds[0].hashCode().toLong() else 0L,
            orderDate = orderDate?.toDate() ?: java.util.Date(),
            totalAmount = totalAmount,
            status = status
        )
    }
    
    companion object {
        // Convert from domain model
        fun fromOrder(order: Order, customerId: String, phoneIds: List<String>): FirebaseOrder {
            return FirebaseOrder(
                id = if (order.orderId > 0) order.orderId.toString() else "",
                customerId = customerId,
                phoneIds = phoneIds,
                totalAmount = order.totalAmount,
                status = order.status
            )
        }
    }
} 