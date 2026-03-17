package com.example.worldscope.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import kotlinx.coroutines.flow.Flow

/** DAO para persistir y consultar países favoritos. */
@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_countries ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteCountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(country: FavoriteCountryEntity)

    @Query("DELETE FROM favorite_countries WHERE alpha2Code = :alpha2Code")
    suspend fun removeFavorite(alpha2Code: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_countries WHERE alpha2Code = :alpha2Code)")
    suspend fun isFavorite(alpha2Code: String): Boolean
}
