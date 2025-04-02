package com.example.cenphone

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    suspend fun getOrdersByCustomer(customerId: Int): List<Order> {
        return orderDao.getOrdersByCustomer(customerId)
    }
}
