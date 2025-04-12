package com.mukund.cenphonenew.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukund.cenphonenew.ui.util.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private var customerViewModel: CustomerViewModel? = null
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Initial)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()
    
    init {
        checkCurrentUser()
        
        // Add auth state listener
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                android.util.Log.d("AuthViewModel", "Auth state changed: User signed in: ${user.uid}")
                _loginState.value = LoginState.Success
                
                // When auth state changes to signed in, ensure customer data is loaded
                customerViewModel?.let { viewModel ->
                    viewModelScope.launch {
                        try {
                            // Small delay to ensure Firebase has time to synchronize
                            kotlinx.coroutines.delay(300)
                            android.util.Log.d("AuthViewModel", "Loading customer data after auth state change")
                            viewModel.loadCustomerByFirebaseAuth()
                        } catch (e: Exception) {
                            android.util.Log.e("AuthViewModel", "Error loading customer data: ${e.message}")
                        }
                    }
                }
            } else {
                android.util.Log.d("AuthViewModel", "Auth state changed: User signed out")
                _loginState.value = LoginState.Initial
            }
        }
    }
    
    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("AuthViewModel", "User already logged in: ${currentUser.uid}")
            
            // Refresh the token to ensure session is valid
            viewModelScope.launch {
                try {
                    val result = currentUser.getIdToken(true).await()
                    android.util.Log.d("AuthViewModel", "Token refreshed: ${result.token?.take(10)}...")
                    _loginState.value = LoginState.Success
                } catch (e: Exception) {
                    android.util.Log.e("AuthViewModel", "Token refresh failed: ${e.message}")
                    // If token refresh fails, we might need to force reauth
                    // But for now we'll still consider the user logged in
                    _loginState.value = LoginState.Success
                }
            }
        } else {
            android.util.Log.d("AuthViewModel", "No user currently logged in")
            _loginState.value = LoginState.Initial
        }
    }
    
    fun setCustomerViewModel(viewModel: CustomerViewModel) {
        customerViewModel = viewModel
        
        // If we already have a logged-in user, inform the CustomerViewModel
        if (auth.currentUser != null) {
            android.util.Log.d("AuthViewModel", "Triggering customer data load for existing user")
            viewModelScope.launch {
                // Small delay to ensure Firebase auth is fully initialized
                kotlinx.coroutines.delay(300)
                customerViewModel?.loadCustomerByFirebaseAuth()
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                android.util.Log.d("AuthViewModel", "Attempting login for email: $email")
                
                // Sign in with Firebase Auth
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user
                
                if (user != null) {
                    android.util.Log.d("AuthViewModel", "Firebase Auth login successful for UID: ${user.uid}")
                    
                    // Tell CustomerViewModel to load the customer data
                    if (customerViewModel != null) {
                        android.util.Log.d("AuthViewModel", "Triggering customer data load")
                        
                        // Create a customer with basic info from auth result
                        // This ensures some data is available even if Firestore lookup fails
                        val basicCustomer = com.mukund.cenphonenew.data.model.Customer(
                            custId = user.uid.hashCode(),
                            userName = email,
                            password = "",
                            firstname = user.displayName?.split(" ")?.firstOrNull() ?: "",
                            lastname = user.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                            address = "",
                            city = "",
                            postalCode = ""
                        )
                        
                        customerViewModel?.setBasicCustomerInfo(basicCustomer)
                        customerViewModel?.loadCustomerByFirebaseAuth()
                    } else {
                        android.util.Log.e("AuthViewModel", "CustomerViewModel is null")
                    }
                    
                    // Give it a moment to load the data before considering it a success
                    kotlinx.coroutines.delay(500)
                    
                    // Show success toast message
                    ToastManager.showSuccessToast("Welcome back! You are now logged in.")
                    
                    _loginState.value = LoginState.Success
                } else {
                    android.util.Log.e("AuthViewModel", "Firebase Auth login succeeded but user is null")
                    // Show error toast message
                    ToastManager.showErrorToast("Login failed: user is null")
                    _loginState.value = LoginState.Error("Login failed: user is null")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Login failed: ${e.message}", e)
                // Show error toast message
                ToastManager.showErrorToast("Login failed: ${e.message ?: "Unknown error"}")
                _loginState.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _registrationState.value = RegistrationState.Loading
                auth.createUserWithEmailAndPassword(email, password).await()
                
                // Show success toast message
                ToastManager.showSuccessToast("Registration successful! Welcome to CenPhone.")
                
                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                // Show error toast message
                ToastManager.showErrorToast("Registration failed: ${e.message ?: "Unknown error"}")
                
                _registrationState.value = RegistrationState.Error(e.message ?: "Registration failed")
            }
        }
    }
    
    fun logout() {
        auth.signOut()
        // Show info toast message
        ToastManager.showInfoToast("You have been logged out")
    }
    
    fun resetLoginState() {
        _loginState.value = LoginState.Initial
    }
    
    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Initial
    }
    
    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
    
    sealed class RegistrationState {
        object Initial : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
} 