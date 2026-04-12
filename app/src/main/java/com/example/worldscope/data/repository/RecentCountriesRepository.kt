package com.example.worldscope.data.repository

import com.example.worldscope.data.local.dao.RecentCountryDao
import com.example.worldscope.data.local.entity.RecentCountryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecentCountriesRepository @Inject constructor(
    private val recentCountryDao: RecentCountryDao
) {

    fun observeRecent(): Flow<List<RecentCountryEntity>> =
        recentCountryDao.observeRecent()

    suspend fun recordVisit(alpha2Code: String, name: String) {
        val code = alpha2Code.trim().uppercase()
        if (code.length != 2) return
        recentCountryDao.upsert(
            RecentCountryEntity(
                alpha2Code = code,
                name = name,
                visitedAt = System.currentTimeMillis()
            )
        )
    }
}
