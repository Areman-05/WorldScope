package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.WeatherApi
import com.example.worldscope.domain.model.WeatherInfo
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Result<WeatherInfo> = runCatching {
        val dto = weatherApi.getCurrentWeather(lat = lat, lon = lon, apiKey = apiKey)
        WeatherInfo(
            temperatureCelsius = dto.main?.temperature,
            feelsLikeCelsius = dto.main?.feelsLike,
            humidity = dto.main?.humidity,
            condition = dto.weather?.firstOrNull()?.main,
            description = dto.weather?.firstOrNull()?.description
        )
    }
}

