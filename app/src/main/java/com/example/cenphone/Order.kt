// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["custId"],
            childColumns = ["custId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Phone::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val custId: Int,
    val productId: Long,
    val orderDate: Date,
    val totalAmount: Double,
    val status: String
)
