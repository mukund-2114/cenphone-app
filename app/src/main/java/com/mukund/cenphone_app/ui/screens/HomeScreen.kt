package com.mukund.cenphonenew.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukund.cenphonenew.ui.theme.CenphoneNewTheme
import com.mukund.cenphonenew.ui.util.ImageUtils
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onOrderNowClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showDescription by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    
    // Infinite transition for continuous animations
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    
    // Logo pulsing animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )
    
    // Button hover animation
    val buttonElevation by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonElevation"
    )
    
    // Title color animation
    val titleColorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleColor"
    )
    
    // Trigger animations sequentially
    LaunchedEffect(Unit) {
        showLogo = true
        delay(400)
        showTitle = true
        delay(300)
        showDescription = true
        delay(300)
        showButton = true
    }
    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background image with subtle animation
            val backgroundImage = ImageUtils.loadImageFromAssets(ImageUtils.getAppBackgroundPath())
            val backgroundAlpha by animateFloatAsState(
                targetValue = 0.2f,
                animationSpec = tween(2000),
                label = "backgroundFade"
            )
            
            backgroundImage?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth,
                    alpha = backgroundAlpha // Animated alpha
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Add extra top space to ensure logo is visible
                Spacer(modifier = Modifier.height(24.dp))
                
                // App Logo with animation at the top
                val logoImage = ImageUtils.loadImageFromAssets(ImageUtils.getAppLogoPath())
                AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn(animationSpec = tween(1000)) + 
                           slideInVertically(
                               animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                               initialOffsetY = { -200 }
                           ),
                    exit = fadeOut()
                ) {
                    logoImage?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "CenPhone Logo",
                            modifier = Modifier
                                .size(220.dp) // Enlarged size
                                .scale(logoScale) // Apply pulsing animation
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                                .padding(12.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Text "CenPhone" that was previously in the top bar
                AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn(animationSpec = tween(800)) + 
                           expandVertically(animationSpec = tween(800)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "CenPhone",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Flexible space to push content down
                Spacer(modifier = Modifier.weight(1f))
                
                // Welcome Text with animation
                AnimatedVisibility(
                    visible = showTitle,
                    enter = fadeIn(animationSpec = tween(800)) + 
                           expandVertically(animationSpec = tween(800)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Welcome to CenPhone",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = titleColorAlpha),
                        modifier = Modifier.graphicsLayer {
                            shadowElevation = 8f * titleColorAlpha
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description Text with animation
                AnimatedVisibility(
                    visible = showDescription,
                    enter = fadeIn(animationSpec = tween(800)) + 
                           expandVertically(animationSpec = tween(800)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Your one-stop shop for premium mobile phones",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Order Now Button with animation
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn(animationSpec = tween(800)) + 
                           expandVertically(animationSpec = tween(800)),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = onOrderNowClick,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(56.dp)
                            .graphicsLayer {
                                shadowElevation = buttonElevation
                            },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = buttonElevation.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = "ORDER NOW",
                            fontSize = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CenphoneNewTheme {
        HomeScreen(
            onOrderNowClick = {},
            onProfileClick = {}
        )
    }
} 