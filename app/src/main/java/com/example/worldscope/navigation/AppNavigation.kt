package com.example.worldscope.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.worldscope.ui.countries.CountriesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "countries"
    ) {
        composable("countries") {
            CountriesScreen()
        }
    }
}
