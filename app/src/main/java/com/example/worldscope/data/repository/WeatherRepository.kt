package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.WeatherApi
import com.example.worldscope.domain.model.WeatherInfo
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Result<WeatherInfo> = try {
        val dto = weatherApi.getCurrentWeather(lat = lat, lon = lon)
        val current = dto.current
            ?: return Result.failure(Exception("Respuesta de clima incompleta"))
        val code = current.weatherCode
        val (condition, description) = mapWeatherCode(code)
        Result.success(
            WeatherInfo(
                temperatureCelsius = current.temperature,
                feelsLikeCelsius = current.apparentTemperature,
                humidity = current.humidity,
                condition = condition,
                description = description
            )
        )
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo cargar el clima: ${e.message ?: "error"}", e))
    }

    private fun mapWeatherCode(code: Int?): Pair<String?, String?> = when (code) {
        0 -> "Despejado" to "Cielo despejado"
        1, 2, 3 -> "Nubes" to "Parcialmente nublado"
        45, 48 -> "Niebla" to "Niebla o escarcha"
        51, 53, 55, 56, 57 -> "Llovizna" to "Llovizna"
        61, 63, 65, 66, 67, 80, 81, 82 -> "Lluvia" to "Lluvia"
        71, 73, 75, 77, 85, 86 -> "Nieve" to "Nieve"
        95, 96, 99 -> "Tormenta" to "Tormenta"
        else -> "Desconocido" to "Condicion sin clasificar"
    }
}

