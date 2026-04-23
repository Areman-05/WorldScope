package com.example.worldscope.data.remote.model

import com.google.gson.annotations.SerializedName

data class ExchangeRateDto(
    @SerializedName("base_code") val baseCode: String?,
    @SerializedName("base") val frankfurterBaseCode: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("rates") val rates: Map<String, Double>?
)

