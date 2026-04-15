package com.example.worldscope.domain.model

/** Modelo de dominio que representa un país. */
data class Country(
    val name: String,
    val capital: String?,
    val region: String?,
    val subregion: String?,
    val population: Long,
    val areaKm2: Double?,
    val flagUrl: String?,
    val languages: List<String>,
    /** Etiquetas legibles de moneda. */
    val currencies: List<String>,
    /** Codigos ISO de moneda (p. ej. EUR) desde RestCountries. */
    val currencyCodes: List<String> = emptyList(),
    val alpha2Code: String?,
    val alpha3Code: String?,
    val latlng: Pair<Double, Double>?
)
