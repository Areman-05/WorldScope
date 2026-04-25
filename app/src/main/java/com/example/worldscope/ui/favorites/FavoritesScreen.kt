package com.example.worldscope.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsSurfaceSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onCountryClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = WsSurfaceSoft,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(WsGreen, WsGreenDark)
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("favorites_topbar")
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        val planetAnim = rememberInfiniteTransition(label = "favorites_planet_anim")
                        val rotation by planetAnim.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 9000),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "favorites_planet_rotation"
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .rotate(rotation)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFFF59D),
                                                Color(0xFFFBC02D)
                                            )
                                        )
                                    )
                                    .padding(7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = null,
                                    tint = WsGreenDark
                                )
                            }
                            Text(
                                text = stringResource(R.string.favorites),
                                color = Color.White,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {},
                    actions = {}
                )
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = !state.hasLoaded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_loading"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        stringResource(R.string.loading),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        color = WsGreenDark
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = state.hasLoaded && state.favorites.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_empty_container"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    tonalElevation = 2.dp,
                    modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 22.dp, vertical = 20.dp)
                            .testTag("favorites_empty_content"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💚",
                            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            stringResource(R.string.no_favorites),
                            modifier = Modifier.testTag("favorites_empty"),
                            color = WsGreenDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .testTag("favorites_go_countries"),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = WsGreenDark,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                stringResource(R.string.countries),
                                modifier = Modifier.testTag("favorites_go_countries_text")
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.hasLoaded && state.favorites.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_list")
            ) {
                items(state.favorites, key = { it.alpha2Code }) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onClick = {
                            val code = favorite.alpha2Code
                            if (code.isNotBlank()) onCountryClick(code)
                        },
                        onRemoveClick = { viewModel.removeFavorite(favorite.alpha2Code) }
                    )
                }
            }
        }
    }
}
