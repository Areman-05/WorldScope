package com.example.worldscope.ui.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun CountryItem(
    country: Country,
    onClick: () -> Unit,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val padding = if (compact) 10.dp else 16.dp
    val flagWidth = if (compact) 40.dp else 48.dp
    val flagHeight = if (compact) 30.dp else 36.dp
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("country_item")
            .padding(horizontal = 10.dp, vertical = 6.dp),
        shape = RoundedCornerShape(if (compact) 14.dp else 16.dp),
        color = androidx.compose.ui.graphics.Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = stringResource(R.string.flag_description, country.name),
                modifier = Modifier
                    .size(flagWidth, flagHeight)
                    .testTag("country_item_flag")
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.name,
                    style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = WsGreenDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("country_item_name")
                )
                SpacerLine()
                Text(
                    text = "%,d".format(country.population),
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Black,
                    modifier = Modifier.testTag("country_item_population")
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
