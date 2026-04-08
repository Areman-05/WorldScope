package com.example.worldscope.domain.model

data class ExchangeInfo(
    val baseCode: String,
    val targetCode: String,
    val rate: Double?
)

