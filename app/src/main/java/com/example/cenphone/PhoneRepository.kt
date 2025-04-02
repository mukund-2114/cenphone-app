package com.example.cenphone

class PhoneRepository(private val phoneDao: PhoneDao) {

    suspend fun getAllPhones(): List<Phone> = phoneDao.getAllPhones()

    suspend fun getPhoneByModelAndBrand(model: String, brand: String): Phone? =
        phoneDao.getPhoneByModelAndBrand(model, brand)
}
