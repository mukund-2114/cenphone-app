package com.mukund.cenphonenew

import android.app.Application
import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.firebase.FirebaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CenphoneApp : Application() {
    // Application scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseManager.initialize(applicationContext)
        
        // Seed the Firestore database in a background coroutine
        applicationScope.launch {
            FirebaseSeeder.seedDatabase()
        }
    }
} 