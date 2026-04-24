package com.example.worldscope.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.domain.model.Country
import com.example.worldscope.domain.model.EconomicInfo
import com.example.worldscope.domain.model.WikiSummary
import com.example.worldscope.domain.model.WeatherInfo
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsGreenLight
import com.example.worldscope.ui.theme.WsSurfaceSoft
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
                    .testTag("country_detail_topbar")
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = null,
                                tint = Color(0xFFFFF59D)
                            )
                            Text(
                                state.country?.name ?: stringResource(R.string.detail),
                                modifier = Modifier.testTag("country_detail_title"),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("country_detail_back")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
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
                                    tint = Color.White,
                                    modifier = Modifier.testTag(
                                        if (state.isFavorite) "country_detail_favorite_on" else "country_detail_favorite_off"
                                    )
                                )
                            }
                        }
                    }
                )
            }
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
                        economicInfo = state.economicInfo,
                        wikiSummary = state.wikiSummary,
                        isLoadingWeather = state.isLoadingWeather,
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
    economicInfo: EconomicInfo?,
    wikiSummary: WikiSummary?,
    isLoadingWeather: Boolean,
    isLoadingEconomic: Boolean,
    isLoadingWiki: Boolean,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .testTag("country_detail_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("country_detail_flag"),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 3.dp
        ) {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 200.dp, width = 300.dp)
            )
        }
        Text(
            text = country.name,
            style = MaterialTheme.typography.headlineMedium,
            color = WsGreenDark,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        DetailSectionCard(
            title = stringResource(R.string.detail),
            icon = Icons.Filled.LocationOn
        ) {
            DetailMetricRow(
                label = stringResource(R.string.capital),
                value = country.capital ?: "-",
                tag = "country_detail_capital_value"
            )
            DetailMetricRow(
                label = stringResource(R.string.region),
                value = country.region ?: "-",
                tag = "country_detail_region_value"
            )
            DetailMetricRow(
                label = stringResource(R.string.subregion),
                value = country.subregion ?: "-",
                tag = "country_detail_subregion_value"
            )
            DetailMetricRow(
                label = stringResource(R.string.population),
                value = "%,d".format(country.population),
                tag = "country_detail_population_value"
            )
            DetailMetricRow(
                label = stringResource(R.string.area),
                value = country.areaKm2?.let { "%.0f km2".format(it) } ?: "-",
                tag = "country_detail_area_value"
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
            DetailSectionCard(
                title = stringResource(R.string.languages),
                icon = Icons.Filled.Language
            ) {
                Text(
                    country.languages.joinToString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("country_detail_languages")
                )
            }
        }
        if (country.currencies.isNotEmpty()) {
            DetailSectionCard(
                title = stringResource(R.string.currencies),
                icon = Icons.Filled.Payments
            ) {
                Text(
                    country.currencies.joinToString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("country_detail_currencies")
                )
            }
        }
        DetailSectionCard(
            title = stringResource(R.string.economy),
            icon = Icons.Filled.QueryStats,
            tag = "country_detail_economy_title"
        ) {
            if (isLoadingEconomic) {
                Text(stringResource(R.string.loading))
            } else if (economicInfo != null) {
                DetailMetricRow(
                    label = stringResource(R.string.gdp_usd),
                    value = formatGdpUsd(economicInfo.gdpUsd),
                    tag = "country_detail_economy_gdp"
                )
                DetailMetricRow(
                    label = stringResource(R.string.inflation),
                    value = formatInflation(economicInfo.inflationPercent),
                    tag = "country_detail_economy_inflation"
                )
            } else {
                Text("Sin datos economicos disponibles por ahora")
            }
        }
        DetailSectionCard(
            title = stringResource(R.string.wikipedia),
            icon = Icons.Filled.MenuBook,
            tag = "country_detail_wiki_title"
        ) {
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
                Text("Sin resumen disponible en Wikipedia para este pais")
            }
        }
        DetailSectionCard(
            title = "Clima actualmente",
            icon = Icons.Filled.Public,
            tag = "country_detail_weather_title"
        ) {
            if (isLoadingWeather) {
                Text(stringResource(R.string.loading))
            } else if (weatherInfo != null) {
                DetailMetricRow(
                    label = stringResource(R.string.temperature),
                    value = weatherInfo.temperatureCelsius?.toString() ?: "-",
                    tag = "country_detail_weather_temp"
                )
                DetailMetricRow(
                    label = stringResource(R.string.feels_like),
                    value = weatherInfo.feelsLikeCelsius?.toString() ?: "-",
                    tag = "country_detail_weather_feels_like"
                )
                DetailMetricRow(
                    label = stringResource(R.string.humidity),
                    value = weatherInfo.humidity?.toString() ?: "-",
                    tag = "country_detail_weather_humidity"
                )
                Text(
                    weatherInfo.description ?: weatherInfo.condition ?: "-",
                    modifier = Modifier.testTag("country_detail_weather_description")
                )
            } else {
                Text(stringResource(R.string.no_data))
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: ImageVector,
    tag: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = WsGreenDark
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = WsGreenDark,
                    fontWeight = FontWeight.SemiBold,
                    modifier = if (tag != null) Modifier.testTag(tag) else Modifier
                )
            }
            content()
        }
    }
}

@Composable
private fun DetailMetricRow(
    label: String,
    value: String,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag(tag))
    }
}

private fun formatGdpUsd(value: Double?): String =
    value?.let { v -> String.format(Locale.US, "%,.0f USD", v) } ?: "-"

private fun formatInflation(value: Double?): String =
    value?.let { v -> String.format(Locale.US, "%.2f %%", v) } ?: "-"
