package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.WikipediaApi
import com.example.worldscope.domain.model.WikiSummary
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val api: WikipediaApi
) {

    suspend fun getSummaryForCountryName(name: String): Result<WikiSummary> = runCatching {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Titulo vacio" }
        val candidates = listOf(
            normalized.replace(' ', '_'),
            "${normalized.replace(' ', '_')}_(country)"
        )
        var lastError: Throwable? = null
        for (candidate in candidates) {
            try {
                val encodedTitle = URLEncoder.encode(candidate, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20")
                val dto = api.getPageSummary(encodedTitle)
                val extract = dto.extract?.takeIf { it.isNotBlank() }
                if (extract != null) {
                    return@runCatching WikiSummary(
                        title = dto.title,
                        extract = extract,
                        thumbnailUrl = dto.thumbnail?.source
                    )
                }
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw (lastError ?: IllegalStateException("No se pudo obtener resumen de Wikipedia"))
    }
}
