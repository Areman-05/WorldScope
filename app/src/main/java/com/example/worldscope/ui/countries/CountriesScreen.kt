package com.example.worldscope.ui.countries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R

@Composable
fun CountriesScreen(
    onCountryClick: (String) -> Unit = {},
    viewModel: CountriesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedSort by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                modifier = Modifier.testTag("countries_topbar")
            )
        }
        ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("countries_search"),
                placeholder = { Text(stringResource(R.string.search_country)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            Text(
                text = stringResource(R.string.region),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .testTag("countries_region_label")
            )
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = expandedRegion,
                onExpandedChange = {
                    if (state.availableRegions.isNotEmpty()) expandedRegion = !expandedRegion
                    else expandedRegion = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = state.regionFilter ?: stringResource(R.string.all_regions),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("countries_region"),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = expandedRegion,
                    onDismissRequest = { expandedRegion = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.all_regions)) },
                        onClick = {
                            viewModel.updateRegionFilter(null)
                            expandedRegion = false
                        }
                    )
                    if (state.availableRegions.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_regions)) },
                            onClick = { },
                            enabled = false,
                            modifier = Modifier.testTag("countries_region_empty")
                        )
                    } else {
                        state.availableRegions.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region) },
                                onClick = {
                                    viewModel.updateRegionFilter(region)
                                    expandedRegion = false
                                }
                            )
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.sort_by),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .testTag("countries_sort_label")
            )
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = expandedSort,
                onExpandedChange = { expandedSort = !expandedSort },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                val sortLabel = when (state.sortMode) {
                    SortMode.NAME -> stringResource(R.string.sort_name)
                    SortMode.POPULATION -> stringResource(R.string.sort_population)
                }
                OutlinedTextField(
                    value = sortLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSort)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("countries_sort"),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_name)) },
                        onClick = {
                            viewModel.updateSortMode(SortMode.NAME)
                            expandedSort = false
                        },
                        modifier = Modifier.testTag("countries_sort_name")
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_population)) },
                        onClick = {
                            viewModel.updateSortMode(SortMode.POPULATION)
                            expandedSort = false
                        },
                        modifier = Modifier.testTag("countries_sort_population")
                    )
                }
            }
            if (state.hasLoaded && state.hasActiveFilters) {
                OutlinedButton(
                    onClick = {
                        viewModel.clearFilters()
                        expandedRegion = false
                        expandedSort = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .testTag("countries_clear_filters")
                ) {
                    Text(stringResource(R.string.clear_filters))
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                when {
                    state.isLoading -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("countries_loading"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                stringResource(R.string.loading),
                                modifier = Modifier.testTag("countries_loading_text")
                            )
                        }
                    }
                    state.error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("countries_error"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.error!!,
                                modifier = Modifier.testTag("countries_error_text")
                            )
                            Button(
                                onClick = { viewModel.loadCountries() },
                                modifier = Modifier.testTag("countries_retry")
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                    state.filteredCountries.isEmpty() -> {
                        if (!state.hasLoaded) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(stringResource(R.string.loading))
                            }
                        } else {
                            val msg = when {
                                state.countries.isEmpty() -> stringResource(R.string.no_data)
                                state.hasActiveFilters -> stringResource(R.string.no_results_filters)
                                else -> stringResource(R.string.no_results)
                            }
                            Text(
                                text = msg,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .testTag("countries_no_results")
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.testTag("countries_list")
                        ) {
                            items(
                                state.filteredCountries,
                                key = { it.alpha2Code ?: it.name }
                            ) { country ->
                                CountryItem(
                                    country = country,
                                    onClick = {
                                        country.alpha2Code?.let { onCountryClick(it) }
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}
