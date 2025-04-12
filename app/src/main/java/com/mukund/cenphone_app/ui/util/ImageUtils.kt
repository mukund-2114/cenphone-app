package com.mukund.cenphonenew.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.mukund.cenphonenew.R
import java.io.IOException
import java.io.InputStream

/**
 * Utility functions for handling images in the app
 */
object ImageUtils {
    
    /**
     * Maps a brand name to its image path in assets
     */
    fun getBrandLogoPath(brand: String): String {
        return when (brand) {
            "Apple" -> "images/apple_logo.png"
            "Samsung" -> "images/samsung_logo.png"
            "Google" -> "images/google_logo.png"
            "Xiaomi" -> "images/Mi-logo-01.png"
            "BlackBerry" -> "images/blackberry_logo.png"
            else -> "images/apple_logo.png" // Default
        }
    }
    
    /**
     * Maps a phone make (brand) to its model image path in assets
     */
    fun getPhoneModelPath(phoneMake: String): String {
        return when (phoneMake) {
            "Apple" -> "images/Iphone.png"
            "Samsung" -> "images/samsungModel.png"
            "Google" -> "images/googleModel.png"
            "Xiaomi" -> "images/Xiaomi model.jpg"
            "BlackBerry" -> "images/blackberrryModel.png"
            else -> "images/Iphone.png" // Default
        }
    }
    
    /**
     * Maps a payment method to its image path in assets
     */
    fun getPaymentMethodPath(method: String): String {
        return when (method) {
            "Credit Card" -> "images/creditcard.png"
            "Apple Pay" -> "images/applepay.png"
            "Google Pay" -> "images/gpay.png"
            "Bank Transfer" -> "images/bankcards.png"
            else -> "images/creditcard.png" // Default
        }
    }
    
    /**
     * Get confirmation success image path
     */
    fun getSuccessImagePath(): String {
        return "images/tick.png"
    }
    
    /**
     * Get app logo path
     */
    fun getAppLogoPath(): String {
        return "images/CenphoneLogo.png"
    }
    
    /**
     * Get app background path
     */
    fun getAppBackgroundPath(): String {
        return "images/CenphoneBg.png"
    }
    
    /**
     * Load image from assets as ImageBitmap
     */
    @Composable
    fun loadImageFromAssets(path: String): ImageBitmap? {
        val context = LocalContext.current
        return try {
            val inputStream: InputStream = context.assets.open(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap.asImageBitmap()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Load image from assets as Bitmap
     */
    fun loadBitmapFromAssets(context: Context, path: String): Bitmap? {
        return try {
            val inputStream: InputStream = context.assets.open(path)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
} 