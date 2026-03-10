package com.example.worldscope.domain.model

data class Country(
    val name: String,
    val capital: String?,
    val region: String?,
    val subregion: String?,
    val population: Long,
    val flagUrl: String?,
    val languages: List<String>,
    val currencies: List<String>,
    val alpha2Code: String?,
    val alpha3Code: String?,
    val latlng: Pair<Double, Double>?
)
