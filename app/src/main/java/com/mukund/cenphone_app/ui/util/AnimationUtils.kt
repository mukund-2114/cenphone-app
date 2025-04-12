package com.mukund.cenphonenew.ui.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.sin

/**
 * Utility class providing reusable animations and animation-related extensions
 * for consistent animation effects throughout the app.
 */
object AnimationUtils {
    
    // Animation durations
    const val ANIMATION_DURATION_SHORT = 300
    const val ANIMATION_DURATION_MEDIUM = 500
    const val ANIMATION_DURATION_LONG = 800
    
    /**
     * Creates a bouncy scale animation for buttons and interactive elements
     */
    @Composable
    fun pulsingAnimation(
        initialValue: Float = 0.95f,
        targetValue: Float = 1.05f,
        duration: Int = 1500
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
        val scale by infiniteTransition.animateFloat(
            initialValue = initialValue,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
        return scale
    }
    
    /**
     * Creates a rotation animation that continuously rotates an element
     */
    @Composable
    fun rotationAnimation(
        duration: Int = 3000
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = EaseInOut)
            ),
            label = "rotate"
        )
        return rotation
    }
    
    /**
     * Creates a subtle wave animation for text or UI elements
     */
    @Composable
    fun waveAnimation(
        amplitude: Float = 2f,
        duration: Int = 4000
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "wave")
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration)
            ),
            label = "waveProgress"
        )
        
        return sin(Math.toRadians(progress.toDouble()).toFloat()) * amplitude
    }
    
    /**
     * Extension function for clickable elements with press animation effect
     */
    fun Modifier.bounceClick(onClick: () -> Unit) = composed {
        var buttonState by remember { mutableStateOf(ButtonState.Idle) }
        
        val scale = when (buttonState) {
            ButtonState.Pressed -> 0.9f
            else -> 1f
        }
        
        this
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .pointerInput(buttonState) {
                awaitPointerEventScope {
                    buttonState = when (buttonState) {
                        ButtonState.Idle -> {
                            awaitFirstDown(false)
                            ButtonState.Pressed
                        }
                        ButtonState.Pressed -> {
                            waitForUpOrCancellation()
                            ButtonState.Idle
                        }
                    }
                }
            }
    }
    
    /**
     * Extension function to add a pulsing effect to an element
     */
    fun Modifier.pulsing(
        initialScale: Float = 0.95f,
        targetScale: Float = 1.05f,
        duration: Int = 1500
    ) = composed {
        val scale = pulsingAnimation(
            initialValue = initialScale,
            targetValue = targetScale,
            duration = duration
        )
        
        this.scale(scale)
    }
    
    /**
     * Extension function to add a floating effect to elements
     */
    fun Modifier.floating(
        offsetY: Float = 10f,
        duration: Int = 2000
    ) = composed {
        val infiniteTransition = rememberInfiniteTransition(label = "float")
        val translationY by infiniteTransition.animateFloat(
            initialValue = -offsetY / 2,
            targetValue = offsetY / 2,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floating"
        )
        
        this.graphicsLayer {
            this.translationY = translationY
        }
    }
    
    /**
     * Create a standard enter transition for screens
     */
    fun enterTransition(): EnterTransition {
        return slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialOffsetY = { it / 3 }
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION_MEDIUM)
        )
    }
    
    /**
     * Create a standard exit transition for screens
     */
    fun exitTransition(): ExitTransition {
        return slideOutVertically(
            animationSpec = tween(ANIMATION_DURATION_MEDIUM),
            targetOffsetY = { -it / 3 }
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION_SHORT)
        )
    }
    
    /**
     * Standard navigation enter transition (for NavHost)
     */
    fun navigationEnterTransition(scope: AnimatedContentTransitionScope<*>): EnterTransition {
        return slideInHorizontally(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialOffsetX = { it }
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION_MEDIUM)
        )
    }
    
    /**
     * Standard navigation exit transition (for NavHost)
     */
    fun navigationExitTransition(scope: AnimatedContentTransitionScope<*>): ExitTransition {
        return slideOutHorizontally(
            animationSpec = tween(ANIMATION_DURATION_MEDIUM),
            targetOffsetX = { -it }
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION_SHORT)
        )
    }
    
    /**
     * Standard navigation pop enter transition (for NavHost)
     */
    fun navigationPopEnterTransition(scope: AnimatedContentTransitionScope<*>): EnterTransition {
        return slideInHorizontally(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialOffsetX = { -it }
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION_MEDIUM)
        )
    }
    
    /**
     * Standard navigation pop exit transition (for NavHost)
     */
    fun navigationPopExitTransition(scope: AnimatedContentTransitionScope<*>): ExitTransition {
        return slideOutHorizontally(
            animationSpec = tween(ANIMATION_DURATION_MEDIUM),
            targetOffsetX = { it }
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION_SHORT)
        )
    }
}

/**
 * Button states for the bounce click animation
 */
private enum class ButtonState {
    Idle, Pressed
} 