package com.mukund.cenphonenew.data.repository

import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.Customer
import com.mukund.cenphonenew.data.model.FirebaseCustomer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CustomerRepository {
    private val firebaseManager = FirebaseManager.getInstance()
    private val customersCollection = firebaseManager.customersCollection
    
    fun getAllCustomers(): Flow<List<Customer>> = flow {
        try {
            val querySnapshot = customersCollection.get().await()
            val customers = querySnapshot.documents.mapNotNull { document ->
                document.toObject(FirebaseCustomer::class.java)?.toCustomer()
            }
            emit(customers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    suspend fun getCustomerByUsername(userName: String): Customer? {
        return try {
            val querySnapshot = customersCollection
                .whereEqualTo("email", userName)
                .limit(1)
                .get()
                .await()
                
            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(FirebaseCustomer::class.java)?.toCustomer()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getCustomerById(customerId: Int): Customer? {
        return try {
            val querySnapshot = customersCollection
                .whereEqualTo("id", customerId.toString())
                .limit(1)
                .get()
                .await()
                
            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(FirebaseCustomer::class.java)?.toCustomer()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getCustomerByFirebaseUid(uid: String): Customer? {
        return try {
            android.util.Log.d("CustomerRepository", "Looking up customer by UID: $uid")
            
            // Try direct document lookup first
            val documentSnapshot = customersCollection.document(uid).get().await()
            android.util.Log.d("CustomerRepository", "Direct document lookup exists? ${documentSnapshot.exists()}")
            
            if (documentSnapshot.exists()) {
                val customer = documentSnapshot.toObject(FirebaseCustomer::class.java)?.toCustomer()
                android.util.Log.d("CustomerRepository", "Customer from direct lookup: ${customer?.userName ?: "null"}")
                customer
            } else {
                android.util.Log.d("CustomerRepository", "Direct lookup failed, trying query")
                
                // Fallback to query if direct lookup fails
                val querySnapshot = customersCollection
                    .whereEqualTo("id", uid)
                    .limit(1)
                    .get()
                    .await()
                
                android.util.Log.d("CustomerRepository", "Query results count: ${querySnapshot.documents.size}")
                    
                if (querySnapshot.documents.isNotEmpty()) {
                    val customer = querySnapshot.documents[0].toObject(FirebaseCustomer::class.java)?.toCustomer()
                    android.util.Log.d("CustomerRepository", "Customer from query: ${customer?.userName ?: "null"}")
                    customer
                } else {
                    // If all else fails, try getting all customers and logging them
                    android.util.Log.d("CustomerRepository", "Both lookups failed, checking all customers")
                    val allCustomers = customersCollection.get().await()
                    android.util.Log.d("CustomerRepository", "Total customers in collection: ${allCustomers.size()}")
                    
                    allCustomers.documents.forEach { doc ->
                        android.util.Log.d("CustomerRepository", "Customer document ID: ${doc.id}, data: ${doc.data}")
                    }
                    
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CustomerRepository", "Error getting customer by UID: ${e.message}", e)
            null
        }
    }
    
    suspend fun insertCustomer(customer: Customer, userId: String): String {
        return try {
            val firebaseCustomer = FirebaseCustomer.fromCustomer(customer, userId)
            // Use the userId as the document ID instead of auto-generating one
            customersCollection.document(userId).set(firebaseCustomer).await()
            userId
        } catch (e: Exception) {
            ""
        }
    }
    
    suspend fun updateCustomer(customer: Customer, userId: String): Boolean {
        return try {
            val firebaseCustomer = FirebaseCustomer.fromCustomer(customer, userId)
            customersCollection.document(userId).set(firebaseCustomer).await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 