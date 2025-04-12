package com.mukund.cenphonenew.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.mukund.cenphonenew.data.model.Order
import com.mukund.cenphonenew.ui.viewmodel.CustomerViewModel
import com.mukund.cenphonenew.ui.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    customerViewModel: CustomerViewModel,
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customer by customerViewModel.currentCustomer.collectAsState()
    val orders by orderViewModel.customerOrders.collectAsState()
    val auth = FirebaseAuth.getInstance()
    
    // Log current state for debugging
    LaunchedEffect(Unit) {
        android.util.Log.d("ProfileScreen", "Firebase Auth current user: ${auth.currentUser?.uid ?: "null"}")
        android.util.Log.d("ProfileScreen", "Current customer: ${customer?.userName ?: "null"}")
    }
    
    // Force refresh customer data whenever this screen is shown
    LaunchedEffect(Unit) {
        customerViewModel.loadCustomerByFirebaseAuth()
    }
    
    // Load orders when customer is available
    LaunchedEffect(customer) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("ProfileScreen", "Loading orders for Firebase UID: ${currentUser.uid}")
            orderViewModel.loadCustomerOrdersByFirebaseUid(currentUser.uid)
        } else if (customer != null) {
            android.util.Log.d("ProfileScreen", "No Firebase user, loading orders for customer ID: ${customer?.custId}")
            orderViewModel.loadCustomerOrders(customer!!.custId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (customer == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You are not logged in",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Please log in to view your profile"
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "${customer?.firstname} ${customer?.lastname}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = customer?.userName ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CustomerInfoRow(label = "Address", value = customer?.address ?: "")
                                CustomerInfoRow(label = "City", value = customer?.city ?: "")
                                CustomerInfoRow(label = "Postal Code", value = customer?.postalCode ?: "")
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedButton(
                                onClick = onLogoutClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Logout")
                            }
                        }
                    }
                }
                
                // Orders section
                item {
                    Text(
                        text = "Your Orders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                if (orders.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No orders yet",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                } else {
                    items(orders) { order ->
                        OrderItem(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun OrderItem(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Divider()
            
            Text(
                text = "Date: ${formatDate(order.orderDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Amount: ${formatPrice(order.totalAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatDate(date: java.util.Date): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(price)
} 