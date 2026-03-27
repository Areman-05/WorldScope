package com.example.worldscope.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.domain.model.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    onBackClick: () -> Unit,
    viewModel: CountryDetailViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.country?.name ?: stringResource(R.string.detail)) },
                modifier = Modifier.testTag("country_detail_topbar"),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (state.country != null) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite() },
                            modifier = Modifier.testTag("country_detail_favorite_toggle")
                        ) {
                            Icon(
                                imageVector = if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(if (state.isFavorite) R.string.remove_favorite else R.string.add_favorite)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("country_detail_loading"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.loading))
                    }
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("country_detail_error"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = state.error!!)
                        Button(
                            onClick = { viewModel.loadCountry() },
                            modifier = Modifier.testTag("country_detail_retry")
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                state.country != null -> {
                    CountryDetailContent(
                        country = state.country!!,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    Text(
                        text = stringResource(R.string.no_data),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("country_detail_no_data")
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryDetailContent(
    country: Country,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .testTag("country_detail_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 200.dp, width = 300.dp)
                .testTag("country_detail_flag")
        )
        Text(
            text = country.name,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_capital"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.capital), style = MaterialTheme.typography.titleMedium)
            Text(country.capital ?: "-", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_region"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.region), style = MaterialTheme.typography.titleMedium)
            Text(country.region ?: "-", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_subregion"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.subregion), style = MaterialTheme.typography.titleMedium)
            Text(country.subregion ?: "-", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_population"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.population), style = MaterialTheme.typography.titleMedium)
            Text("%,d".format(country.population), style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_code2"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.country_code), style = MaterialTheme.typography.titleMedium)
            Text(country.alpha2Code ?: "-", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.country_code3), style = MaterialTheme.typography.titleMedium)
            Text(country.alpha3Code ?: "-", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_coordinates"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.coordinates), style = MaterialTheme.typography.titleMedium)
            Text(
                country.latlng?.let { "%.4f, %.4f".format(it.first, it.second) }
                    ?: stringResource(R.string.coordinates_unknown),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (country.languages.isNotEmpty()) {
            Text("${stringResource(R.string.languages)}: ${country.languages.joinToString()}", style = MaterialTheme.typography.bodyMedium)
        }
        if (country.currencies.isNotEmpty()) {
            Text("${stringResource(R.string.currencies)}: ${country.currencies.joinToString()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
