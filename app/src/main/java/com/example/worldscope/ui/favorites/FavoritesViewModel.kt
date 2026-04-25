package com.example.worldscope.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.data.repository.CountriesRepository
import com.example.worldscope.data.repository.FavoriteGroupData
import com.example.worldscope.data.repository.FavoritesRepository
import com.example.worldscope.domain.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val countriesRepository: CountriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.getAllFavorites().collect { list ->
                _uiState.update { state -> state.copy(favorites = list, hasLoaded = true) }
            }
        }
        viewModelScope.launch {
            favoritesRepository.observeFavoriteGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups.map(FavoriteGroupData::toUi)) }
            }
        }
        viewModelScope.launch {
            countriesRepository.getAllCountries().collect { result ->
                result.onSuccess { countries ->
                    val candidates = countries
                        .filter { !it.alpha2Code.isNullOrBlank() }
                        .distinctBy { it.alpha2Code }
                        .sortedBy { it.name }
                        .map { it.toCandidate() }
                    _uiState.update { it.copy(allCountries = candidates) }
                }
            }
        }
    }

    fun removeFavorite(alpha2Code: String) {
        val normalizedCode = alpha2Code.trim()
        if (normalizedCode.isBlank()) return
        viewModelScope.launch {
            favoritesRepository.removeFavorite(normalizedCode)
        }
    }

    fun updateNewGroupName(value: String) {
        _uiState.update { it.copy(newGroupName = value) }
    }

    fun createGroup() {
        val groupName = _uiState.value.newGroupName.trim()
        if (groupName.isBlank()) return
        viewModelScope.launch {
            favoritesRepository.createGroup(groupName)
            _uiState.update { it.copy(newGroupName = "") }
        }
    }

    fun toggleCountryInGroup(groupId: Long, alpha2Code: String) {
        viewModelScope.launch {
            favoritesRepository.toggleCountryInGroup(groupId, alpha2Code)
        }
    }

    fun addCountryToGroup(groupId: Long, alpha2Code: String) {
        viewModelScope.launch {
            val country = _uiState.value.allCountries
                .firstOrNull { it.alpha2Code == alpha2Code }
                ?.toDomainCountry() ?: return@launch
            favoritesRepository.addCountryToGroup(country, groupId)
        }
    }

    fun removeCountryFromGroup(groupId: Long, alpha2Code: String) {
        viewModelScope.launch {
            favoritesRepository.removeCountryFromGroup(groupId, alpha2Code)
        }
    }

    fun removeGroup(groupId: Long) {
        viewModelScope.launch {
            favoritesRepository.removeGroup(groupId)
        }
    }
}

data class FavoritesUiState(
    val favorites: List<FavoriteCountryEntity> = emptyList(),
    val groups: List<FavoriteGroup> = emptyList(),
    val allCountries: List<CountryCandidate> = emptyList(),
    val newGroupName: String = "",
    val hasLoaded: Boolean = false
)

data class FavoriteGroup(
    val id: Long,
    val name: String,
    val countryCodes: Set<String> = emptySet()
)

private fun FavoriteGroupData.toUi(): FavoriteGroup =
    FavoriteGroup(
        id = id,
        name = name,
        countryCodes = countryCodes
    )

data class CountryCandidate(
    val name: String,
    val alpha2Code: String,
    val flagUrl: String?
)

private fun Country.toCandidate(): CountryCandidate =
    CountryCandidate(
        name = name,
        alpha2Code = alpha2Code ?: "",
        flagUrl = flagUrl
    )

private fun CountryCandidate.toDomainCountry(): Country =
    Country(
        name = name,
        capital = null,
        region = null,
        subregion = null,
        population = 0L,
        areaKm2 = null,
        flagUrl = flagUrl,
        languages = emptyList(),
        currencies = emptyList(),
        currencyCodes = emptyList(),
        alpha2Code = alpha2Code,
        alpha3Code = null,
        latlng = null
    )
