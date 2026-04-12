package com.example.worldscope.navigation

object Routes {
    const val COUNTRIES = "countries"
    const val FAVORITES = "favorites"
    const val COMPARE = "compare"
    const val QUIZ = "quiz"
    const val COUNTRY_DETAIL = "country/{code}"

    fun countryDetail(code: String): String = "country/$code"
}

