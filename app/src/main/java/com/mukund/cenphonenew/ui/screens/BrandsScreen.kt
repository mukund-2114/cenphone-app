package com.mukund.cenphonenew.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukund.cenphonenew.ui.util.ImageUtils
import com.mukund.cenphonenew.ui.viewmodel.PhoneViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandsScreen(
    viewModel: PhoneViewModel,
    onBrandClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brands by viewModel.brands.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Animation states
    val visibleState = remember { MutableTransitionState(false) }
    val titleVisibleState = remember { MutableTransitionState(false) }
    
    // Start animations when the screen loads
    LaunchedEffect(key1 = Unit) {
        viewModel.loadBrands()
        
        // Staggered animations
        titleVisibleState.targetState = true
        delay(300)
        visibleState.targetState = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    AnimatedVisibility(
                        visibleState = titleVisibleState,
                        enter = fadeIn(tween(500)) + 
                               slideInVertically(
                                   animationSpec = spring(
                                       dampingRatio = Spring.DampingRatioMediumBouncy,
                                       stiffness = Spring.StiffnessMedium
                                   ),
                                   initialOffsetY = { -100 }
                               ),
                        exit = fadeOut()
                    ) {
                        Text("Select Brand") 
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visibleState = titleVisibleState,
                        enter = fadeIn(tween(600)),
                        exit = fadeOut()
                    ) {
                        Row {
                            IconButton(onClick = onCartClick) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart"
                                )
                            }
                            IconButton(onClick = onProfileClick) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile"
                                )
                            }
                        }
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
            if (isLoading) {
                // Loading animation
                val rotation by animateFloatAsState(
                    targetValue = if (isLoading) 360f else 0f,
                    animationSpec = tween(1500),
                    label = "LoadingRotation"
                )
                
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer {
                                rotationZ = rotation
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Loading brand options...",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(brands) { index, brand ->
                        // Staggered animation delay based on item index
                        val itemVisibleState = remember { MutableTransitionState(false) }
                        
                        LaunchedEffect(visibleState.targetState) {
                            if (visibleState.targetState) {
                                delay(100L * index) // Staggered delay
                                itemVisibleState.targetState = true
                            }
                        }
                        
                        AnimatedVisibility(
                            visibleState = itemVisibleState,
                            enter = fadeIn(tween(500)) + 
                                   slideInVertically(
                                       animationSpec = spring(
                                           dampingRatio = Spring.DampingRatioMediumBouncy,
                                           stiffness = Spring.StiffnessMedium
                                       ),
                                       initialOffsetY = { it / 2 }
                                   ),
                            exit = fadeOut()
                        ) {
                            BrandItem(
                                brand = brand,
                                onClick = { onBrandClick(brand) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandItem(brand: String, onClick: () -> Unit) {
    // Hover animation
    var isHovered by remember { mutableStateOf(false) }
    
    // Card elevation and scale animations
    val elevation by animateFloatAsState(
        targetValue = if (isHovered) 8f else 4f,
        animationSpec = tween(200),
        label = "CardElevation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CardScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale)
            .graphicsLayer {
                shadowElevation = elevation
            }
            .clickable { 
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        // Single column with content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand logo with animation
            val brandLogo = ImageUtils.loadImageFromAssets(ImageUtils.getBrandLogoPath(brand))
            val logoScale by animateFloatAsState(
                targetValue = if (isHovered) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "LogoScale"
            )
            
            brandLogo?.let {
                Image(
                    bitmap = it,
                    contentDescription = "$brand logo",
                    modifier = Modifier
                        .size(70.dp)
                        .scale(logoScale)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = brand,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                color = if (isHovered) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 