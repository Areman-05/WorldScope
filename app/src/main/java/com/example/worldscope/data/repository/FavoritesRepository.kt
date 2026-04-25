package com.example.worldscope.data.repository

import com.example.worldscope.data.local.dao.FavoriteDao
import com.example.worldscope.data.local.dao.FavoriteGroupDao
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.data.local.entity.FavoriteGroupEntity
import com.example.worldscope.data.local.entity.FavoriteGroupItemCrossRef
import com.example.worldscope.domain.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val favoriteGroupDao: FavoriteGroupDao
) {

    fun getAllFavorites(): Flow<List<FavoriteCountryEntity>> =
        favoriteDao.getAllFavorites()

    suspend fun isFavorite(alpha2Code: String): Boolean =
        favoriteDao.isFavorite(alpha2Code)

    suspend fun addFavorite(country: Country) {
        favoriteDao.insertFavorite(country.toFavoriteEntity())
    }

    suspend fun removeFavorite(alpha2Code: String) {
        favoriteDao.removeFavorite(alpha2Code)
    }

    fun observeFavoriteGroups(): Flow<List<FavoriteGroupData>> =
        favoriteGroupDao.observeGroupsWithCountries().map { groups ->
            groups.map { item ->
                FavoriteGroupData(
                    id = item.group.id,
                    name = item.group.name,
                    countryCodes = item.countries.map { it.alpha2Code }.toSet()
                )
            }
        }

    suspend fun createGroup(name: String): Long {
        val normalized = name.trim()
        if (normalized.isBlank()) return -1L
        val inserted = favoriteGroupDao.insertGroup(FavoriteGroupEntity(name = normalized))
        if (inserted != -1L) return inserted
        return favoriteGroupDao.findGroupIdByName(normalized) ?: -1L
    }

    suspend fun removeGroup(groupId: Long) {
        favoriteGroupDao.deleteGroup(groupId)
    }

    suspend fun toggleCountryInGroup(groupId: Long, alpha2Code: String) {
        if (favoriteGroupDao.isCountryInGroup(groupId, alpha2Code)) {
            favoriteGroupDao.removeGroupItem(groupId, alpha2Code)
        } else {
            favoriteGroupDao.upsertGroupItem(
                FavoriteGroupItemCrossRef(
                    groupId = groupId,
                    alpha2Code = alpha2Code
                )
            )
        }
    }

    suspend fun addCountryToGroup(groupId: Long, alpha2Code: String) {
        favoriteGroupDao.upsertGroupItem(
            FavoriteGroupItemCrossRef(
                groupId = groupId,
                alpha2Code = alpha2Code
            )
        )
    }

    suspend fun removeCountryFromGroup(groupId: Long, alpha2Code: String) {
        favoriteGroupDao.removeGroupItem(groupId, alpha2Code)
    }

    suspend fun addCountryToGroup(country: Country, groupId: Long) {
        val alpha2 = country.alpha2Code ?: return
        favoriteDao.insertFavorite(country.toFavoriteEntity())
        favoriteGroupDao.upsertGroupItem(
            FavoriteGroupItemCrossRef(
                groupId = groupId,
                alpha2Code = alpha2
            )
        )
    }

    private fun Country.toFavoriteEntity(): FavoriteCountryEntity =
        FavoriteCountryEntity(
            alpha2Code = alpha2Code ?: "",
            name = name,
            capital = capital,
            region = region,
            population = population,
            flagUrl = flagUrl
        )
}

data class FavoriteGroupData(
    val id: Long,
    val name: String,
    val countryCodes: Set<String>
)
