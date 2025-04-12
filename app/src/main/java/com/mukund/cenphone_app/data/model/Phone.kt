package com.mukund.cenphonenew.data.model

data class Phone(
    val productId: Long = 0,
    val phoneMake: String,
    val phoneModel: String,
    val phoneColor: String,
    val storageCapacity: String,
    val price: Double
) 