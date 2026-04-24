@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("DEPRECATION")
package com.example.worldscope.ui.countries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AssistChip
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsGreenLight
import com.example.worldscope.ui.theme.WsSurfaceSoft

@Composable
fun CountriesScreen(
    onCountryClick: (String) -> Unit = {},
    onAboutClick: () -> Unit = {},
    viewModel: CountriesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedSort by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(false) }

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
                    .testTag("countries_topbar")
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("countries_topbar_title"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = null,
                                tint = Color(0xFFFFF176)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.app_name),
                                textAlign = TextAlign.Center,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        IconButton(
                            onClick = onAboutClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("countries_about")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = stringResource(R.string.about_title)
                            )
                        }
                        IconButton(
                            onClick = viewModel::toggleViewMode,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("countries_toggle_view")
                        ) {
                            Icon(
                                imageVector = if (state.viewMode == CountriesViewMode.LIST) {
                                    Icons.Filled.GridView
                                } else {
                                    Icons.Filled.ViewAgenda
                                },
                                contentDescription = stringResource(R.string.countries)
                            )
                        }
                    }
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.95f)
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .testTag("countries_search"),
                        placeholder = { Text(stringResource(R.string.search_country)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = WsGreenDark
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
        ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.recentVisits.isNotEmpty()) {
                Text(
                    text = "⭐ ${stringResource(R.string.recent_title)}",
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("countries_recent_title"),
                    color = WsGreenDark,
                    style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("countries_recent_row")
                ) {
                    items(state.recentVisits, key = { it.alpha2Code }) { recent ->
                        AssistChip(
                            onClick = { onCountryClick(recent.alpha2Code) },
                            label = { Text(recent.name) },
                            modifier = Modifier.testTag("countries_recent_${recent.alpha2Code}"),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = WsGreen,
                                labelColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = WsGreenDark
                            )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { filtersExpanded = !filtersExpanded },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = WsGreenDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("countries_filters_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (filtersExpanded) "Ocultar filtros" else "Mostrar filtros")
                }
            }
            AnimatedVisibility(
                visible = filtersExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = androidx.compose.ui.graphics.Color.White,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(R.string.region),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .testTag("countries_region_label"),
                        color = WsGreenDark,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedRegion,
                        onExpandedChange = {
                            if (state.availableRegions.isNotEmpty()) expandedRegion = !expandedRegion
                            else expandedRegion = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.regionFilter ?: stringResource(R.string.all_regions),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("countries_region"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.sort_by),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .testTag("countries_sort_label"),
                        color = WsGreenDark,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedSort,
                        onExpandedChange = { expandedSort = !expandedSort },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val sortLabel = when (state.sortMode) {
                            SortMode.NAME -> stringResource(R.string.sort_name)
                            SortMode.POPULATION -> stringResource(R.string.sort_population)
                            SortMode.AREA -> stringResource(R.string.sort_area)
                        }
                        OutlinedTextField(
                            value = sortLabel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSort)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("countries_sort"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
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
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_area)) },
                                onClick = {
                                    viewModel.updateSortMode(SortMode.AREA)
                                    expandedSort = false
                                },
                                modifier = Modifier.testTag("countries_sort_area")
                            )
                        }
                    }
                }
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
                        .padding(horizontal = 10.dp)
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
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("countries_loading"),
                            shape = RoundedCornerShape(18.dp),
                            color = androidx.compose.ui.graphics.Color.White,
                            tonalElevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(color = WsGreen)
                                Text(
                                    stringResource(R.string.loading),
                                    color = WsGreenDark,
                                    modifier = Modifier.testTag("countries_loading_text")
                                )
                            }
                        }
                    }
                    state.error != null -> {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("countries_error"),
                            shape = RoundedCornerShape(18.dp),
                            color = androidx.compose.ui.graphics.Color.White,
                            tonalElevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = state.error!!,
                                    color = androidx.compose.ui.graphics.Color.Black,
                                    modifier = Modifier.testTag("countries_error_text"),
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { viewModel.loadCountries() },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = WsGreen
                                    ),
                                    modifier = Modifier.testTag("countries_retry")
                                ) {
                                    Text(
                                        stringResource(R.string.retry),
                                        color = androidx.compose.ui.graphics.Color.White,
                                        modifier = Modifier.testTag("countries_retry_text")
                                    )
                                }
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
                                    .testTag("countries_no_results_text"),
                                color = WsGreenDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        if (state.viewMode == CountriesViewMode.GRID) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 160.dp),
                                modifier = Modifier.testTag("countries_grid")
                            ) {
                                items(
                                    count = state.filteredCountries.size,
                                    key = { idx -> state.filteredCountries[idx].alpha2Code ?: state.filteredCountries[idx].name }
                                ) { idx ->
                                    val country = state.filteredCountries[idx]
                                    CountryItem(
                                        country = country,
                                        onClick = { country.alpha2Code?.let { onCountryClick(it) } },
                                        compact = true
                                    )
                                }
                            }
                        } else {
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
}
