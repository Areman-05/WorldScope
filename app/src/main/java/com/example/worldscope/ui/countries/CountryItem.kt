package com.example.worldscope.ui.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.domain.model.Country

@Composable
fun CountryItem(
    country: Country,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("country_item")
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = stringResource(R.string.flag_description, country.name),
            modifier = Modifier
                .size(48.dp, 36.dp)
                .testTag("country_item_flag")
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = country.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("country_item_name")
            )
            Text(
                text = "%,d".format(country.population),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("country_item_population")
            )
        }
    }
}
