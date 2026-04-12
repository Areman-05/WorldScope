package com.example.worldscope.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.worldscope.data.local.dao.FavoriteDao
import com.example.worldscope.data.local.dao.RecentCountryDao
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.data.local.entity.RecentCountryEntity

@Database(
    entities = [FavoriteCountryEntity::class, RecentCountryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WorldScopeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentCountryDao(): RecentCountryDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS recent_countries (" +
                        "alpha2Code TEXT NOT NULL PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "visitedAt INTEGER NOT NULL)"
                )
            }
        }
    }
}
