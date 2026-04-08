package com.example.worldscope.domain.model

data class WeatherInfo(
    val temperatureCelsius: Double?,
    val feelsLikeCelsius: Double?,
    val humidity: Int?,
    val condition: String?,
    val description: String?
)

