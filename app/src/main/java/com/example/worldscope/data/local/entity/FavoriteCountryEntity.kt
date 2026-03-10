package com.example.worldscope.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_countries")
data class FavoriteCountryEntity(
    @PrimaryKey val alpha2Code: String,
    val name: String,
    val capital: String?,
    val region: String?,
    val population: Long,
    val flagUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)
