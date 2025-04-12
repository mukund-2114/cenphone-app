package com.mukund.cenphonenew.ui.navigation

/**
 * Class to define navigation destinations
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Brands : Screen("brands")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")

    object Models : Screen("models/{brand}") {
        const val BRAND_ARG = "brand"
        fun createRoute(brand: String) = "models/$brand"
    }

    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Payment : Screen("payment")
    object OrderConfirmation : Screen("order_confirmation")
} 