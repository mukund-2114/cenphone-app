package com.example.cenphone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    fun insertOrder(order: Order, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertOrder(order)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun getOrdersByCustomer(
        customerId: Int,
        onResult: (List<Order>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val orders = repository.getOrdersByCustomer(customerId)
                onResult(orders)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
