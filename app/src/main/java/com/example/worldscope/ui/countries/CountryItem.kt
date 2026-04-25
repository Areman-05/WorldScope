package com.example.worldscope.ui.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.domain.model.Country
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsGreenLight
import com.example.worldscope.ui.theme.WsGreen

@Composable
fun CountryItem(
    country: Country,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    compact: Boolean = false,
    appearDelayMs: Int = 0,
    modifier: Modifier = Modifier
) {
    val padding = if (compact) 10.dp else 16.dp
    val flagWidth = if (compact) 40.dp else 48.dp
    val flagHeight = if (compact) 30.dp else 36.dp
    val cardShape = RoundedCornerShape(if (compact) 14.dp else 16.dp)
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "country_card_scale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 1f else 3f,
        animationSpec = tween(durationMillis = 120),
        label = "country_card_elevation"
    )
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(country.alpha2Code, country.name) {
        kotlinx.coroutines.delay(appearDelayMs.toLong())
        appeared = true
    }
    val appearScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.92f,
        animationSpec = tween(durationMillis = 240),
        label = "country_appear_scale"
    )
    val appearAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "country_appear_alpha"
    )
    var favoriteAnimReady by remember(country.alpha2Code) { mutableStateOf(false) }
    var favoritePopped by remember(country.alpha2Code) { mutableStateOf(false) }
    LaunchedEffect(isFavorite) {
        if (!favoriteAnimReady) {
            favoriteAnimReady = true
            return@LaunchedEffect
        }
        favoritePopped = true
        kotlinx.coroutines.delay(150)
        favoritePopped = false
    }
    val favoriteScale by animateFloatAsState(
        targetValue = if (favoritePopped) 1.22f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "country_favorite_pop_scale"
    )
    Surface(
        modifier = modifier
            .alpha(appearAlpha)
            .scale(appearScale)
            .scale(scale)
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .testTag("country_item")
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                color = WsGreenLight.copy(alpha = 0.9f),
                shape = cardShape
            ),
        shape = cardShape,
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = elevation.dp
    ) {
        Row(
            modifier = Modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = if (compact) 34.dp else 40.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(WsGreen)
            )
            AsyncImage(
                model = country.flagUrl,
                contentDescription = stringResource(R.string.flag_description, country.name),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WsGreenLight.copy(alpha = 0.25f))
                    .size(flagWidth, flagHeight)
                    .testTag("country_item_flag")
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.name,
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WsGreenDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("country_item_name")
                )
                SpacerLine()
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.testTag("country_item_favorite_toggle")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) {
                        stringResource(R.string.remove_favorite)
                    } else {
                        stringResource(R.string.add_favorite)
                    },
                    tint = if (isFavorite) Color(0xFFF06292) else Color(0xFFDADADA),
                    modifier = Modifier.testTag(
                        if (isFavorite) "country_item_favorite_on" else "country_item_favorite_off"
                    ).scale(favoriteScale)
                )
            }
        }
    }
}

@Composable
private fun SpacerLine() {
    Surface(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(0.28f)
            .height(2.dp),
        shape = RoundedCornerShape(2.dp),
        color = WsGreenLight
    ) {}
}
