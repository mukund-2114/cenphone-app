package com.mukund.cenphonenew.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.mukund.cenphonenew.data.model.Customer
import com.mukund.cenphonenew.ui.model.DeliveryInfo
import com.mukund.cenphonenew.ui.util.AnimationUtils.paymentButtonEffect
import com.mukund.cenphonenew.ui.util.AnimationUtils.staggeredEntrance
import com.mukund.cenphonenew.ui.util.PaymentHelper
import com.mukund.cenphonenew.ui.util.ToastManager
import com.mukund.cenphonenew.ui.viewmodel.CartViewModel
import com.mukund.cenphonenew.ui.viewmodel.OrderViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    deliveryInfo: DeliveryInfo,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    customer: Customer?,
    onBackClick: () -> Unit,
    onPaymentComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val coroutineScope = rememberCoroutineScope()
    
    // UI state
    var cardNumber by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    
    // Animation states
    val contentAlpha = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.8f) }
    
    // Animate content when screen appears
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(500))
        buttonScale.animateTo(1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ))
    }
    
    // Main screen UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .graphicsLayer(alpha = contentAlpha.value)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(delay = 100, offsetY = 50),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = formatPrice(deliveryInfo.totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Card details form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(delay = 200, offsetY = 50),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Card Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    // Card Number Field
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { 
                            // Only allow digits and limit to 16 characters
                            if (it.length <= 16 && it.all { char -> char.isDigit() }) {
                                cardNumber = it
                            }
                        },
                        label = { Text("Card Number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    // Cardholder Name Field
                    OutlinedTextField(
                        value = cardholderName,
                        onValueChange = { cardholderName = it },
                        label = { Text("Cardholder Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Expiry Date and CVV in one row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { 
                                // Format as MM/YY
                                val filtered = it.filter { char -> char.isDigit() || char == '/' }
                                if (filtered.length <= 5) {
                                    if (filtered.length == 2 && expiryDate.length == 1 && !filtered.contains('/')) {
                                        expiryDate = "$filtered/"
                                    } else {
                                        expiryDate = filtered
                                    }
                                }
                            },
                            label = { Text("Expiry Date (MM/YY)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { 
                                // Only allow digits and limit to 3 or 4 characters
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    cvv = it
                                }
                            },
                            label = { Text("CVV") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pay button
            Button(
                onClick = {
                    // Instead of using Stripe, just complete the payment
                    onPaymentComplete()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale.value)
                    .staggeredEntrance(delay = 300, offsetY = 50),
                enabled = cardNumber.length >= 16 && 
                         cardholderName.isNotBlank() && 
                         expiryDate.length >= 5 && 
                         cvv.length >= 3
            ) {
                Text(
                    text = "Pay ${formatPrice(deliveryInfo.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Add extra space at the bottom to ensure content stays in safe area
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = if (title.contains("Google Pay")) Icons.Default.Payment else Icons.Default.CreditCard,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun fetchPaymentIntent(
    amount: Double,
    onSuccess: (paymentIntent: String, ephemeralKey: String, customer: String, publishableKey: String) -> Unit
) {
    val backendUrl = "https://stripe-server-pkxv.onrender.com/payment-sheet"
    val totalAmountInCents = (amount * 100).toInt()
    
    val jsonBody = JSONObject().apply {
        put("amount", totalAmountInCents)
    }
    
    backendUrl.httpPost()
        .body(jsonBody.toString())
        .header("Content-Type", "application/json")
        .responseJson { _, _, result ->
            when (result) {
                is Result.Success -> {
                    try {
                        val responseJson = result.get().obj()
                        val paymentIntent = responseJson.getString("paymentIntent")
                        val ephemeralKey = responseJson.getString("ephemeralKey")
                        val customer = responseJson.getString("customer")
                        val publishableKey = responseJson.getString("publishableKey")
                        
                        onSuccess(paymentIntent, ephemeralKey, customer, publishableKey)
                        
                        Log.d("PaymentScreen", "Payment setup complete")
                    } catch (e: Exception) {
                        Log.e("PaymentScreen", "Error parsing response: ${e.message}")
                    }
                }
                is Result.Failure -> {
                    Log.e("PaymentScreen", "Error fetching payment intent: ${result.error}")
                }
            }
        }
}

private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.CANADA).format(price)
} 