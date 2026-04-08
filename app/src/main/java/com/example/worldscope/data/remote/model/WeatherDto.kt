package com.example.worldscope.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("main") val main: WeatherMainDto?,
    @SerializedName("weather") val weather: List<WeatherDescriptionDto>?
)

data class WeatherMainDto(
    @SerializedName("temp") val temperature: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("humidity") val humidity: Int?
)

data class WeatherDescriptionDto(
    @SerializedName("main") val main: String?,
    @SerializedName("description") val description: String?
)

