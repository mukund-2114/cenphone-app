package com.mukund.cenphonenew.ui.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.*
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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

    /**
     * Creates a pulsing animation that scales the element up and down
     */
    fun Modifier.pulseAnimation(
        pulseMagnitude: Float = 1.2f,
        duration: Int = 1000
    ) = composed {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = duration
                    1f at 0
                    pulseMagnitude at duration / 2
                    1f at duration
                },
                repeatMode = RepeatMode.Restart
            ), label = "scale"
        )

        this.scale(scale)
    }

    /**
     * Animates the element with a staggered entrance animation
     */
    fun Modifier.staggeredEntrance(
        delay: Int = 0,
        duration: Int = 300,
        offsetX: Int = 0,
        offsetY: Int = 100
    ) = composed {
        var animationPlayed by remember { mutableStateOf(false) }
        val animatedOffset by animateIntOffsetAsState(
            targetValue = if (animationPlayed) IntOffset(0, 0) else IntOffset(offsetX, offsetY),
            animationSpec = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = EaseOutQuint
            ), label = "offset"
        )
        val animatedAlpha by animateFloatAsState(
            targetValue = if (animationPlayed) 1f else 0f,
            animationSpec = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = EaseOutQuint
            ), label = "alpha"
        )

        LaunchedEffect(true) {
            delay(50) // Small initial delay before starting animation
            animationPlayed = true
        }

        this
            .offset { animatedOffset }
            .alpha(animatedAlpha)
    }
    
    /**
     * Animate the element with a wave motion effect
     */
    fun Modifier.waveEffect(
        amplitude: Float = 15f,
        frequency: Float = 0.05f,
        speed: Float = 2f
    ) = composed {
        val infiniteTransition = rememberInfiniteTransition(label = "wave")
        val phase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * Math.PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = (1000 / speed).toInt()),
                repeatMode = RepeatMode.Restart
            ), label = "phase"
        )

        this.graphicsLayer {
            translationY = sin(phase + (frequency * size.width)) * amplitude
        }
    }
    
    /**
     * Creates a bouncy effect for interactive buttons
     */
    fun Modifier.bounceOnHover() = composed {
        var isHovered by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isHovered) 1.10f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ), label = "bounce"
        )
        
        this
            .scale(scale)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isHovered = event.type == PointerEventType.Enter
                    }
                }
            }
    }
    
    /**
     * Creates a payment button effect with subtle glowing and pulsing
     */
    fun Modifier.paymentButtonEffect() = composed {
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ), label = "press"
        )
        
        val shape = RoundedCornerShape(8.dp)
        
        val elevation by animateDpAsState(
            targetValue = if (isPressed) 8.dp else 2.dp,
            animationSpec = tween(150), 
            label = "elevation"
        )
        
        this
            .scale(scale)
            .shadow(elevation = elevation, shape = shape, clip = true)
            .clip(shape)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isPressed = event.type == PointerEventType.Press
                        if (event.type == PointerEventType.Release) {
                            isPressed = false
                        }
                    }
                }
            }
    }
}

/**
 * Button states for the bounce click animation
 */
private enum class ButtonState {
    Idle, Pressed
} 