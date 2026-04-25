package com.example.worldscope.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsGreenLight

@Composable
fun FavoriteItem(
    favorite: FavoriteCountryEntity,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appeared by remember(favorite.alpha2Code) { mutableStateOf(false) }
    LaunchedEffect(favorite.alpha2Code) {
        appeared = true
    }
    val appearAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "favorite_item_alpha"
    )
    val appearScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.94f,
        animationSpec = tween(durationMillis = 240),
        label = "favorite_item_scale"
    )
    val trashInteraction = remember { MutableInteractionSource() }
    val isTrashHovered by trashInteraction.collectIsHoveredAsState()
    val trashBg by animateFloatAsState(
        targetValue = if (isTrashHovered) 0.18f else 0f,
        animationSpec = tween(durationMillis = 130),
        label = "trash_hover_alpha"
    )

    Surface(
        modifier = modifier
            .alpha(appearAlpha)
            .scale(appearScale)
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                color = WsGreenLight.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 40.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(WsGreen)
            )
            AsyncImage(
                model = favorite.flagUrl,
                contentDescription = stringResource(R.string.flag_description, favorite.name),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WsGreenLight.copy(alpha = 0.25f))
                    .size(48.dp, 36.dp)
                    .testTag("favorite_item_flag")
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
                    .testTag("favorite_item_row")
                    .testTag("favorite_item_click"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = favorite.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WsGreenDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("favorite_item_name")
                    )
                }
            }
            IconButton(
                onClick = onRemoveClick,
                interactionSource = trashInteraction,
                modifier = Modifier
                    .hoverable(trashInteraction)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F).copy(alpha = trashBg))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFD32F2F).copy(alpha = if (isTrashHovered) 0.5f else 0f),
                        shape = CircleShape
                    )
                    .testTag("favorite_remove")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.remove_favorite),
                    tint = Color(0xFFC62828)
                )
            }
        }
    }
}
