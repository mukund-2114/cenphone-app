package com.mukund.cenphonenew.data.repository

import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.FirebasePhone
import com.mukund.cenphonenew.data.model.Phone
import kotlinx.coroutines.tasks.await

class PhoneRepository {
    private val firebaseManager = FirebaseManager.getInstance()
    private val phonesCollection = firebaseManager.phonesCollection
    
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
} 