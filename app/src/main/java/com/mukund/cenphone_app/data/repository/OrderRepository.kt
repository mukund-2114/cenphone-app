package com.mukund.cenphonenew.data.repository

import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.FirebaseOrder
import com.mukund.cenphonenew.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val firebaseManager = FirebaseManager.getInstance()
    private val ordersCollection = firebaseManager.ordersCollection
    
    suspend fun getOrdersByCustomer(customerId: Int): List<Order> {
        return try {
            android.util.Log.d("OrderRepository", "Getting orders by customer ID: $customerId")
            val querySnapshot = ordersCollection
                .whereEqualTo("customerId", customerId.toString())
                .get()
                .await()
                
            android.util.Log.d("OrderRepository", "Found ${querySnapshot.documents.size} orders by customer ID")
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(FirebaseOrder::class.java)?.toOrder()
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error getting orders by customer ID: ${e.message}", e)
            emptyList()
        }
    }
    
    suspend fun getOrderById(orderId: Int): Order? {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("orderId", orderId)
                .limit(1)
                .get()
                .await()
                
            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(FirebaseOrder::class.java)?.toOrder()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun insertOrder(order: Order, customerId: String, phoneIds: List<String>): String {
        return try {
            val firebaseOrder = FirebaseOrder.fromOrder(order, customerId, phoneIds)
            val documentRef = ordersCollection.add(firebaseOrder).await()
            documentRef.id
        } catch (e: Exception) {
            ""
        }
    }
    
    // New method to get orders by Firebase UID
    suspend fun getOrdersByFirebaseUid(uid: String): List<Order> {
        return try {
            android.util.Log.d("OrderRepository", "Getting orders by Firebase UID: $uid")
            val querySnapshot = ordersCollection
                .whereEqualTo("customerId", uid)
                .get()
                .await()
                
            android.util.Log.d("OrderRepository", "Found ${querySnapshot.documents.size} orders by Firebase UID")
            
            if (querySnapshot.documents.isEmpty()) {
                // If no orders found, log all orders for debugging
                val allOrders = ordersCollection.get().await()
                android.util.Log.d("OrderRepository", "Total orders in collection: ${allOrders.size()}")
                allOrders.documents.forEach { doc ->
                    android.util.Log.d("OrderRepository", "Order document ID: ${doc.id}, customerId: ${doc.getString("customerId")}")
                }
            }
            
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(FirebaseOrder::class.java)?.toOrder()
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error getting orders by Firebase UID: ${e.message}", e)
            emptyList()
        }
    }
} 