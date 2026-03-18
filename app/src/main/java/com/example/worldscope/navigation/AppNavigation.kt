package com.example.worldscope.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.worldscope.ui.countries.CountriesScreen
import com.example.worldscope.ui.detail.CountryDetailScreen
import com.example.worldscope.ui.favorites.FavoritesScreen
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.COUNTRIES
    ) {
        composable(Routes.COUNTRIES) {
            CountriesScreen(
                onCountryClick = { code ->
                    navController.navigate(Routes.countryDetail(code))
                },
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
