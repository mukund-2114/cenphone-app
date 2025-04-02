// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhoneDao {
    @Insert
    suspend fun insert(phone: Phone)

    @Query("SELECT * FROM phone WHERE productId = :id")
    suspend fun getPhoneById(id: Int): Phone?

    @Query("SELECT * FROM phone")
    suspend fun getAllPhones(): List<Phone>

    @Query("SELECT DISTINCT phoneMake FROM phone")
    suspend fun getUniqueBrands(): List<String>

    @Query("DELETE FROM phone")
    suspend fun deleteAllPhones()

    @Query("SELECT * FROM phone WHERE phoneModel = :model AND phoneMake = :brand LIMIT 1")
    suspend fun getPhoneByModelAndBrand(model: String, brand: String): Phone?
}