package com.example.worldscope.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** API del Banco Mundial (respuesta JSON como texto para parseo manual). */
interface WorldBankApi {

    @GET("v2/country/{countryCode}/indicator/{indicatorId}")
    suspend fun getIndicatorRaw(
        @Path("countryCode") countryCode: String,
        @Path("indicatorId") indicatorId: String,
        @Query("format") format: String = "json",
        @Query("mrnev") mrnev: Int = 1
    ): String
}
