package com.example.worldscope.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_countries")
data class RecentCountryEntity(
    @PrimaryKey val alpha2Code: String,
    val name: String,
    val visitedAt: Long = System.currentTimeMillis()
)
