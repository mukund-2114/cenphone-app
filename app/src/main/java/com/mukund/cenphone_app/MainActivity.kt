package com.mukund.cenphonenew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.ui.navigation.NavGraph
import com.mukund.cenphonenew.ui.theme.CenphoneNewTheme
import com.mukund.cenphonenew.ui.util.ToastManager
import com.mukund.cenphonenew.ui.viewmodel.AuthViewModel
import com.mukund.cenphonenew.ui.viewmodel.CartViewModel
import com.mukund.cenphonenew.ui.viewmodel.CustomerViewModel
import com.mukund.cenphonenew.ui.viewmodel.OrderViewModel
import com.mukund.cenphonenew.ui.viewmodel.PhoneViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firebase in the application context
        FirebaseManager.initialize(applicationContext)
        
        // Ensure Firebase Auth uses persistent sessions
        val auth = FirebaseAuth.getInstance()
        // Setting this explicitly, although Firebase uses PERSISTENCE_DEFAULT by default
        // which should already maintain login sessions between app restarts
        auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(false)
        
        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("MainActivity", "User already signed in: ${currentUser.uid}")
            android.util.Log.d("MainActivity", "Email: ${currentUser.email}")
            // Force Firebase token refresh to ensure we have a valid session
            currentUser.getIdToken(true)
                .addOnSuccessListener { result ->
                    android.util.Log.d("MainActivity", "Token refreshed successfully")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("MainActivity", "Token refresh failed: ${e.message}")
                }
        } else {
            android.util.Log.d("MainActivity", "No user is currently signed in")
        }
        
        setContent {
            CenphoneNewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Box to hold both the NavGraph and the ToastHost
                    Box(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        
                        // ViewModels
                        val phoneViewModel: PhoneViewModel = viewModel()
                        val customerViewModel: CustomerViewModel = viewModel()
                        val authViewModel: AuthViewModel = viewModel()
                        val cartViewModel: CartViewModel = viewModel()
                        val orderViewModel: OrderViewModel = viewModel()
                        
                        // Connect auth and customer view models
                        authViewModel.setCustomerViewModel(customerViewModel)
                        
                        NavGraph(
                            navController = navController,
                            customerViewModel = customerViewModel,
                            authViewModel = authViewModel,
                            phoneViewModel = phoneViewModel,
                            cartViewModel = cartViewModel,
                            orderViewModel = orderViewModel
                        )
                        
                        // Toast manager host to display toast messages
                        ToastManager.ToastHost()
                    }
                }
            }
        }
    }
}