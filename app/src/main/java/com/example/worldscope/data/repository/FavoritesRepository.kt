package com.example.worldscope.data.repository

import com.example.worldscope.data.local.dao.FavoriteDao
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.domain.model.Country
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
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

    suspend fun removeFavorite(entity: FavoriteCountryEntity) {
        favoriteDao.removeFavorite(entity.alpha2Code)
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
