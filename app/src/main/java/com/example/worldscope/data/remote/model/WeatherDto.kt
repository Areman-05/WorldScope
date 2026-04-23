package com.example.worldscope.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("current") val current: WeatherCurrentDto?
)

data class WeatherCurrentDto(
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("apparent_temperature") val apparentTemperature: Double?,
    @SerializedName("relative_humidity_2m") val humidity: Int?,
    @SerializedName("weather_code") val weatherCode: Int?
)

