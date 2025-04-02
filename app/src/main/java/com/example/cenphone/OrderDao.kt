// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
// app/src/main/java/com/example/cenphone/OrderDao.kt
package com.example.cenphone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM orders WHERE custId = :customerId")
    suspend fun getOrdersByCustomer(customerId: Int): List<Order>
}