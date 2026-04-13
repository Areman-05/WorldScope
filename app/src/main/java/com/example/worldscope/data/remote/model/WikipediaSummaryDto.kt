package com.example.worldscope.data.remote.model

import com.google.gson.annotations.SerializedName

data class WikipediaSummaryDto(
    @SerializedName("title") val title: String?,
    @SerializedName("extract") val extract: String?,
    @SerializedName("thumbnail") val thumbnail: WikipediaThumbnailDto?
)

data class WikipediaThumbnailDto(
    @SerializedName("source") val source: String?
)
