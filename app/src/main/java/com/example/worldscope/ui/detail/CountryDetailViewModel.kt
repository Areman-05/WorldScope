package com.example.worldscope.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.domain.model.EconomicInfo
import com.example.worldscope.domain.model.ExchangeInfo
import com.example.worldscope.domain.model.Country
import com.example.worldscope.domain.model.WeatherInfo
import com.example.worldscope.domain.model.WikiSummary
import com.example.worldscope.data.repository.CountriesRepository
import com.example.worldscope.data.repository.ExchangeRateRepository
import com.example.worldscope.data.repository.FavoritesRepository
import com.example.worldscope.data.repository.RecentCountriesRepository
import com.example.worldscope.data.repository.WeatherRepository
import com.example.worldscope.data.repository.WikipediaRepository
import com.example.worldscope.data.repository.WorldBankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val repository: CountriesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val weatherRepository: WeatherRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val worldBankRepository: WorldBankRepository,
    private val wikipediaRepository: WikipediaRepository,
    private val recentCountriesRepository: RecentCountriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val code: String = savedStateHandle.get<String>("code") ?: ""

    private val _uiState = MutableStateFlow(CountryDetailUiState())
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    init {
        loadCountry()
    }

    fun toggleFavorite() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            val country = _uiState.value.country ?: return@launch
            val alpha2 = country.alpha2Code ?: return@launch
            if (_uiState.value.isFavorite) {
                favoritesRepository.removeFavorite(alpha2)
                _uiState.update { it.copy(isFavorite = false) }
            } else {
                favoritesRepository.addFavorite(country)
                _uiState.update { it.copy(isFavorite = true) }
            }
        }
    }

    fun loadCountry() {
        if (code.isBlank()) {
            _uiState.update { it.copy(hasLoaded = true, isLoading = false) }
            return
        }
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    hasLoaded = false,
                    wikiSummary = null,
                    isLoadingWiki = false
                )
            }
            repository.getCountryByCode(code)
                .onSuccess { country ->
                    val isFav = favoritesRepository.isFavorite(country.alpha2Code ?: "")
                    _uiState.update {
                        it.copy(country = country, isLoading = false, error = null, isFavorite = isFav, hasLoaded = true)
                    }
                    country.alpha2Code?.let { alpha2 ->
                        viewModelScope.launch {
                            recentCountriesRepository.recordVisit(alpha2, country.name)
                        }
                    }
                    loadWeather(country)
                    loadExchangeRate(country)
                    loadEconomic(country)
                    loadWikipedia(country)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message, hasLoaded = true)
                    }
                }
        }
    }

    private fun loadWeather(country: Country) {
        val latLng = country.latlng ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingWeather = true) }
            val result = weatherRepository.getCurrentWeather(
                lat = latLng.first,
                lon = latLng.second
            )
            result.onSuccess { weather ->
                _uiState.update { it.copy(weatherInfo = weather, isLoadingWeather = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingWeather = false) }
            }
        }
    }

    private fun loadWikipedia(country: Country) {
        val title = country.name.trim()
        if (title.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingWiki = true) }
            wikipediaRepository.getSummaryForCountryName(title)
                .onSuccess { summary ->
                    _uiState.update { it.copy(wikiSummary = summary, isLoadingWiki = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(wikiSummary = null, isLoadingWiki = false) }
                }
        }
    }

    private fun loadEconomic(country: Country) {
        val iso2 = country.alpha2Code ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEconomic = true) }
            worldBankRepository.getEconomicInfo(iso2)
                .onSuccess { info ->
                    _uiState.update { it.copy(economicInfo = info, isLoadingEconomic = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingEconomic = false) }
                }
        }
    }

    private fun loadExchangeRate(country: Country) {
        val baseCode = extractCurrencyCode(country) ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingExchange = true) }
            val result = exchangeRateRepository.getExchangeRate(
                baseCode = baseCode,
                targetCode = "USD"
            )
            result.onSuccess { info ->
                _uiState.update { it.copy(exchangeInfo = info, isLoadingExchange = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingExchange = false) }
            }
        }
    }

    private fun extractCurrencyCode(country: Country): String? =
        country.currencyCodes.firstOrNull()?.uppercase()?.takeIf { it.length == 3 }
            ?: "USD"
}

data class CountryDetailUiState(
    val country: Country? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val hasLoaded: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val exchangeInfo: ExchangeInfo? = null,
    val isLoadingWeather: Boolean = false,
    val isLoadingExchange: Boolean = false,
    val economicInfo: EconomicInfo? = null,
    val isLoadingEconomic: Boolean = false,
    val wikiSummary: WikiSummary? = null,
    val isLoadingWiki: Boolean = false
)
