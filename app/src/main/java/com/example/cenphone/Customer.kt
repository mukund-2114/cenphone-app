// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer")
data class Customer(
    @PrimaryKey(autoGenerate = true) val custId: Int,
    val userName: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    var address: String,
    var city: String,
    var postalCode: String
)


