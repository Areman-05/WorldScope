package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.WorldBankApi
import com.example.worldscope.domain.model.EconomicInfo
import org.json.JSONArray
import javax.inject.Inject

class WorldBankRepository @Inject constructor(
    private val api: WorldBankApi
) {

    suspend fun getEconomicInfo(countryIso2: String): Result<EconomicInfo> = runCatching {
        val iso = countryIso2.uppercase()
        val gdpJson = api.getIndicatorRaw(iso, INDICATOR_GDP_USD)
        val inflJson = api.getIndicatorRaw(iso, INDICATOR_INFLATION)
        EconomicInfo(
            gdpUsd = parseFirstNumericValue(gdpJson),
            inflationPercent = parseFirstNumericValue(inflJson)
        )
    }

    private fun parseFirstNumericValue(json: String): Double? {
        return try {
            val root = JSONArray(json)
            if (root.length() < 2) return null
            val data = root.getJSONArray(1)
            if (data.length() == 0) return null
            for (i in 0 until data.length()) {
                val row = data.optJSONObject(i) ?: continue
                if (row.isNull("value")) continue
                val value = row.optDouble("value", Double.NaN).takeUnless { it.isNaN() }
                if (value != null) return value
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val INDICATOR_GDP_USD = "NY.GDP.MKTP.CD"
        private const val INDICATOR_INFLATION = "FP.CPI.TOTL.ZG"
    }
}
