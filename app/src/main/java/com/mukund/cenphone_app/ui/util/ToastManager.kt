package com.mukund.cenphonenew.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * A data class representing a toast message
 */
data class ToastData(
    val message: String,
    val type: ToastType,
    val duration: Long = 3000,
    val id: Long = System.currentTimeMillis() // Unique ID for each toast
)

/**
 * A singleton object that manages toast messages across the app
 */
object ToastManager {
    // Current active toast
    private val _currentToast = mutableStateOf<ToastData?>(null)
    
    // Queue of toast messages waiting to be displayed
    private val toastQueue = mutableListOf<ToastData>()
    
    /**
     * Shows a success toast message
     */
    fun showSuccessToast(message: String, duration: Long = 3000) {
        addToast(ToastData(message, ToastType.SUCCESS, duration))
    }
    
    /**
     * Shows an error toast message
     */
    fun showErrorToast(message: String, duration: Long = 3000) {
        addToast(ToastData(message, ToastType.ERROR, duration))
    }
    
    /**
     * Shows an info toast message
     */
    fun showInfoToast(message: String, duration: Long = 3000) {
        addToast(ToastData(message, ToastType.INFO, duration))
    }
    
    /**
     * Shows a cart toast message
     */
    fun showCartToast(message: String, duration: Long = 3000) {
        addToast(ToastData(message, ToastType.CART, duration))
    }
    
    /**
     * Add a toast to the queue
     */
    private fun addToast(toast: ToastData) {
        toastQueue.add(toast)
        if (_currentToast.value == null) {
            showNextToast()
        }
    }
    
    /**
     * Show the next toast in the queue
     */
    private fun showNextToast() {
        if (toastQueue.isNotEmpty()) {
            _currentToast.value = toastQueue.removeAt(0)
        } else {
            _currentToast.value = null
        }
    }
    
    /**
     * A composable that displays toast messages
     */
    @Composable
    fun ToastHost() {
        val currentToast = _currentToast.value
        
        // Remember the last visible toast to properly animate its dismissal
        var visibleToast by remember { mutableStateOf<ToastData?>(null) }
        
        // Update the visible toast when the current toast changes
        LaunchedEffect(currentToast) {
            visibleToast = currentToast
            
            // If there's a toast to show, wait for its duration and then show the next one
            currentToast?.let {
                delay(it.duration)
                showNextToast()
            }
        }
        
        // Display the visible toast
        visibleToast?.let {
            CustomToast(
                message = it.message,
                type = it.type,
                duration = it.duration
            )
        }
    }
} 