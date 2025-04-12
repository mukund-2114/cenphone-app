package com.mukund.cenphonenew.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukund.cenphonenew.R
import com.mukund.cenphonenew.ui.util.ImageUtils
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun OrderConfirmationScreen(
    onContinueShopping: () -> Unit,
    onViewOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    
    // Infinite transition for continuous animations
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    
    // Success image pulsing animation
    val imagePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "imagePulse"
    )
    
    // Title wave animation
    val titleWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing)
        ),
        label = "titleWave"
    )
    
    // Button animation
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonScale"
    )
    
    // Trigger animations sequentially
    LaunchedEffect(Unit) {
        showContent = true
        showConfetti = true
        delay(100)
        showImage = true
        delay(300)
        showTitle = true
        delay(300)
        showText = true
        delay(500)
        showButtons = true
    }
    
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Confetti animation
            if (showConfetti) {
                ConfettiAnimation(
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut()
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Success image with animation
                    AnimatedVisibility(
                        visible = showImage,
                        enter = fadeIn(animationSpec = tween(800)) + 
                               slideInVertically(
                                   animationSpec = spring(
                                       dampingRatio = Spring.DampingRatioLowBouncy,
                                       stiffness = Spring.StiffnessMedium
                                   ),
                                   initialOffsetY = { -200 }
                               ),
                        exit = fadeOut()
                    ) {
                        val successImage = ImageUtils.loadImageFromAssets(ImageUtils.getSuccessImagePath())
                        
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Glowing circle background
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .scale(imagePulse)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                            )
                            
                            successImage?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "Success",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .scale(imagePulse)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Fit
                                )
                            } ?: Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                modifier = Modifier
                                    .size(100.dp)
                                    .scale(imagePulse),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Title with animation
                    AnimatedVisibility(
                        visible = showTitle,
                        enter = fadeIn(animationSpec = tween(800)) + 
                               expandVertically(animationSpec = tween(800)),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Order Placed Successfully!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .graphicsLayer { 
                                    // Apply a subtle wave animation to the title
                                    rotationZ = sin(titleWave * Math.PI.toFloat() / 180) * 2f
                                    shadowElevation = 8f + sin(titleWave * Math.PI.toFloat() / 180) * 4f
                                }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Thank you text with animation
                    AnimatedVisibility(
                        visible = showText,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 300)) + 
                               expandVertically(animationSpec = tween(800, delayMillis = 300)),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Thank you for your order. We have received your purchase request and will process it soon.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Email confirmation text with animation
                    AnimatedVisibility(
                        visible = showText,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 500)) + 
                               expandVertically(animationSpec = tween(800, delayMillis = 500)),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "You will receive an email confirmation shortly.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Continue shopping button with animation
                    AnimatedVisibility(
                        visible = showButtons,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 600)) + 
                               slideInVertically(
                                   animationSpec = tween(800, delayMillis = 600),
                                   initialOffsetY = { it / 3 }
                               ),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = onContinueShopping,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .scale(buttonScale),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Text(
                                "Continue Shopping",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // View orders button with animation
                    AnimatedVisibility(
                        visible = showButtons,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 700)) + 
                               slideInVertically(
                                   animationSpec = tween(800, delayMillis = 700),
                                   initialOffsetY = { it / 3 }
                               ),
                        exit = fadeOut()
                    ) {
                        OutlinedButton(
                            onClick = onViewOrders,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .graphicsLayer {
                                    // Apply a subtle shadow animation
                                    shadowElevation = 2f + sin(titleWave * Math.PI.toFloat() / 180) * 2f
                                },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "View My Orders",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfettiAnimation(modifier: Modifier = Modifier) {
    val confettiCount = 100
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        Color.Yellow,
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Magenta
    )
    
    // Animate falling confetti
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiFall"
    )
    
    // Define confetti pieces
    val confettiPieces = remember {
        List(confettiCount) {
            val color = colors[Random.nextInt(colors.size)]
            val x = Random.nextFloat()
            val y = Random.nextFloat() * -1f // Start above screen
            val size = Random.nextFloat() * 15f + 5f
            val angle = Random.nextFloat() * 360f
            val speed = Random.nextFloat() * 0.6f + 0.4f
            
            object {
                val color = color
                val initialX = x
                val initialY = y
                val size = size
                val initialAngle = angle
                val rotationSpeed = (Random.nextFloat() - 0.5f) * 20f
                val fallSpeed = speed
                val sway = Random.nextFloat() * 10f
                val swayFrequency = Random.nextFloat() * 5f + 1f
                val shape = Random.nextInt(3) // 0: circle, 1: rectangle, 2: custom shape
            }
        }
    }
    
    Canvas(modifier = modifier) {
        // Draw each confetti piece
        confettiPieces.forEach { piece ->
            val x = size.width * piece.initialX + sin(animationProgress * piece.swayFrequency * Math.PI.toFloat() * 2f) * piece.sway
            
            // Calculate falling position with acceleration
            val gravity = 1.5f
            val fallDistance = animationProgress * piece.fallSpeed * size.height * gravity
            val y = size.height * piece.initialY + fallDistance
            
            // Wrap around when it falls below the bottom
            val wrappedY = (y % size.height)
            
            // Calculate rotation
            val angle = piece.initialAngle + animationProgress * piece.rotationSpeed * 360f
            
            // Draw the confetti based on its shape
            drawContext.canvas.save()
            drawContext.canvas.translate(x, wrappedY)
            rotate(angle) {
                when (piece.shape) {
                    0 -> { // Circle
                        drawCircle(
                            color = piece.color,
                            radius = piece.size / 2,
                            center = Offset(0f, 0f)
                        )
                    }
                    1 -> { // Rectangle
                        drawRect(
                            color = piece.color,
                            size = androidx.compose.ui.geometry.Size(piece.size, piece.size / 2),
                            topLeft = Offset(-piece.size / 2, -piece.size / 4)
                        )
                    }
                    else -> { // Star or custom shape
                        val path = Path()
                        val radius = piece.size / 2
                        for (i in 0 until 5) {
                            val angle = i * 2 * Math.PI / 5
                            val x = radius * cos(angle).toFloat()
                            val y = radius * sin(angle).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(path, piece.color)
                    }
                }
            }
            drawContext.canvas.restore()
        }
    }
} 