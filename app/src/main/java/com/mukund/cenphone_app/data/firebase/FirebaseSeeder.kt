package com.mukund.cenphonenew.data.firebase

import com.mukund.cenphonenew.data.model.FirebasePhone
import kotlinx.coroutines.tasks.await

/**
 * Helper class to seed the Firestore database with initial data
 */
object FirebaseSeeder {
    
    /**
     * Seed the database with sample data if it's empty
     */
    suspend fun seedDatabase() {
        val firebaseManager = FirebaseManager.getInstance()
        
        try {
            // Check if phones collection is empty
            val snapshot = firebaseManager.phonesCollection.limit(1).get().await()
            
            if (snapshot.isEmpty) {
                // Add sample phones
                val samplePhones = listOf(
                    // Apple Phones
                    FirebasePhone(
                        phoneMake = "Apple", 
                        phoneModel = "iPhone 15 Pro Max", 
                        phoneColor = "Natural Titanium", 
                        storageCapacity = "1TB", 
                        price = 1599.0,
                        imageUrl = "images/iphone15promax.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Apple", 
                        phoneModel = "iPhone 15 Pro", 
                        phoneColor = "Blue Titanium", 
                        storageCapacity = "512GB", 
                        price = 1399.0,
                        imageUrl = "images/iphone15pro.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Apple", 
                        phoneModel = "iPhone 15", 
                        phoneColor = "Black", 
                        storageCapacity = "256GB", 
                        price = 999.0,
                        imageUrl = "images/iphone15.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Apple", 
                        phoneModel = "iPhone 14 Pro", 
                        phoneColor = "Deep Purple", 
                        storageCapacity = "256GB", 
                        price = 1099.0,
                        imageUrl = "images/iphone14pro.png"
                    ),
                    
                    // Samsung Phones
                    FirebasePhone(
                        phoneMake = "Samsung", 
                        phoneModel = "Galaxy S24 Ultra", 
                        phoneColor = "Titanium Black", 
                        storageCapacity = "512GB", 
                        price = 1419.0,
                        imageUrl = "images/s24ultra.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Samsung", 
                        phoneModel = "Galaxy S24+", 
                        phoneColor = "Cobalt Violet", 
                        storageCapacity = "256GB", 
                        price = 1119.0,
                        imageUrl = "images/s24plus.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Samsung", 
                        phoneModel = "Galaxy Z Fold5", 
                        phoneColor = "Phantom Black", 
                        storageCapacity = "512GB", 
                        price = 1919.0,
                        imageUrl = "images/fold5.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Samsung", 
                        phoneModel = "Galaxy Z Flip5", 
                        phoneColor = "Mint", 
                        storageCapacity = "256GB", 
                        price = 1099.0,
                        imageUrl = "images/flip5.png"
                    ),
                    
                    // Google Phones
                    FirebasePhone(
                        phoneMake = "Google", 
                        phoneModel = "Pixel 8 Pro", 
                        phoneColor = "Obsidian", 
                        storageCapacity = "256GB", 
                        price = 999.0,
                        imageUrl = "images/pixel8pro.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Google", 
                        phoneModel = "Pixel 8", 
                        phoneColor = "Hazel", 
                        storageCapacity = "128GB", 
                        price = 699.0,
                        imageUrl = "images/pixel8.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Google", 
                        phoneModel = "Pixel 7 Pro", 
                        phoneColor = "Snow", 
                        storageCapacity = "256GB", 
                        price = 799.0,
                        imageUrl = "images/pixel7pro.png"
                    ),
                    
                    // Xiaomi Phones
                    FirebasePhone(
                        phoneMake = "Xiaomi", 
                        phoneModel = "Mi 14 Ultra", 
                        phoneColor = "Black", 
                        storageCapacity = "512GB", 
                        price = 999.0,
                        imageUrl = "images/mi14ultra.png"
                    ),
                    FirebasePhone(
                        phoneMake = "Xiaomi", 
                        phoneModel = "Redmi Note 13 Pro", 
                        phoneColor = "Midnight Black", 
                        storageCapacity = "256GB", 
                        price = 349.0,
                        imageUrl = "images/note13pro.png"
                    ),
                    
                    // BlackBerry
                    FirebasePhone(
                        phoneMake = "BlackBerry", 
                        phoneModel = "Key3", 
                        phoneColor = "Black", 
                        storageCapacity = "128GB", 
                        price = 599.0,
                        imageUrl = "images/blackberrykey3.png"
                    )
                )
                
                // Add each phone to Firestore
                val batch = firebaseManager.firestore.batch()
                
                samplePhones.forEach { phone ->
                    val docRef = firebaseManager.phonesCollection.document()
                    batch.set(docRef, phone)
                }
                
                // Commit the batch
                batch.commit().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 