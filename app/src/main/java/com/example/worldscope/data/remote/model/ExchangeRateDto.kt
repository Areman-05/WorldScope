package com.example.worldscope.data.remote.model

import com.google.gson.annotations.SerializedName

data class ExchangeRateDto(
    @SerializedName("result") val result: String?,
    @SerializedName("base_code") val baseCode: String?,
    @SerializedName("rates") val rates: Map<String, Double>?
)

