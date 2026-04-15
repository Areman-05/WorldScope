package com.example.worldscope.data.repository

import com.example.worldscope.data.local.dao.SearchHistoryDao
import com.example.worldscope.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchHistoryRepository @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {

    fun observeRecentSearches(): Flow<List<SearchHistoryEntity>> =
        searchHistoryDao.observeRecentSearches()

    suspend fun recordSearch(rawQuery: String) {
        val query = rawQuery.trim()
        if (query.length < 2) return
        searchHistoryDao.upsert(
            SearchHistoryEntity(
                query = query,
                searchedAt = System.currentTimeMillis()
            )
        )
    }
}
