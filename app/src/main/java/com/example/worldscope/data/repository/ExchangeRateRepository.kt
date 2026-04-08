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
    ): Result<ExchangeInfo> = runCatching {
        val dto = exchangeRateApi.getLatestRates(apiKey = apiKey, baseCode = baseCode)
        val rate = dto.rates?.get(targetCode)
        ExchangeInfo(
            baseCode = baseCode,
            targetCode = targetCode,
            rate = rate
        )
    }
}

