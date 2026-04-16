package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.CountriesApi
import com.example.worldscope.data.remote.model.CountryDto
import com.example.worldscope.domain.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val api: CountriesApi
) {

    @Volatile
    private var cachedAllCountries: Result<List<Country>>? = null

    @Volatile
    private var cacheTsMs: Long = 0L

    // Evita spamear la API si Android remonta pantallas durante tests (teclado/IME, navegacion, etc).
    private val cacheTtlMs: Long = 30_000

    fun getAllCountries(): Flow<Result<List<Country>>> = flow {
        try {
            val now = System.currentTimeMillis()
            val cached = cachedAllCountries
            if (cached != null && now - cacheTsMs <= cacheTtlMs) {
                emit(cached)
                return@flow
            }

            val response = api.getAllCountries()
            val mapped = response.map { it.toDomain() }
            val result: Result<List<Country>> = Result.success(mapped)
            cachedAllCountries = result
            cacheTsMs = now
            emit(result)
        } catch (e: Exception) {
            val now = System.currentTimeMillis()
            val result: Result<List<Country>> =
                Result.failure(Exception("Error al cargar paises: ${e.message ?: "red"}", e))
            cachedAllCountries = result
            cacheTsMs = now
            emit(result)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getCountryByCode(code: String): Result<Country> = withContext(Dispatchers.IO) {
        try {
            val dto = api.getCountryByCode(code)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar el pais: ${e.message ?: "red"}", e))
        }
    }

    private fun CountryDto.toDomain(): Country = Country(
        name = name?.common ?: "",
        capital = capital?.firstOrNull(),
        region = region,
        subregion = subregion,
        population = population ?: 0L,
        areaKm2 = area,
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
