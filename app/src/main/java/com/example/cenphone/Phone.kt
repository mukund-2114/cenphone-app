// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone")
data class Phone(
    @PrimaryKey(autoGenerate = true) val productId: Long = 0,
    val phoneMake: String,
    val phoneModel: String,
    val phoneColor: String,
    val storageCapacity: String,
    val price: Double
)