package com.mukund.cenphonenew.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/**
 * Manages Firebase services for the application
 */
class FirebaseManager private constructor() {
    
    // Firebase services
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    
    // Collections
    val customersCollection = firestore.collection("customers")
    val phonesCollection = firestore.collection("phones")
    val ordersCollection = firestore.collection("orders")
    
    // Current user
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    init {
        // Setup persistence (default is already PERSISTENCE_LOCAL but setting explicitly for clarity)
        try {
            android.util.Log.d("FirebaseManager", "Configuring Firebase Auth persistence")
            
            // Configure Firestore for offline persistence
            val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            firestore.firestoreSettings = settings
            
            android.util.Log.d("FirebaseManager", "Firebase configurations applied successfully")
        } catch (e: Exception) {
            android.util.Log.e("FirebaseManager", "Error configuring Firebase: ${e.message}", e)
        }
    }
    
    /**
     * Sign in with email and password
     * 
     * @param email User email
     * @param password User password
     * @return Result containing Firebase user or exception
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Authentication failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign up with email and password
     * 
     * @param email User email
     * @param password User password
     * @return Result containing Firebase user or exception
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("User creation failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: FirebaseManager? = null
        
        /**
         * Initialize Firebase in the application
         */
        fun initialize(context: Context) {
            FirebaseApp.initializeApp(context)
            
            // Log current auth state
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                android.util.Log.d(
                    "FirebaseManager", 
                    "Firebase initialized with logged in user: ${auth.currentUser?.uid}"
                )
            } else {
                android.util.Log.d("FirebaseManager", "Firebase initialized without logged in user")
            }
        }
        
        /**
         * Get the singleton instance
         */
        fun getInstance(): FirebaseManager {
            return INSTANCE ?: synchronized(this) {
                val instance = FirebaseManager()
                INSTANCE = instance
                instance
            }
        }
    }
} 