package com.mukund.cenphonenew.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.mukund.cenphonenew.data.model.Order
import com.mukund.cenphonenew.ui.model.DeliveryInfo
import com.mukund.cenphonenew.ui.model.DeliveryOption
import com.mukund.cenphonenew.ui.screens.BrandsScreen
import com.mukund.cenphonenew.ui.screens.CartScreen
import com.mukund.cenphonenew.ui.screens.CheckoutScreen
import com.mukund.cenphonenew.ui.screens.HomeScreen
import com.mukund.cenphonenew.ui.screens.LoginScreen
import com.mukund.cenphonenew.ui.screens.OrderConfirmationScreen
import com.mukund.cenphonenew.ui.screens.PaymentScreen
import com.mukund.cenphonenew.ui.screens.PhoneModelScreen
import com.mukund.cenphonenew.ui.screens.ProfileScreen
import com.mukund.cenphonenew.ui.screens.RegisterScreen
import com.mukund.cenphonenew.ui.util.AnimationUtils
import com.mukund.cenphonenew.ui.viewmodel.CartViewModel
import com.mukund.cenphonenew.ui.viewmodel.CustomerViewModel
import com.mukund.cenphonenew.ui.viewmodel.OrderViewModel
import com.mukund.cenphonenew.ui.viewmodel.PhoneViewModel
import com.mukund.cenphonenew.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    customerViewModel: CustomerViewModel,
    authViewModel: AuthViewModel,
    phoneViewModel: PhoneViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel
) {
    // States
    val currentCustomer by customerViewModel.currentCustomer.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()

    // In a real app, these would be managed by a properly scoped ViewModel
    var selectedDeliveryInfo by remember { mutableStateOf<DeliveryInfo?>(null) }
    
    // Check if user is already logged in when NavGraph is first composed
    LaunchedEffect(key1 = loginState) {
        if (loginState is AuthViewModel.LoginState.Success) {
            android.util.Log.d("NavGraph", "User is logged in, ensuring customer data is loaded")
            // Force customer data to load if not already loaded
            if (currentCustomer == null) {
                customerViewModel.loadCustomerByFirebaseAuth()
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { AnimationUtils.navigationEnterTransition(this) },
        exitTransition = { AnimationUtils.navigationExitTransition(this) },
        popEnterTransition = { AnimationUtils.navigationPopEnterTransition(this) },
        popExitTransition = { AnimationUtils.navigationPopExitTransition(this) }
    ) {
        composable(route = Screen.Home.route) {
            // Check if user is already logged in when we navigate to Home
            LaunchedEffect(key1 = Unit) {
                val auth = FirebaseAuth.getInstance()
                if (auth.currentUser != null) {
                    android.util.Log.d("NavGraph", "User already logged in at Home, loading profile data")
                    customerViewModel.loadCustomerByFirebaseAuth()
                }
            }
            
            HomeScreen(
                onOrderNowClick = {
                    navController.navigate(Screen.Brands.route)
                },
                onProfileClick = {
                    if (currentCustomer != null) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }
        
        composable(route = Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // After successful login, navigate back to previous screen or home
                    // and ensure customer data is loaded
                    customerViewModel.loadCustomerByFirebaseAuth()
                    navController.popBackStack()
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Screen.Register.route) {
            RegisterScreen(
                viewModel = customerViewModel,
                onRegistrationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Brands.route) {
            BrandsScreen(
                viewModel = phoneViewModel,
                onBrandClick = { brand ->
                    navController.navigate(Screen.Models.createRoute(brand))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onProfileClick = {
                    if (currentCustomer != null) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Models.route,
            arguments = listOf(
                navArgument(Screen.Models.BRAND_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val brand = backStackEntry.arguments?.getString(Screen.Models.BRAND_ARG) ?: ""

            PhoneModelScreen(
                brand = brand,
                viewModel = phoneViewModel,
                cartViewModel = cartViewModel,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onProfileClick = {
                    if (currentCustomer != null) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }

        composable(route = Screen.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    if (currentCustomer != null) {
                        navController.navigate(Screen.Checkout.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onContinueShopping = {
                    navController.navigate(Screen.Brands.route) {
                        popUpTo(Screen.Brands.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                cartViewModel = cartViewModel,
                customer = currentCustomer,
                onBackClick = { navController.popBackStack() },
                onContinueToPayment = { deliveryInfo ->
                    selectedDeliveryInfo = deliveryInfo
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        composable(route = Screen.Payment.route) {
            PaymentScreen(
                deliveryInfo = selectedDeliveryInfo ?: DeliveryInfo(
                    name = "",
                    address = "",
                    city = "",
                    postalCode = "",
                    phoneNumber = "",
                    deliveryOption = DeliveryOption.Standard,
                    totalAmount = 0.0
                ),
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel,
                customer = currentCustomer,
                onBackClick = { navController.popBackStack() },
                onPaymentComplete = {
                    // Create order and navigate to confirmation
                    if (cartItems.isNotEmpty() && currentCustomer != null) {
                        cartItems.forEach { phone ->
                            orderViewModel.createOrder(
                                customerId = currentCustomer!!.custId,
                                productId = phone.productId,
                                totalAmount = phone.price + (selectedDeliveryInfo?.deliveryOption?.price ?: 0.0)
                            )
                        }
                        
                        // Clear cart and delivery info
                        cartViewModel.clearCart()
                        selectedDeliveryInfo = null
                        
                        navController.navigate(Screen.OrderConfirmation.route) {
                            // Clear backstack so user can't go back to payment
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                        }
                    }
                }
            )
        }

        composable(route = Screen.OrderConfirmation.route) {
            OrderConfirmationScreen(
                onContinueShopping = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onViewOrders = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                customerViewModel = customerViewModel,
                orderViewModel = orderViewModel,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    customerViewModel.logout()
                    authViewModel.logout()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
} 