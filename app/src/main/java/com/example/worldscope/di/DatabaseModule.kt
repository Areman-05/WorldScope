package com.example.worldscope.di

import android.content.Context
import androidx.room.Room
import com.example.worldscope.data.local.WorldScopeDatabase
import com.example.worldscope.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WorldScopeDatabase =
        Room.databaseBuilder(
            context,
            WorldScopeDatabase::class.java,
            "worldscope_db"
        ).build()

    @Provides
    fun provideFavoriteDao(database: WorldScopeDatabase): FavoriteDao =
        database.favoriteDao()
}
