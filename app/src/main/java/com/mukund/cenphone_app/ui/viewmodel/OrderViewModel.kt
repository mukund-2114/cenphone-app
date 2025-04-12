package com.mukund.cenphonenew.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukund.cenphonenew.data.model.Order
import com.mukund.cenphonenew.data.repository.OrderRepository
import com.mukund.cenphonenew.ui.util.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrderRepository()
    private val auth = FirebaseAuth.getInstance()
    
    // StateFlows for UI state
    private val _customerOrders = MutableStateFlow<List<Order>>(emptyList())
    val customerOrders: StateFlow<List<Order>> = _customerOrders.asStateFlow()
    
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder.asStateFlow()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _orderStatus = MutableStateFlow<OrderStatus>(OrderStatus.Initial)
    val orderStatus: StateFlow<OrderStatus> = _orderStatus.asStateFlow()
    
    fun loadCustomerOrders(customerId: Int) {
        viewModelScope.launch {
            android.util.Log.d("OrderViewModel", "Loading orders for customer ID: $customerId")
            val orders = repository.getOrdersByCustomer(customerId)
            android.util.Log.d("OrderViewModel", "Loaded ${orders.size} orders")
            _customerOrders.value = orders
        }
    }
    
    fun loadCustomerOrdersByFirebaseUid(uid: String) {
        viewModelScope.launch {
            android.util.Log.d("OrderViewModel", "Loading orders for Firebase UID: $uid")
            val orders = repository.getOrdersByFirebaseUid(uid)
            android.util.Log.d("OrderViewModel", "Loaded ${orders.size} orders")
            _customerOrders.value = orders
        }
    }
    
    fun createOrder(customerId: Int, productId: Long, totalAmount: Double) {
        viewModelScope.launch {
            val order = Order(
                custId = customerId,
                productId = productId,
                orderDate = Date(),
                totalAmount = totalAmount,
                status = "Pending"
            )
            
            val currentUser = auth.currentUser
            val phoneIdsList = listOf(productId.toString())
            
            if (currentUser != null) {
                val orderId = repository.insertOrder(order, currentUser.uid, phoneIdsList)
                if (orderId.isNotEmpty()) {
                    // Show success toast
                    ToastManager.showSuccessToast("Order placed successfully!")
                    
                    _orderStatus.value = OrderStatus.Success
                    loadCustomerOrders(customerId)
                } else {
                    // Show error toast
                    ToastManager.showErrorToast("Failed to create order")
                    
                    _orderStatus.value = OrderStatus.Failed("Failed to create order")
                }
            } else {
                // Show error toast
                ToastManager.showErrorToast("User not authenticated")
                
                _orderStatus.value = OrderStatus.Failed("User not authenticated")
            }
        }
    }
    
    fun getOrder(orderId: Int) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            _currentOrder.value = order
        }
    }
    
    fun placeOrder(order: Order, phoneIds: List<String>) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val orderId = repository.insertOrder(order, currentUser.uid, phoneIds)
                if (orderId.isNotEmpty()) {
                    // Show success toast
                    ToastManager.showSuccessToast("Order placed successfully!")
                    
                    _orderStatus.value = OrderStatus.Success
                } else {
                    // Show error toast
                    ToastManager.showErrorToast("Failed to place order")
                    
                    _orderStatus.value = OrderStatus.Failed("Failed to place order")
                }
            } else {
                // Show error toast
                ToastManager.showErrorToast("User not logged in")
                
                _orderStatus.value = OrderStatus.Failed("User not logged in")
            }
        }
    }
    
    sealed class OrderStatus {
        object Initial : OrderStatus()
        object Success : OrderStatus()
        data class Failed(val message: String) : OrderStatus()
    }
} 