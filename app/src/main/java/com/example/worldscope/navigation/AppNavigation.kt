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
        startDestination = "countries"
    ) {
        composable("countries") {
            CountriesScreen(
                onCountryClick = { code ->
                    navController.navigate("country/$code")
                },
                onFavoritesClick = { navController.navigate("favorites") }
            )
        }
        composable(
            route = "country/{code}",
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: com.example.worldscope.ui.detail.CountryDetailViewModel = hiltViewModel(backStackEntry)
            CountryDetailScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("favorites") {
            FavoritesScreen(
                onCountryClick = { code -> navController.navigate("country/$code") },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
