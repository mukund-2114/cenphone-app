package com.mukund.cenphonenew.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mukund.cenphonenew.data.model.Customer
import com.mukund.cenphonenew.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.mukund.cenphonenew.ui.util.ToastManager

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CustomerRepository()
    private val auth = FirebaseAuth.getInstance()
    
    // StateFlows for UI state
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()
    
    private val _currentCustomer = MutableStateFlow<Customer?>(null)
    val currentCustomer: StateFlow<Customer?> = _currentCustomer.asStateFlow()
    
    private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.NotLoggedIn)
    val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()
    
    private val _registrationStatus = MutableStateFlow<RegistrationStatus>(RegistrationStatus.Initial)
    val registrationStatus: StateFlow<RegistrationStatus> = _registrationStatus.asStateFlow()
    
    private val _updateResult = MutableStateFlow<Boolean>(false)
    val updateResult: StateFlow<Boolean> = _updateResult.asStateFlow()
    
    // Flag to prevent duplicate loading of customer data
    private var isLoadingCustomerData = false
    
    init {
        loadCustomers()
        checkCurrentAuthUser()
        
        // Add auth state listener
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                android.util.Log.d("CustomerViewModel", "Auth state changed: User signed in: ${user.uid}")
                loadCustomerByFirebaseUid(user.uid)
            } else {
                android.util.Log.d("CustomerViewModel", "Auth state changed: User signed out")
                _currentCustomer.value = null
                _loginStatus.value = LoginStatus.NotLoggedIn
            }
        }
    }
    
    private fun loadCustomers() {
        viewModelScope.launch {
            repository.getAllCustomers().collect { customers ->
                _customers.value = customers
            }
        }
    }
    
    private fun checkCurrentAuthUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            android.util.Log.d("CustomerViewModel", "Current Firebase user found: ${firebaseUser.uid}")
            loadCustomerByFirebaseUid(firebaseUser.uid)
        } else {
            android.util.Log.d("CustomerViewModel", "No current Firebase user")
        }
    }
    
    private fun loadCustomerByFirebaseUid(uid: String) {
        // Prevent duplicate loading if already in progress
        if (isLoadingCustomerData) {
            android.util.Log.d("CustomerViewModel", "Customer data load already in progress, skipping")
            return
        }
        
        isLoadingCustomerData = true
        
        viewModelScope.launch {
            try {
                android.util.Log.d("CustomerViewModel", "Loading customer data for UID: $uid")
                val customer = repository.getCustomerByFirebaseUid(uid)
                
                if (customer != null) {
                    android.util.Log.d("CustomerViewModel", "Customer found: ${customer.userName}")
                    _currentCustomer.value = customer
                    _loginStatus.value = LoginStatus.Success
                } else {
                    android.util.Log.e("CustomerViewModel", "No customer found for UID: $uid")
                    
                    // If no customer found in Firestore, create a basic one from Auth data
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        android.util.Log.d("CustomerViewModel", "Creating basic customer from Firebase Auth data")
                        
                        val basicCustomer = Customer(
                            custId = uid.hashCode(),
                            userName = firebaseUser.email ?: "",
                            password = "",
                            firstname = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "",
                            lastname = firebaseUser.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                            address = "",
                            city = "",
                            postalCode = ""
                        )
                        
                        _currentCustomer.value = basicCustomer
                        _loginStatus.value = LoginStatus.Success
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CustomerViewModel", "Error loading customer: ${e.message}", e)
            } finally {
                isLoadingCustomerData = false
            }
        }
    }
    
    /**
     * Loads the customer information for the currently logged-in Firebase Auth user
     */
    fun loadCustomerByFirebaseAuth() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("CustomerViewModel", "Loading customer for authenticated user: ${currentUser.uid}")
            loadCustomerByFirebaseUid(currentUser.uid)
        } else {
            android.util.Log.d("CustomerViewModel", "Cannot load customer: No authenticated user")
            _loginStatus.value = LoginStatus.NotLoggedIn
        }
    }
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            val customer = repository.getCustomerByUsername(username)
            if (customer != null && customer.password == password) {
                _currentCustomer.value = customer
                _loginStatus.value = LoginStatus.Success
            } else {
                _loginStatus.value = LoginStatus.Failed("Invalid username or password")
            }
        }
    }
    
    fun registerCustomer(
        username: String,
        password: String,
        name: String,
        email: String,
        phone: String,
        address: String,
        city: String,
        postalCode: String
    ) {
        viewModelScope.launch {
            try {
                android.util.Log.d("CustomerViewModel", "Starting registration for email: $email")
                
                // 1. Register with Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                
                if (user == null) {
                    android.util.Log.e("CustomerViewModel", "Registration failed: Firebase user is null")
                    _registrationStatus.value = RegistrationStatus.Failed("Authentication failed")
                    return@launch
                }
                
                android.util.Log.d("CustomerViewModel", "Firebase Auth registration successful for UID: ${user.uid}")
                
                // 2. Create customer object and save to Firestore
                val customer = Customer(
                    custId = 0, // Will be set by Firestore
                    userName = email,
                    password = "", // We don't store passwords in Firestore
                    firstname = name.split(" ").firstOrNull() ?: "",
                    lastname = name.split(" ").drop(1).joinToString(" "),
                    address = address,
                    city = city,
                    postalCode = postalCode
                )
                
                val userId = user.uid
                android.util.Log.d("CustomerViewModel", "Saving customer data to Firestore with UID: $userId")
                
                val customerId = repository.insertCustomer(customer, userId)
                if (customerId.isNotEmpty()) {
                    android.util.Log.d("CustomerViewModel", "Customer data saved successfully")
                    
                    // Set current customer so profile is immediately available
                    _currentCustomer.value = customer.copy(custId = userId.hashCode())
                    _loginStatus.value = LoginStatus.Success
                    _registrationStatus.value = RegistrationStatus.Success
                    
                    // Show success toast
                    ToastManager.showSuccessToast("Your account has been created successfully!")
                } else {
                    android.util.Log.e("CustomerViewModel", "Failed to save customer data")
                    _registrationStatus.value = RegistrationStatus.Failed("Failed to save customer data")
                    
                    // Show error toast
                    ToastManager.showErrorToast("Failed to save customer data")
                }
            } catch (e: Exception) {
                android.util.Log.e("CustomerViewModel", "Registration failed: ${e.message}", e)
                _registrationStatus.value = RegistrationStatus.Failed(e.message ?: "Registration failed")
                
                // Show error toast
                ToastManager.showErrorToast("Registration failed: ${e.message}")
            }
        }
    }
    
    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            _updateResult.value = false
            
            try {
                val success = repository.updateCustomer(customer, auth.currentUser?.uid ?: "")
                if (success) {
                    _updateResult.value = true
                    
                    // Update the current customer
                    _currentCustomer.value = customer
                    
                    // Show success toast
                    ToastManager.showSuccessToast("Profile updated successfully!")
                } else {
                    android.util.Log.e("CustomerViewModel", "Failed to update customer")
                    _updateResult.value = false
                    
                    // Show error toast
                    ToastManager.showErrorToast("Failed to update profile")
                }
            } catch (e: Exception) {
                android.util.Log.e("CustomerViewModel", "Error updating customer: ${e.message}", e)
                _updateResult.value = false
                
                // Show error toast
                ToastManager.showErrorToast("Failed to update profile: ${e.message}")
            }
        }
    }
    
    fun logout() {
        _currentCustomer.value = null
        _loginStatus.value = LoginStatus.NotLoggedIn
    }
    
    suspend fun insertCustomer(customer: Customer): String {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val customerId = repository.insertCustomer(customer, currentUser.uid)
            customerId
        } else {
            ""
        }
    }
    
    /**
     * Set basic customer info from auth data
     * This ensures some profile data is available even if Firestore lookup fails
     */ 
    fun setBasicCustomerInfo(customer: Customer) {
        android.util.Log.d("CustomerViewModel", "Setting basic customer info: ${customer.userName}")
        _currentCustomer.value = customer
        _loginStatus.value = LoginStatus.Success
    }
    
    sealed class LoginStatus {
        object NotLoggedIn : LoginStatus()
        object Success : LoginStatus()
        data class Failed(val message: String) : LoginStatus()
    }
    
    sealed class RegistrationStatus {
        data object Initial : RegistrationStatus()
        object Success : RegistrationStatus()
        data class Failed(val message: String) : RegistrationStatus()
    }
} 