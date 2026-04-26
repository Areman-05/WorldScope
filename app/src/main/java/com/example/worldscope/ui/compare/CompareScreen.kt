@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.worldscope.ui.compare

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import com.example.worldscope.R
import com.example.worldscope.domain.model.Country
import com.example.worldscope.domain.model.WeatherInfo
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsSurfaceSoft
import java.util.Locale

@Composable
fun CompareScreen(
    viewModel: CompareViewModel = hiltViewModel(),
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
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("compare_topbar")
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
                                text = stringResource(R.string.compare_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { padding ->
        when {
            state.isLoadingList -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                        color = Color.White,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(modifier = Modifier.testTag("compare_loading_list"))
                            Text(stringResource(R.string.loading))
                        }
                    }
                }
            }
            state.listError != null -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.listError!!, modifier = Modifier.testTag("compare_list_error"))
                            Button(onClick = { viewModel.loadCountryList() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .testTag("compare_content"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.compare_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF476147)
                            )
                            CountryPicker(
                                label = stringResource(R.string.compare_country_a),
                                countries = state.allCountries,
                                selectedCode = state.codeA,
                                onSelect = viewModel::updateCodeA,
                                testTagField = "compare_pick_a",
                                testTagMenu = "compare_menu_a"
                            )
                            CountryPicker(
                                label = stringResource(R.string.compare_country_b),
                                countries = state.allCountries,
                                selectedCode = state.codeB,
                                onSelect = viewModel::updateCodeB,
                                testTagField = "compare_pick_b",
                                testTagMenu = "compare_menu_b"
                            )
                            Button(
                                onClick = { viewModel.runCompare() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("compare_run"),
                                enabled = !state.isLoadingCompare,
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = WsGreen,
                                    contentColor = Color.White
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.compare_run),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.clearComparison() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("compare_clear"),
                                enabled = !state.isLoadingCompare,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.compare_clear))
                            }
                        }
                    }
                    when (state.compareUserError) {
                        CompareUserError.NeedTwo ->
                            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                                Text(
                                    stringResource(R.string.compare_need_two),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.testTag("compare_error_user")
                                )
                            }
                        CompareUserError.SameCountry ->
                            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                                Text(
                                    stringResource(R.string.compare_same_country),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.testTag("compare_error_user")
                                )
                            }
                        null -> Unit
                    }
                    AnimatedVisibility(
                        visible = state.loadError != null,
                        enter = fadeIn(animationSpec = tween(180)),
                        exit = fadeOut(animationSpec = tween(180))
                    ) {
                        if (state.loadError != null) {
                            Text(
                                state.loadError!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("compare_error_load")
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = state.isLoadingCompare,
                        enter = fadeIn(animationSpec = tween(180)),
                        exit = fadeOut(animationSpec = tween(180))
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF8EF)),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("compare_loading_compare")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.height(20.dp))
                                Text(stringResource(R.string.loading))
                            }
                        }
                    }
                    val a = state.countryA
                    val b = state.countryB
                    Crossfade(
                        targetState = a != null && b != null,
                        animationSpec = tween(durationMillis = 260),
                        label = "compare_results_crossfade"
                    ) { ready ->
                        if (ready && a != null && b != null) {
                            CompareColumn(
                                left = a,
                                right = b,
                                gdpLeft = state.economicA?.gdpUsd,
                                gdpRight = state.economicB?.gdpUsd,
                                inflLeft = state.economicA?.inflationPercent,
                                inflRight = state.economicB?.inflationPercent,
                                weatherLeft = state.weatherA,
                                weatherRight = state.weatherB
                            )
                        }
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun CountryPicker(
    label: String,
    countries: List<Country>,
    selectedCode: String?,
    onSelect: (String?) -> Unit,
    testTagField: String,
    testTagMenu: String
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = countries.firstOrNull { it.alpha2Code == selectedCode }?.name ?: ""
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WsGreenDark,
                focusedLabelColor = WsGreenDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag(testTagField)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.testTag(testTagMenu)
        ) {
            countries.forEach { c ->
                val code = c.alpha2Code ?: return@forEach
                DropdownMenuItem(
                    text = { Text(c.name) },
                    onClick = {
                        onSelect(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CompareColumn(
    left: Country,
    right: Country,
    gdpLeft: Double?,
    gdpRight: Double?,
    inflLeft: Double?,
    inflRight: Double?,
    weatherLeft: WeatherInfo?,
    weatherRight: WeatherInfo?
) {
    Card(
        modifier = Modifier.testTag("compare_results"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(R.string.compare_results),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = WsGreenDark
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = left.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = WsGreenDark
                )
                Text(
                    text = right.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = WsGreenDark
                )
            }
            RowCompareMetric(
                label = stringResource(R.string.population),
                leftValue = left.population.toDouble(),
                rightValue = right.population.toDouble(),
                leftText = "%,d".format(Locale.US, left.population),
                rightText = "%,d".format(Locale.US, right.population),
                testTag = "compare_metric_population"
            )
            RowCompareRow(
                label = stringResource(R.string.area),
                left = left.areaKm2?.let { String.format(Locale.US, "%,.0f km2", it) } ?: "-",
                right = right.areaKm2?.let { String.format(Locale.US, "%,.0f km2", it) } ?: "-"
            )
            RowCompareRow(
                label = stringResource(R.string.capital),
                left = left.capital ?: "-",
                right = right.capital ?: "-"
            )
            RowCompareRow(
                label = stringResource(R.string.region),
                left = left.region ?: "-",
                right = right.region ?: "-"
            )
            RowCompareMetric(
                label = stringResource(R.string.gdp_usd),
                leftValue = gdpLeft,
                rightValue = gdpRight,
                leftText = gdpLeft?.let { String.format(Locale.US, "%,.0f", it) } ?: "-",
                rightText = gdpRight?.let { String.format(Locale.US, "%,.0f", it) } ?: "-",
                testTag = "compare_metric_gdp"
            )
            RowCompareRow(
                label = stringResource(R.string.inflation),
                left = inflLeft?.let { String.format(Locale.US, "%.2f %%", it) } ?: "-",
                right = inflRight?.let { String.format(Locale.US, "%.2f %%", it) } ?: "-"
            )
            RowCompareRow(
                label = stringResource(R.string.currencies),
                left = left.currencyCodes.firstOrNull() ?: "-",
                right = right.currencyCodes.firstOrNull() ?: "-"
            )
            RowCompareRow(
                label = stringResource(R.string.weather),
                left = weatherLeft?.let { formatWeather(it) } ?: "-",
                right = weatherRight?.let { formatWeather(it) } ?: "-"
            )
        }
    }
}

@Composable
private fun RowCompareRow(
    label: String,
    left: String,
    right: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF7FBF7),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = WsGreenDark)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(left, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Text(right, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RowCompareMetric(
    label: String,
    leftValue: Double?,
    rightValue: Double?,
    leftText: String,
    rightText: String,
    testTag: String
) {
    val l = (leftValue ?: 0.0).coerceAtLeast(0.0)
    val r = (rightValue ?: 0.0).coerceAtLeast(0.0)
    val max = maxOf(l, r, 1.0)
    val leftProgress = (l / max).toFloat()
    val rightProgress = (r / max).toFloat()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF7FBF7),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
            .testTag(testTag)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = WsGreenDark)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(leftText, fontWeight = FontWeight.SemiBold)
                LinearProgressIndicator(
                    progress = { leftProgress },
                    color = WsGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .testTag("${testTag}_left")
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(rightText, fontWeight = FontWeight.SemiBold)
                LinearProgressIndicator(
                    progress = { rightProgress },
                    color = WsGreenDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .testTag("${testTag}_right")
                )
            }
        }
    }
}

private fun formatWeather(info: WeatherInfo): String {
    val temp = info.temperatureCelsius?.let { String.format(Locale.US, "%.1f C", it) } ?: "-"
    val label = info.description ?: info.condition ?: ""
    return if (label.isBlank()) temp else "$temp · $label"
}
