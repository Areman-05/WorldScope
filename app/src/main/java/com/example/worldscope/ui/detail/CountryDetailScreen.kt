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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.domain.model.Country
import com.example.worldscope.domain.model.EconomicInfo
import com.example.worldscope.domain.model.WikiSummary
import com.example.worldscope.domain.model.ExchangeInfo
import com.example.worldscope.domain.model.WeatherInfo
import java.util.Locale

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
                title = {
                    Text(
                        state.country?.name ?: stringResource(R.string.detail),
                        modifier = Modifier.testTag("country_detail_title")
                    )
                },
                modifier = Modifier.testTag("country_detail_topbar"),
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("country_detail_back")
                    ) {
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
                                contentDescription = stringResource(if (state.isFavorite) R.string.remove_favorite else R.string.add_favorite),
                                modifier = Modifier.testTag(
                                    if (state.isFavorite) "country_detail_favorite_on" else "country_detail_favorite_off"
                                )
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
                        Text(
                            text = state.error!!,
                            modifier = Modifier.testTag("country_detail_error_text")
                        )
                        Button(
                            onClick = { viewModel.loadCountry() },
                            modifier = Modifier.testTag("country_detail_retry")
                        ) {
                            Text(
                                stringResource(R.string.retry),
                                modifier = Modifier.testTag("country_detail_retry_text")
                            )
                        }
                    }
                }
                state.country != null -> {
                    CountryDetailContent(
                        country = state.country!!,
                        weatherInfo = state.weatherInfo,
                        exchangeInfo = state.exchangeInfo,
                        economicInfo = state.economicInfo,
                        wikiSummary = state.wikiSummary,
                        isLoadingWeather = state.isLoadingWeather,
                        isLoadingExchange = state.isLoadingExchange,
                        isLoadingEconomic = state.isLoadingEconomic,
                        isLoadingWiki = state.isLoadingWiki,
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
    weatherInfo: WeatherInfo?,
    exchangeInfo: ExchangeInfo?,
    economicInfo: EconomicInfo?,
    wikiSummary: WikiSummary?,
    isLoadingWeather: Boolean,
    isLoadingExchange: Boolean,
    isLoadingEconomic: Boolean,
    isLoadingWiki: Boolean,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
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
            Text(
                country.capital ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_capital_value")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_region"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.region), style = MaterialTheme.typography.titleMedium)
            Text(
                country.region ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_region_value")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_subregion"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.subregion), style = MaterialTheme.typography.titleMedium)
            Text(
                country.subregion ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_subregion_value")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_population"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.population), style = MaterialTheme.typography.titleMedium)
            Text(
                "%,d".format(country.population),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_population_value")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_area"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.area), style = MaterialTheme.typography.titleMedium)
            Text(
                country.areaKm2?.let { "%.0f km2".format(it) } ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_area_value")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_code2"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.country_code), style = MaterialTheme.typography.titleMedium)
            Text(
                country.alpha2Code ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_code2_value")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_code3"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.country_code3), style = MaterialTheme.typography.titleMedium)
            Text(
                country.alpha3Code ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_code3_value")
            )
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
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("country_detail_coordinates_value")
            )
        }
        country.latlng?.let { latlng ->
            Button(
                onClick = { uriHandler.openUri("geo:${latlng.first},${latlng.second}") },
                modifier = Modifier.testTag("country_detail_open_map")
            ) {
                Text(stringResource(R.string.open_map))
            }
        }
        if (country.languages.isNotEmpty()) {
            Text(
                "${stringResource(R.string.languages)}: ${country.languages.joinToString()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("country_detail_languages")
            )
        }
        if (country.currencies.isNotEmpty()) {
            Text(
                "${stringResource(R.string.currencies)}: ${country.currencies.joinToString()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("country_detail_currencies")
            )
        }
        Text(
            text = stringResource(R.string.economy),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("country_detail_economy_title")
        )
        if (isLoadingEconomic) {
            Text(stringResource(R.string.loading))
        } else if (economicInfo != null) {
            Text(
                "${stringResource(R.string.gdp_usd)}: ${formatGdpUsd(economicInfo.gdpUsd)}",
                modifier = Modifier.testTag("country_detail_economy_gdp")
            )
            Text(
                "${stringResource(R.string.inflation)}: ${formatInflation(economicInfo.inflationPercent)}",
                modifier = Modifier.testTag("country_detail_economy_inflation")
            )
        } else {
            Text(stringResource(R.string.no_data))
        }
        Text(
            text = stringResource(R.string.wikipedia),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("country_detail_wiki_title")
        )
        if (isLoadingWiki) {
            Text(stringResource(R.string.loading))
        } else if (wikiSummary?.extract != null) {
            wikiSummary.thumbnailUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("country_detail_wiki_thumb")
                )
            }
            Text(
                text = wikiSummary.extract,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("country_detail_wiki_extract")
            )
            Text(
                text = stringResource(R.string.wikipedia_source),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.testTag("country_detail_wiki_source")
            )
        } else {
            Text(stringResource(R.string.no_data))
        }
        Text(
            text = stringResource(R.string.weather),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("country_detail_weather_title")
        )
        if (isLoadingWeather) {
            Text(stringResource(R.string.loading))
        } else if (weatherInfo != null) {
            Text(
                "${stringResource(R.string.temperature)}: ${weatherInfo.temperatureCelsius ?: "-"}",
                modifier = Modifier.testTag("country_detail_weather_temp")
            )
            Text(
                "${stringResource(R.string.feels_like)}: ${weatherInfo.feelsLikeCelsius ?: "-"}",
                modifier = Modifier.testTag("country_detail_weather_feels_like")
            )
            Text(
                "${stringResource(R.string.humidity)}: ${weatherInfo.humidity ?: "-"}",
                modifier = Modifier.testTag("country_detail_weather_humidity")
            )
            Text(
                weatherInfo.description ?: weatherInfo.condition ?: "-",
                modifier = Modifier.testTag("country_detail_weather_description")
            )
        } else {
            Text(stringResource(R.string.no_data))
        }
        Text(
            text = stringResource(R.string.exchange_rate),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("country_detail_exchange_title")
        )
        if (isLoadingExchange) {
            Text(stringResource(R.string.loading))
        } else if (exchangeInfo != null) {
            Text(
                stringResource(R.string.from_to_format, exchangeInfo.baseCode, exchangeInfo.targetCode),
                modifier = Modifier.testTag("country_detail_exchange_pair")
            )
            Text(
                exchangeInfo.rate?.toString() ?: stringResource(R.string.unknown_rate),
                modifier = Modifier.testTag("country_detail_exchange_rate")
            )
        } else {
            Text(stringResource(R.string.no_data))
        }
    }
}

private fun formatGdpUsd(value: Double?): String =
    value?.let { v -> String.format(Locale.US, "%,.0f USD", v) } ?: "-"

private fun formatInflation(value: Double?): String =
    value?.let { v -> String.format(Locale.US, "%.2f %%", v) } ?: "-"
