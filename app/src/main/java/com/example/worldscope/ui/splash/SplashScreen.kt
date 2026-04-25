package com.example.worldscope.ui.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worldscope.R
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import kotlinx.coroutines.delay

private const val SplashDurationMs = 3000L

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(SplashDurationMs)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite_transition")

    val bgShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_bg_shift"
    )
    val planetRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000),
            repeatMode = RepeatMode.Restart
        ),
        label = "splash_planet_rotation"
    )
    val planetPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1350),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_planet_pulse"
    )
    val ringPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_ring_pulse"
    )

    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { reveal = true }

    val titleAlpha by animateFloatAsState(
        targetValue = if (reveal) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "splash_title_alpha"
    )
    val titleScale by animateFloatAsState(
        targetValue = if (reveal) 1f else 0.75f,
        animationSpec = tween(durationMillis = 900),
        label = "splash_title_scale"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (reveal) 0.92f else 0f,
        animationSpec = tween(durationMillis = 1300),
        label = "splash_subtitle_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        WsGreenDark.copy(alpha = 0.98f),
                        WsGreen.copy(alpha = 0.96f),
                        Color(0xFF43A047).copy(alpha = 0.95f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(bgShift * 700f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, 1400f - (bgShift * 500f))
                )
            )
            .testTag("splash_screen")
    ) {
        SparkleLayer()

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(230.dp)
                .scale(ringPulse)
                .alpha(0.26f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFF59D), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .scale(planetPulse)
                    .rotate(planetRotation)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFF59D),
                                Color(0xFFFBC02D)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    tint = WsGreenDark,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .scale(titleScale)
                    .testTag("splash_title")
            )
            Text(
                text = stringResource(R.string.splash_subtitle),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .alpha(subtitleAlpha)
                    .testTag("splash_subtitle")
            )
        }
    }
}

@Composable
private fun SparkleLayer() {
    val sparkleTransition = rememberInfiniteTransition(label = "splash_sparkles")

    val sparkleA by sparkleTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha_a"
    )
    val sparkleB by sparkleTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha_b"
    )
    val floatY by sparkleTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_float_y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .graphicsLayer {
                    alpha = sparkleA
                    translationY = floatY
                }
                .size(12.dp)
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    alpha = sparkleB
                    translationY = -floatY
                }
                .size(9.dp)
                .background(Color(0xFFFFF59D).copy(alpha = 0.9f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    alpha = ((sparkleA + sparkleB) / 2f).coerceIn(0f, 1f)
                    translationY = floatY * 0.7f
                }
                .size(10.dp)
                .background(Color.White.copy(alpha = 0.75f), CircleShape)
        )
    }
}
