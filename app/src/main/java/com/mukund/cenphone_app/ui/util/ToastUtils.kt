package com.mukund.cenphonenew.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS, ERROR, INFO, CART
}

/**
 * Shows a custom beautiful toast message.
 * @param message The message to display in the toast
 * @param type The type of toast (SUCCESS, ERROR, INFO, CART)
 * @param duration Duration in milliseconds the toast will be shown
 */
@Composable
fun CustomToast(
    message: String,
    type: ToastType = ToastType.SUCCESS,
    duration: Long = 3000,
    modifier: Modifier = Modifier
) {
    val visible = remember { MutableTransitionState(false) }
    
    // Set the toast to appear
    LaunchedEffect(message) {
        visible.targetState = true
        delay(duration)
        visible.targetState = false
    }
    
    // Shadow and elevation animation
    val elevation by animateFloatAsState(
        targetValue = if (visible.targetState) 8f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ToastElevation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 48.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visibleState = visible,
            enter = fadeIn(animationSpec = tween(500)) + 
                   slideInVertically(
                       animationSpec = spring(
                           dampingRatio = Spring.DampingRatioMediumBouncy,
                           stiffness = Spring.StiffnessLow
                       ),
                       initialOffsetY = { 200 }
                   ),
            exit = fadeOut(animationSpec = tween(500)) +
                   slideOutVertically(
                       animationSpec = tween(500),
                       targetOffsetY = { 200 }
                   )
        ) {
            Surface(
                modifier = Modifier
                    .shadow(elevation = elevation.dp, shape = RoundedCornerShape(12.dp))
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(12.dp),
                color = when (type) {
                    ToastType.SUCCESS -> Color(0xFF4CAF50).copy(alpha = 0.95f)
                    ToastType.ERROR -> Color(0xFFE53935).copy(alpha = 0.95f)
                    ToastType.INFO -> Color(0xFF2196F3).copy(alpha = 0.95f)
                    ToastType.CART -> Color(0xFFFF9800).copy(alpha = 0.95f)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon based on the toast type
                    val icon = when (type) {
                        ToastType.SUCCESS -> Icons.Filled.CheckCircle
                        ToastType.ERROR -> Icons.Filled.Error
                        ToastType.INFO -> Icons.Filled.Info
                        ToastType.CART -> Icons.Filled.ShoppingCart
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
} 