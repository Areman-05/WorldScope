package com.example.worldscope.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.data.local.entity.FavoriteCountryEntity

@Composable
fun FavoriteItem(
    favorite: FavoriteCountryEntity,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick)
                .testTag("favorite_item_row")
                .testTag("favorite_item_click"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = favorite.flagUrl,
                contentDescription = stringResource(R.string.flag_description, favorite.name),
                modifier = Modifier
                    .size(48.dp, 36.dp)
                    .testTag("favorite_item_flag")
            )
            Column {
                Text(
                    text = favorite.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "%,d".format(favorite.population),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("favorite_item_population")
                )
            }
        }
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier.testTag("favorite_remove")
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.remove_favorite)
            )
        }
    }
}
