package com.mukund.cenphonenew.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukund.cenphonenew.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Animation states
    val animationStarted = remember { MutableTransitionState(false) }
    var emailFieldFocused by remember { mutableStateOf(false) }
    var passwordFieldFocused by remember { mutableStateOf(false) }
    
    // Start the enter animation on first composition
    LaunchedEffect(Unit) {
        animationStarted.targetState = true
    }
    
    // Animation for the title
    val titleScale by animateFloatAsState(
        targetValue = if (animationStarted.targetState) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )
    
    // Infinite animation for the login button
    val infiniteTransition = rememberInfiniteTransition(label = "buttonAnimation")
    val buttonElevation by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "buttonElevation"
    )
    
    // Field focus animations
    val emailScale by animateFloatAsState(
        targetValue = if (emailFieldFocused) 1.02f else 1f,
        animationSpec = tween(200),
        label = "emailScale"
    )
    
    val passwordScale by animateFloatAsState(
        targetValue = if (passwordFieldFocused) 1.02f else 1f,
        animationSpec = tween(200),
        label = "passwordScale"
    )
    
    // Handle login state
    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthViewModel.LoginState.Success -> {
                onLoginSuccess()
                viewModel.resetLoginState()
            }
            is AuthViewModel.LoginState.Error -> {
                val errorMessage = (loginState as AuthViewModel.LoginState.Error).message
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.resetLoginState()
            }
            else -> { /* do nothing */ }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = animationStarted,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            initialOffsetY = { it / 2 }
                        ),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header with animation
                    Text(
                        text = "Welcome to CenPhone",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .scale(titleScale)
                            .graphicsLayer {
                                shadowElevation = 8f * titleScale
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Subtitle with animation
                    AnimatedVisibility(
                        visible = animationStarted.targetState,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 300)) +
                                expandVertically(animationSpec = tween(500, delayMillis = 300)),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Login to your account",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Email field with animation
                    AnimatedVisibility(
                        visible = animationStarted.targetState,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 400)) +
                                slideInVertically(
                                    animationSpec = tween(500, delayMillis = 400),
                                    initialOffsetY = { it / 3 }
                                ),
                        exit = fadeOut()
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(emailScale)
                                .onFocusChanged { 
                                    emailFieldFocused = it.isFocused 
                                }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password field with animation
                    AnimatedVisibility(
                        visible = animationStarted.targetState,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 500)) +
                                slideInVertically(
                                    animationSpec = tween(500, delayMillis = 500),
                                    initialOffsetY = { it / 3 }
                                ),
                        exit = fadeOut()
                    ) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        text = if (passwordVisible) "Hide" else "Show",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(passwordScale)
                                .onFocusChanged { 
                                    passwordFieldFocused = it.isFocused 
                                }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Login button with animation
                    AnimatedVisibility(
                        visible = animationStarted.targetState,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 600)) +
                                slideInVertically(
                                    animationSpec = tween(500, delayMillis = 600),
                                    initialOffsetY = { it / 3 }
                                ),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    viewModel.login(email, password)
                                }
                            },
                            enabled = loginState !is AuthViewModel.LoginState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { 
                                    shadowElevation = buttonElevation
                                },
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = buttonElevation.dp,
                                pressedElevation = 2.dp
                            )
                        ) {
                            if (loginState is AuthViewModel.LoginState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Login")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Register button
                    AnimatedVisibility(
                        visible = animationStarted.targetState,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 700)),
                        exit = fadeOut()
                    ) {
                        TextButton(onClick = onRegisterClick) {
                            Text("Don't have an account? Register")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        content()
    }
} 