package com.example.worldscope.data.remote.api

import com.example.worldscope.data.remote.model.ExchangeRateDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    // Frankfurter no requiere api key para conversion basica.
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") baseCode: String,
        @Query("to") targetCode: String
    ): ExchangeRateDto
}

