package com.example.worldscope.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.worldscope.data.local.entity.FavoriteGroupEntity
import com.example.worldscope.data.local.entity.FavoriteGroupItemCrossRef
import com.example.worldscope.data.local.entity.FavoriteGroupWithCountries
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGroupDao {

    @Transaction
    @Query("SELECT * FROM favorite_groups ORDER BY createdAt DESC")
    fun observeGroupsWithCountries(): Flow<List<FavoriteGroupWithCountries>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: FavoriteGroupEntity): Long

    @Query("SELECT id FROM favorite_groups WHERE name = :name LIMIT 1")
    suspend fun findGroupIdByName(name: String): Long?

    @Query("DELETE FROM favorite_groups WHERE id = :groupId")
    suspend fun deleteGroup(groupId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroupItem(crossRef: FavoriteGroupItemCrossRef)

    @Query("DELETE FROM favorite_group_items WHERE groupId = :groupId AND alpha2Code = :alpha2Code")
    suspend fun removeGroupItem(groupId: Long, alpha2Code: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_group_items WHERE groupId = :groupId AND alpha2Code = :alpha2Code)")
    suspend fun isCountryInGroup(groupId: Long, alpha2Code: String): Boolean
}
