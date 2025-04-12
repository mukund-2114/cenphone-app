package com.mukund.cenphonenew.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukund.cenphonenew.data.firebase.FirebaseManager
import com.mukund.cenphonenew.data.model.Phone
import com.mukund.cenphonenew.ui.util.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    
    // StateFlows for UI state
    private val _cartItems = MutableStateFlow<List<Phone>>(emptyList())
    val cartItems: StateFlow<List<Phone>> = _cartItems.asStateFlow()
    
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()
    
    // Firestore collection for cart items
    private val cartCollection = firebaseManager.firestore.collection("carts")
    
    init {
        // Load cart if user is authenticated
        loadCartFromFirestore()
    }
    
    private fun loadCartFromFirestore() {
        val userId = firebaseManager.currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                val cartDoc = cartCollection.document(userId).get().await()
                
                if (cartDoc.exists()) {
                    val phoneIds = cartDoc.get("phoneIds") as? List<String> ?: emptyList()
                    val phonesCollection = firebaseManager.phonesCollection
                    
                    val phones = mutableListOf<Phone>()
                    for (phoneId in phoneIds) {
                        val phoneDoc = phonesCollection.document(phoneId).get().await()
                        val firebasePhone = phoneDoc.toObject(com.mukund.cenphonenew.data.model.FirebasePhone::class.java)
                        firebasePhone?.let { phones.add(it.toPhone()) }
                    }
                    
                    _cartItems.value = phones
                    calculateTotal()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun saveCartToFirestore() {
        val userId = firebaseManager.currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                // Get phone IDs from the current cart items
                val phoneIds = _cartItems.value.map { it.productId.toString() }
                
                // Save to Firestore
                val cartData = hashMapOf(
                    "userId" to userId,
                    "phoneIds" to phoneIds,
                    "totalPrice" to _totalPrice.value,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
                
                cartCollection.document(userId).set(cartData).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun addToCart(phone: Phone) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.add(phone)
        _cartItems.value = currentItems
        calculateTotal()
        
        // Show toast message for added item
        ToastManager.showCartToast("${phone.phoneModel} added to cart")
        
        // Save updated cart to Firestore
        saveCartToFirestore()
    }
    
    fun removeFromCart(phone: Phone) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.remove(phone)
        _cartItems.value = currentItems
        calculateTotal()
        
        // Show toast message for removed item
        ToastManager.showInfoToast(" ${phone.phoneModel} removed from cart")
        
        // Save updated cart to Firestore
        saveCartToFirestore()
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
        _totalPrice.value = 0.0
        
        // Show toast message for cleared cart
        ToastManager.showInfoToast("Your cart has been cleared")
        
        // Remove cart from Firestore
        val userId = firebaseManager.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                try {
                    cartCollection.document(userId).delete().await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    private fun calculateTotal() {
        _totalPrice.value = _cartItems.value.sumOf { it.price }
    }
    
    // This method should be called when the user logs in
    fun refreshCart() {
        loadCartFromFirestore()
    }
    
    // This method should be called when the user logs out
    fun clearUserCart() {
        clearCart()
    }
} 