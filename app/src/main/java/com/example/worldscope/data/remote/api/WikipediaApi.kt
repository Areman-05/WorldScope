package com.example.worldscope.data.remote.api

import com.example.worldscope.data.remote.model.WikipediaSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path

/** API REST de Wikipedia (resumen de pagina, sin API key). */
interface WikipediaApi {

    @GET("page/summary/{title}")
    suspend fun getPageSummary(
        @Path(value = "title", encoded = true) title: String
    ): WikipediaSummaryDto
}
