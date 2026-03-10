package com.example.worldscope.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.worldscope.data.local.dao.FavoriteDao
import com.example.worldscope.data.local.entity.FavoriteCountryEntity

@Database(
    entities = [FavoriteCountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WorldScopeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
