package com.example.worldscope.data.remote.model

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name") val name: CountryNameDto?,
    @SerializedName("capital") val capital: List<String>?,
    @SerializedName("region") val region: String?,
    @SerializedName("subregion") val subregion: String?,
    @SerializedName("population") val population: Long?,
    @SerializedName("area") val area: Double?,
    @SerializedName("flags") val flags: CountryFlagsDto?,
    @SerializedName("languages") val languages: Map<String, String>?,
    @SerializedName("currencies") val currencies: Map<String, CurrencyDto>?,
    @SerializedName("latlng") val latlng: List<Double>?,
    @SerializedName("cca2") val alpha2Code: String?,
    @SerializedName("cca3") val alpha3Code: String?
)

data class CountryNameDto(
    @SerializedName("common") val common: String?,
    @SerializedName("official") val official: String?
)

data class CountryFlagsDto(
    @SerializedName("png") val png: String?,
    @SerializedName("svg") val svg: String?
)

data class CurrencyDto(
    @SerializedName("name") val name: String?,
    @SerializedName("symbol") val symbol: String?
)
