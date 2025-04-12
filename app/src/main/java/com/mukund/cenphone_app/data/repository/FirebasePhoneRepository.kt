package com.mukund.cenphonenew.data.repository

import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.FirebasePhone
import com.mukund.cenphonenew.data.model.Phone
import kotlinx.coroutines.tasks.await

/**
 * Repository for phone data using Firebase Firestore
 */
class FirebasePhoneRepository {
    private val firebaseManager = FirebaseManager.getInstance()
    private val phonesCollection = firebaseManager.phonesCollection
    
    /**
     * Get all phones from Firestore
     */
    suspend fun getAllPhones(): List<Phone> {
        return try {
            val querySnapshot = phonesCollection.get().await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(FirebasePhone::class.java)?.toPhone()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get unique phone brands (makes) from Firestore
     */
    suspend fun getUniqueBrands(): List<String> {
        return try {
            val querySnapshot = phonesCollection.get().await()
            val phones = querySnapshot.documents.mapNotNull { 
                it.toObject(FirebasePhone::class.java) 
            }
            phones.map { it.phoneMake }.distinct()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get phones by brand (make) from Firestore
     */
    suspend fun getPhonesByBrand(brand: String): List<Phone> {
        return try {
            val querySnapshot = phonesCollection
                .whereEqualTo("phoneMake", brand)
                .get()
                .await()
                
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(FirebasePhone::class.java)?.toPhone()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get a phone by model and brand from Firestore
     */
    suspend fun getPhoneByModelAndBrand(model: String, brand: String): Phone? {
        return try {
            val querySnapshot = phonesCollection
                .whereEqualTo("phoneMake", brand)
                .whereEqualTo("phoneModel", model)
                .limit(1)
                .get()
                .await()
                
            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(FirebasePhone::class.java)?.toPhone()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Add a phone to Firestore
     */
    suspend fun addPhone(phone: Phone): String {
        return try {
            val firebasePhone = FirebasePhone.fromPhone(phone)
            val documentRef = phonesCollection.add(firebasePhone).await()
            documentRef.id
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Update a phone in Firestore
     */
    suspend fun updatePhone(phone: Phone): Boolean {
        return try {
            val phoneId = phone.productId.toString()
            val firebasePhone = FirebasePhone.fromPhone(phone)
            
            phonesCollection.document(phoneId).set(firebasePhone).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Delete a phone from Firestore
     */
    suspend fun deletePhone(phoneId: String): Boolean {
        return try {
            phonesCollection.document(phoneId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 