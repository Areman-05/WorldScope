package com.example.worldscope.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.worldscope.R
import com.example.worldscope.ui.countries.CountriesScreen
import com.example.worldscope.ui.detail.CountryDetailScreen
import com.example.worldscope.ui.favorites.FavoritesScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route != Routes.COUNTRY_DETAIL

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.COUNTRIES } == true,
                        onClick = {
                            navController.navigate(Routes.COUNTRIES) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Public, contentDescription = null) },
                        label = { Text(stringResource(R.string.countries)) }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.FAVORITES } == true,
                        onClick = {
                            navController.navigate(Routes.FAVORITES) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.favorites)) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.COUNTRIES,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(Routes.COUNTRIES) {
                CountriesScreen(
                    onCountryClick = { code -> navController.navigate(Routes.countryDetail(code)) },
                    onFavoritesClick = { navController.navigate(Routes.FAVORITES) }
                )
            }
            composable(
                route = Routes.COUNTRY_DETAIL,
                arguments = listOf(navArgument("code") { type = NavType.StringType })
            ) { backStackEntry ->
                val viewModel: com.example.worldscope.ui.detail.CountryDetailViewModel = hiltViewModel(backStackEntry)
                CountryDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    onCountryClick = { code -> navController.navigate(Routes.countryDetail(code)) },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

