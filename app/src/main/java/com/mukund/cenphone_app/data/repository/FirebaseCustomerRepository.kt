package com.mukund.cenphonenew.data.repository

import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.Customer
import com.mukund.cenphonenew.data.model.FirebaseCustomer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Repository for customer data using Firebase Authentication and Firestore
 */
class FirebaseCustomerRepository {
    private val firebaseManager = FirebaseManager.getInstance()
    private val customersCollection = firebaseManager.customersCollection
    
    /**
     * Register a new customer with Firebase Authentication and store in Firestore
     */
    suspend fun registerCustomer(customer: Customer, password: String): Result<String> {
        return try {
            // First create the user in Firebase Auth
            val authResult = firebaseManager.signUp(customer.userName, password)
            
            authResult.fold(
                onSuccess = { user ->
                    // Then save additional user data in Firestore
                    val userId = user.uid
                    val firebaseCustomer = FirebaseCustomer.fromCustomer(customer, userId)
                    
                    customersCollection.document(userId).set(firebaseCustomer).await()
                    Result.success(userId)
                },
                onFailure = {
                    Result.failure(it)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign in a customer
     */
    suspend fun signInCustomer(email: String, password: String): Result<Customer> {
        return try {
            val authResult = firebaseManager.signIn(email, password)
            
            authResult.fold(
                onSuccess = { user ->
                    val userId = user.uid
                    val documentSnapshot = customersCollection.document(userId).get().await()
                    
                    documentSnapshot.toObject(FirebaseCustomer::class.java)?.let {
                        Result.success(it.toCustomer())
                    } ?: Result.failure(Exception("Customer data not found"))
                },
                onFailure = {
                    Result.failure(it)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get customer by user ID
     */
    suspend fun getCustomerById(userId: String): Customer? {
        return try {
            val documentSnapshot = customersCollection.document(userId).get().await()
            documentSnapshot.toObject(FirebaseCustomer::class.java)?.toCustomer()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get customer by email
     */
    suspend fun getCustomerByEmail(email: String): Customer? {
        return try {
            val querySnapshot = customersCollection.whereEqualTo("email", email).limit(1).get().await()
            
            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(FirebaseCustomer::class.java)?.toCustomer()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update customer information
     */
    suspend fun updateCustomer(customer: Customer): Boolean {
        val userId = firebaseManager.currentUser?.uid ?: return false
        
        return try {
            val firebaseCustomer = FirebaseCustomer.fromCustomer(customer, userId)
            customersCollection.document(userId).set(firebaseCustomer).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Sign out the current customer
     */
    fun signOut() {
        firebaseManager.signOut()
    }
    
    /**
     * Check if user is authenticated
     */
    fun isUserAuthenticated(): Boolean {
        return firebaseManager.currentUser != null
    }
    
    /**
     * Get ID of current user
     */
    fun getCurrentUserId(): String? {
        return firebaseManager.currentUser?.uid
    }
    
    /**
     * Get current customer data as flow
     */
    fun getCurrentCustomer(): Flow<Customer?> = flow {
        val userId = firebaseManager.currentUser?.uid
        if (userId != null) {
            try {
                val documentSnapshot = customersCollection.document(userId).get().await()
                val customer = documentSnapshot.toObject(FirebaseCustomer::class.java)?.toCustomer()
                emit(customer)
            } catch (e: Exception) {
                emit(null)
            }
        } else {
            emit(null)
        }
    }
} 