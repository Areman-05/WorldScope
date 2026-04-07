package com.example.worldscope.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onCountryClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorites)) },
                modifier = Modifier.testTag("favorites_topbar"),
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("favorites_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (!state.hasLoaded) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_loading"),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.loading))
            }
        } else if (state.favorites.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_empty_container"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.testTag("favorites_empty_content"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.no_favorites),
                        modifier = Modifier.testTag("favorites_empty")
                    )
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .testTag("favorites_go_countries")
                    ) {
                        Text(
                            stringResource(R.string.countries),
                            modifier = Modifier.testTag("favorites_go_countries_text")
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_list")
            ) {
                items(state.favorites, key = { it.alpha2Code }) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onClick = {
                            val code = favorite.alpha2Code
                            if (code.isNotBlank()) onCountryClick(code)
                        },
                        onRemoveClick = { viewModel.removeFavorite(favorite.alpha2Code) }
                    )
                    HorizontalDivider(modifier = Modifier.testTag("favorites_divider"))
                }
            }
        }
    }
}
