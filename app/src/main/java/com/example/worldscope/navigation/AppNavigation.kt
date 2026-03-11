package com.example.worldscope.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.worldscope.ui.countries.CountriesScreen
import com.example.worldscope.ui.detail.CountryDetailScreen

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
                }
            )
        }
        composable(
            route = "country/{code}",
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: return@composable
            CountryDetailScreen(
                countryCode = code,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
