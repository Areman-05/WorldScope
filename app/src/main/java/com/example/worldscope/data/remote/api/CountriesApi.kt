package com.example.worldscope.data.remote.api

import com.example.worldscope.data.remote.model.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountriesApi {

    @GET("v3.1/all")
    suspend fun getAllCountries(): List<CountryDto>

    @GET("v3.1/name/{name}")
    suspend fun getCountriesByName(@Path("name") name: String): List<CountryDto>

    @GET("v3.1/region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): List<CountryDto>

    @GET("v3.1/alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): CountryDto
}
