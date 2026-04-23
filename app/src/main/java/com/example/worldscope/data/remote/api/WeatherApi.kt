package com.example.worldscope.data.remote.api

import com.example.worldscope.data.remote.model.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    // Open-Meteo no requiere api key para consulta basica.
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current")
        current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code"
    ): WeatherDto
}

