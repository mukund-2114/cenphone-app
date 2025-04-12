package com.mukund.cenphonenew.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mukund.cenphonenew.data.model.Phone
import com.mukund.cenphonenew.data.repository.FirebasePhoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhoneViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FirebasePhoneRepository = FirebasePhoneRepository()
    
    // StateFlows for UI state
    private val _brands = MutableStateFlow<List<String>>(emptyList())
    val brands: StateFlow<List<String>> = _brands.asStateFlow()
    
    private val _phones = MutableStateFlow<List<Phone>>(emptyList())
    val phones: StateFlow<List<Phone>> = _phones.asStateFlow()
    
    private val _selectedPhone = MutableStateFlow<Phone?>(null)
    val selectedPhone: StateFlow<Phone?> = _selectedPhone.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        // Load brands when ViewModel is created
        loadBrands()
        getPhones()
    }
    
    fun loadBrands() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _brands.value = repository.getUniqueBrands()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getPhones() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _phones.value = repository.getAllPhones()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getPhonesByBrand(brand: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _phones.value = repository.getPhonesByBrand(brand)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectPhone(phone: Phone) {
        _selectedPhone.value = phone
    }
} 