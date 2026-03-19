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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R

@Composable
fun CountriesScreen(
    onCountryClick: (String) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
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
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Favorite, contentDescription = stringResource(R.string.favorites))
                    }
                }
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
                    .padding(8.dp),
                placeholder = { Text(stringResource(R.string.search_country)) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = expandedRegion,
                onExpandedChange = { expandedRegion = !expandedRegion },
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
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
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
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_population)) },
                        onClick = {
                            viewModel.updateSortMode(SortMode.POPULATION)
                            expandedSort = false
                        }
                    )
                }
            }
            if (state.searchQuery.isNotBlank() || state.regionFilter != null || state.sortMode != SortMode.NAME) {
                OutlinedButton(
                    onClick = { viewModel.clearFilters() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
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
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    state.error != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = state.error!!)
                            Button(onClick = { viewModel.loadCountries() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                    state.filteredCountries.isEmpty() -> {
                        val msg = if (state.searchQuery.isNotBlank() || state.regionFilter != null) {
                            stringResource(R.string.no_results_filters)
                        } else {
                            stringResource(R.string.no_results)
                        }
                        Text(
                            text = msg,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn {
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
