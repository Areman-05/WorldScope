package com.example.worldscope.navigation

object Routes {
    const val COUNTRIES = "countries"
    const val FAVORITES = "favorites"
    const val COUNTRY_DETAIL = "country/{code}"

    fun countryDetail(code: String): String = "country/$code"
}

