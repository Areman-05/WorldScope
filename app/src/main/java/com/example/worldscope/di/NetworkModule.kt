package com.example.worldscope.di

import com.example.worldscope.data.remote.api.CountriesApi
import com.example.worldscope.data.remote.api.ExchangeRateApi
import com.example.worldscope.data.remote.api.WeatherApi
import com.example.worldscope.data.remote.api.WorldBankApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

/** Módulo Hilt que provee Retrofit, OkHttp y CountriesApi. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val COUNTRIES_BASE_URL = "https://restcountries.com/"
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/"
    private const val EXCHANGE_RATE_BASE_URL = "https://v6.exchangerate-api.com/"
    private const val WORLD_BANK_BASE_URL = "https://api.worldbank.org/"

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

    @Provides
    @Named("weather")
    fun provideWeatherRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideWeatherApi(@Named("weather") retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Named("exchange")
    fun provideExchangeRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(EXCHANGE_RATE_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideExchangeRateApi(@Named("exchange") retrofit: Retrofit): ExchangeRateApi =
        retrofit.create(ExchangeRateApi::class.java)

    @Provides
    @Named("worldbank")
    fun provideWorldBankRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(WORLD_BANK_BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    @Provides
    fun provideWorldBankApi(@Named("worldbank") retrofit: Retrofit): WorldBankApi =
        retrofit.create(WorldBankApi::class.java)
}
