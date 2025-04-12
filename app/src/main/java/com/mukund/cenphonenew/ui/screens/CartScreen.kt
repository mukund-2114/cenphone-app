package com.mukund.cenphonenew.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukund.cenphonenew.R
import com.mukund.cenphonenew.data.model.Phone
import com.mukund.cenphonenew.ui.util.ImageUtils
import com.mukund.cenphonenew.ui.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
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
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartContent(onContinueShopping)
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(cartItems) { phone ->
                            CartItem(
                                phone = phone,
                                onRemoveClick = {
                                    cartViewModel.removeFromCart(phone)
                                }
                            )
                        }
                    }
                    
                    CheckoutFooter(
                        totalPrice = totalPrice,
                        onCheckoutClick = onCheckoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CartItem(
    phone: Phone,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val phoneImage = ImageUtils.loadImageFromAssets(ImageUtils.getPhoneModelPath(phone.phoneMake))
            phoneImage?.let {
                Image(
                    bitmap = it,
                    contentDescription = "${phone.phoneModel} image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${phone.phoneMake} ${phone.phoneModel}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${phone.phoneColor}, ${phone.storageCapacity}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = formatPrice(phone.price),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove"
                )
            }
        }
    }
}

@Composable
fun CheckoutFooter(
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Divider()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total:",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = formatPrice(totalPrice),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onCheckoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed to Checkout")
        }
    }
}

@Composable
fun EmptyCartContent(onContinueShopping: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Looks like you haven't added any phones to your cart yet.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(onClick = onContinueShopping) {
            Text("Continue Shopping")
        }
    }
}

private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(price)
} 