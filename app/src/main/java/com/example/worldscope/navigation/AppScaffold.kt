package com.example.worldscope.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R
import com.example.worldscope.ui.compare.CompareScreen
import com.example.worldscope.ui.countries.CountriesScreen
import com.example.worldscope.ui.detail.CountryDetailScreen
import com.example.worldscope.ui.favorites.FavoritesScreen
import com.example.worldscope.ui.quiz.QuizScreen

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route?.startsWith("country/") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(modifier = Modifier.testTag("app_bottom_bar")) {
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
                        label = { Text(stringResource(R.string.countries)) },
                        modifier = Modifier.testTag("nav_countries")
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
                        label = { Text(stringResource(R.string.favorites)) },
                        modifier = Modifier.testTag("nav_favorites")
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.COMPARE } == true,
                        onClick = {
                            navController.navigate(Routes.COMPARE) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.CompareArrows, contentDescription = null) },
                        label = { Text(stringResource(R.string.compare_title)) },
                        modifier = Modifier.testTag("nav_compare")
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.QUIZ } == true,
                        onClick = {
                            navController.navigate(Routes.QUIZ) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.EmojiEvents, contentDescription = null) },
                        label = { Text(stringResource(R.string.quiz_title)) },
                        modifier = Modifier.testTag("nav_quiz")
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.COUNTRIES,
            modifier = Modifier
                .padding(paddingValues)
                .testTag("app_nav_host")
        ) {
            composable(Routes.COUNTRIES) {
                CountriesScreen(
                    onCountryClick = { code -> navController.navigate(Routes.countryDetail(code)) }
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
                    onBackClick = {
                        navController.navigate(Routes.COUNTRIES) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Routes.COMPARE) {
                CompareScreen()
            }
            composable(Routes.QUIZ) {
                QuizScreen()
            }
        }
    }
}
