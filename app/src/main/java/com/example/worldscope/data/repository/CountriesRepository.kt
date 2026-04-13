package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.CountriesApi
import com.example.worldscope.data.remote.model.CountryDto
import com.example.worldscope.domain.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val api: CountriesApi
) {

    fun getAllCountries(): Flow<Result<List<Country>>> = flow {
        try {
            val response = api.getAllCountries()
            emit(Result.success(response.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Error al cargar paises: ${e.message ?: "red"}", e)))
        }
    }

    suspend fun getCountryByCode(code: String): Result<Country> = try {
        val dto = api.getCountryByCode(code)
        Result.success(dto.toDomain())
    } catch (e: Exception) {
        Result.failure(Exception("Error al cargar el pais: ${e.message ?: "red"}", e))
    }

    private fun CountryDto.toDomain(): Country = Country(
        name = name?.common ?: "",
        capital = capital?.firstOrNull(),
        region = region,
        subregion = subregion,
        population = population ?: 0L,
        flagUrl = flags?.png,
        languages = languages?.values?.toList() ?: emptyList(),
        currencyCodes = currencies?.keys?.map { it.uppercase() } ?: emptyList(),
        currencies = currencies?.mapNotNull { (code, cur) ->
            "${cur.name} (${cur.symbol ?: ""}) — $code"
        } ?: emptyList(),
        alpha2Code = alpha2Code,
        alpha3Code = alpha3Code,
        latlng = latlng?.let { list -> if (list.size >= 2) Pair(list[0], list[1]) else null }
    )

}
