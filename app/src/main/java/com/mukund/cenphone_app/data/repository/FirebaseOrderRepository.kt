package com.mukund.cenphonenew.data.repository

import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.FirebaseOrder
import com.mukund.cenphonenew.data.model.Order
import kotlinx.coroutines.tasks.await

/**
 * Repository for order data using Firebase Firestore
 */
class FirebaseOrderRepository {
    private val firebaseManager = FirebaseManager.getInstance()
    private val ordersCollection = firebaseManager.ordersCollection
    
    /**
     * Create a new order in Firestore
     * 
     * @param customerId Firebase user ID of the customer
     * @param phoneIds List of Firebase document IDs of ordered phones
     * @param totalAmount Total order amount
     * @param shippingAddress Shipping address for the order
     * @param paymentMethod Payment method used
     * @return Order ID if successful, empty string otherwise
     */
    suspend fun createOrder(
        customerId: String,
        phoneIds: List<String>,
        totalAmount: Double,
        shippingAddress: String,
        paymentMethod: String
    ): String {
        return try {
            val order = FirebaseOrder(
                customerId = customerId,
                phoneIds = phoneIds,
                totalAmount = totalAmount,
                shippingAddress = shippingAddress,
                paymentMethod = paymentMethod
            )
            
            val documentRef = ordersCollection.add(order).await()
            documentRef.id
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Get orders by customer ID
     * 
     * @param customerId Firebase user ID of the customer
     * @return List of orders for the customer
     */
    suspend fun getOrdersByCustomer(customerId: String): List<Order> {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("customerId", customerId)
                .get()
                .await()
                
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(FirebaseOrder::class.java)?.toOrder()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get order by ID
     * 
     * @param orderId Firestore document ID of the order
     * @return Order if found, null otherwise
     */
    suspend fun getOrderById(orderId: String): Order? {
        return try {
            val documentSnapshot = ordersCollection.document(orderId).get().await()
            documentSnapshot.toObject(FirebaseOrder::class.java)?.toOrder()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update order status
     * 
     * @param orderId Firestore document ID of the order
     * @param status New status for the order
     * @return true if successful, false otherwise
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            ordersCollection.document(orderId).update("status", status).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Delete an order
     * 
     * @param orderId Firestore document ID of the order
     * @return true if successful, false otherwise
     */
    suspend fun deleteOrder(orderId: String): Boolean {
        return try {
            ordersCollection.document(orderId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 