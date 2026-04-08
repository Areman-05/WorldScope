package com.example.worldscope.di

import com.example.worldscope.data.remote.api.CountriesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

/** Módulo Hilt que provee Retrofit, OkHttp y CountriesApi. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val COUNTRIES_BASE_URL = "https://restcountries.com/"

    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Named("countries")
    fun provideCountriesRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(COUNTRIES_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideCountriesApi(@Named("countries") retrofit: Retrofit): CountriesApi =
        retrofit.create(CountriesApi::class.java)
}
