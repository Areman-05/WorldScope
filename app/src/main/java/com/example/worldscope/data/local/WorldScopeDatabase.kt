package com.example.worldscope.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.worldscope.data.local.dao.FavoriteDao
import com.example.worldscope.data.local.dao.FavoriteGroupDao
import com.example.worldscope.data.local.dao.RecentCountryDao
import com.example.worldscope.data.local.dao.SearchHistoryDao
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.data.local.entity.FavoriteGroupEntity
import com.example.worldscope.data.local.entity.FavoriteGroupItemCrossRef
import com.example.worldscope.data.local.entity.RecentCountryEntity
import com.example.worldscope.data.local.entity.SearchHistoryEntity

@Database(
    entities = [
        FavoriteCountryEntity::class,
        FavoriteGroupEntity::class,
        FavoriteGroupItemCrossRef::class,
        RecentCountryEntity::class,
        SearchHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class WorldScopeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun favoriteGroupDao(): FavoriteGroupDao
    abstract fun recentCountryDao(): RecentCountryDao
    abstract fun searchHistoryDao(): SearchHistoryDao

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
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS search_history (" +
                        "query TEXT NOT NULL PRIMARY KEY, " +
                        "searchedAt INTEGER NOT NULL)"
                )
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS favorite_groups (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "createdAt INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_favorite_groups_name " +
                        "ON favorite_groups(name)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS favorite_group_items (" +
                        "groupId INTEGER NOT NULL, " +
                        "alpha2Code TEXT NOT NULL, " +
                        "addedAt INTEGER NOT NULL, " +
                        "PRIMARY KEY(groupId, alpha2Code), " +
                        "FOREIGN KEY(groupId) REFERENCES favorite_groups(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY(alpha2Code) REFERENCES favorite_countries(alpha2Code) ON DELETE CASCADE)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_favorite_group_items_groupId " +
                        "ON favorite_group_items(groupId)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_favorite_group_items_alpha2Code " +
                        "ON favorite_group_items(alpha2Code)"
                )
            }
        }
    }
}
