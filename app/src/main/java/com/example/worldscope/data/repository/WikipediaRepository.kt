package com.example.worldscope.data.repository

import com.example.worldscope.data.remote.api.WikipediaApi
import com.example.worldscope.domain.model.WikiSummary
import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val api: WikipediaApi
) {

    suspend fun getSummaryForCountryName(name: String): Result<WikiSummary> = runCatching {
        val title = name.trim().replace(' ', '_')
        require(title.isNotEmpty()) { "Titulo vacio" }
        val dto = api.getPageSummary(title)
        WikiSummary(
            title = dto.title,
            extract = dto.extract?.takeIf { it.isNotBlank() },
            thumbnailUrl = dto.thumbnail?.source
        )
    }
}
