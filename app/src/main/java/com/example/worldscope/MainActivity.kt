package com.example.worldscope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.worldscope.navigation.AppScaffold
import com.example.worldscope.ui.splash.SplashScreen
import com.example.worldscope.ui.theme.WorldScopeTheme
import dagger.hilt.android.AndroidEntryPoint

/** Main entry point for WorldScope. Hosts Compose navigation and applies WorldScopeTheme. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorldScopeTheme {
                var showSplash by remember { mutableStateOf(true) }
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_surface"),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(
                            visible = !showSplash,
                            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                            exit = fadeOut(animationSpec = tween(durationMillis = 240))
                        ) {
                            AppScaffold()
                        }
                        AnimatedVisibility(
                            visible = showSplash,
                            enter = fadeIn(animationSpec = tween(durationMillis = 260)),
                            exit = fadeOut(animationSpec = tween(durationMillis = 520))
                        ) {
                            SplashScreen(onFinished = { showSplash = false })
                        }
                    }
                }
            }
        }
    }
}
