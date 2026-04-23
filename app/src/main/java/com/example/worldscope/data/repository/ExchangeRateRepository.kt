package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.ExchangeRateApi
import com.example.worldscope.domain.model.ExchangeInfo
import javax.inject.Inject

class ExchangeRateRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi
) {
    suspend fun getExchangeRate(
        baseCode: String,
        targetCode: String
    ): Result<ExchangeInfo> = try {
        val dto = exchangeRateApi.getLatestRates(baseCode = baseCode, targetCode = targetCode)
        val rate = dto.rates?.get(targetCode)
            ?: return Result.failure(
                Exception("No se encontro tasa para $targetCode desde $baseCode")
            )
        val resolvedBaseCode = dto.baseCode ?: dto.frankfurterBaseCode ?: baseCode
        Result.success(
            ExchangeInfo(
                baseCode = resolvedBaseCode,
                targetCode = targetCode,
                rate = rate
            )
        )
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo cargar el cambio de divisa: ${e.message ?: "error"}", e))
    }
}

