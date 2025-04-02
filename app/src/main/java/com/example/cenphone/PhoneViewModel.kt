package com.example.cenphone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneViewModel(private val repository: PhoneRepository) : ViewModel() {

    fun getPhonesForBrand(brand: String, onResult: (List<Phone>) -> Unit) {
        viewModelScope.launch {
            val phones = withContext(Dispatchers.IO) {
                repository.getAllPhones().filter { it.phoneMake == brand }
            }
            onResult(phones)
        }
    }
}
