package com.mukund.cenphonenew.data.model

data class Customer(
    val custId: Int = 0,
    val userName: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    var address: String,
    var city: String,
    var postalCode: String
) 