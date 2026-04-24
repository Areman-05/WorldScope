package com.example.worldscope.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.worldscope.data.local.entity.RecentCountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentCountryDao {

    @Query("SELECT * FROM recent_countries ORDER BY visitedAt DESC LIMIT 12")
    fun observeRecent(): Flow<List<RecentCountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentCountryEntity): Unit
}
