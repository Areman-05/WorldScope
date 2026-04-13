package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.ExchangeRateApi
import com.example.worldscope.domain.model.ExchangeInfo
import javax.inject.Inject

class ExchangeRateRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi
) {
    suspend fun getExchangeRate(
        apiKey: String,
        baseCode: String,
        targetCode: String
    ): Result<ExchangeInfo> = try {
        val dto = exchangeRateApi.getLatestRates(apiKey = apiKey, baseCode = baseCode)
        val rate = dto.rates?.get(targetCode)
        Result.success(
            ExchangeInfo(
                baseCode = baseCode,
                targetCode = targetCode,
                rate = rate
            )
        )
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo cargar el cambio de divisa: ${e.message ?: "error"}", e))
    }
}

