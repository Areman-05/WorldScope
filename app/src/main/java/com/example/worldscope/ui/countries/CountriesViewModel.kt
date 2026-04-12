package com.example.worldscope.ui.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.data.local.entity.RecentCountryEntity
import com.example.worldscope.domain.model.Country
import com.example.worldscope.data.repository.CountriesRepository
import com.example.worldscope.data.repository.RecentCountriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val repository: CountriesRepository,
    private val recentCountriesRepository: RecentCountriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountriesUiState())
    val uiState: StateFlow<CountriesUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
        viewModelScope.launch {
            recentCountriesRepository.observeRecent().collect { list ->
                _uiState.update { it.copy(recentVisits = list) }
            }
        }
    }

    fun loadCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, hasLoaded = false) }
            repository.getAllCountries().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val availableRegions = getAvailableRegions(list)
                        _uiState.update { state ->
                            val filtered = applyFilters(
                                list = list,
                                searchQuery = state.searchQuery,
                                regionFilter = state.regionFilter,
                                sortMode = state.sortMode
                            )
                            state.copy(
                                countries = list,
                                availableRegions = availableRegions,
                                filteredCountries = filtered,
                                hasActiveFilters = hasActiveFilters(
                                    state.searchQuery,
                                    state.regionFilter,
                                    state.sortMode
                                ),
                                isLoading = false,
                                error = null,
                                hasLoaded = true
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                countries = emptyList(),
                                filteredCountries = emptyList(),
                                availableRegions = emptyList(),
                                hasActiveFilters = false,
                                isLoading = false,
                                error = e.message,
                                hasLoaded = true
                            )
                        }
                    }
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        val normalizedQuery = query.trimStart()
        if (_uiState.value.searchQuery == normalizedQuery) return
        _uiState.update { state ->
            state.copy(
                searchQuery = normalizedQuery,
                hasActiveFilters = hasActiveFilters(normalizedQuery, state.regionFilter, state.sortMode),
                filteredCountries = applyFilters(
                    list = state.countries,
                    searchQuery = normalizedQuery,
                    regionFilter = state.regionFilter,
                    sortMode = state.sortMode
                )
            )
        }
    }

    fun updateRegionFilter(region: String?) {
        if (_uiState.value.regionFilter == region) return
        _uiState.update { state ->
            state.copy(
                regionFilter = region,
                hasActiveFilters = hasActiveFilters(state.searchQuery, region, state.sortMode),
                filteredCountries = applyFilters(
                    list = state.countries,
                    searchQuery = state.searchQuery,
                    regionFilter = region,
                    sortMode = state.sortMode
                )
            )
        }
    }

    fun updateSortMode(sortMode: SortMode) {
        if (_uiState.value.sortMode == sortMode) return
        _uiState.update { state ->
            state.copy(
                sortMode = sortMode,
                hasActiveFilters = hasActiveFilters(state.searchQuery, state.regionFilter, sortMode),
                filteredCountries = applyFilters(
                    list = state.countries,
                    searchQuery = state.searchQuery,
                    regionFilter = state.regionFilter,
                    sortMode = sortMode
                )
            )
        }
    }

    fun clearFilters() {
        if (!_uiState.value.hasActiveFilters) return
        val clearedQuery = ""
        val clearedRegion: String? = null
        val clearedSort = SortMode.NAME
        _uiState.update { state ->
            state.copy(
                searchQuery = clearedQuery,
                regionFilter = clearedRegion,
                sortMode = clearedSort,
                hasActiveFilters = false,
                filteredCountries = applyFilters(
                    list = state.countries,
                    searchQuery = clearedQuery,
                    regionFilter = clearedRegion,
                    sortMode = clearedSort
                )
            )
        }
    }

    private fun getAvailableRegions(list: List<Country>): List<String> =
        list.mapNotNull { it.region }.distinct().sorted()

    private fun hasActiveFilters(
        searchQuery: String,
        regionFilter: String?,
        sortMode: SortMode
    ): Boolean = searchQuery.isNotBlank() || regionFilter != null || sortMode != SortMode.NAME

    private fun applyFilters(
        list: List<Country>,
        searchQuery: String,
        regionFilter: String?,
        sortMode: SortMode
    ): List<Country> {
        val byQuery = if (searchQuery.isBlank()) {
            list
        } else {
            list.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        val byRegion = if (regionFilter == null) {
            byQuery
        } else {
            byQuery.filter { it.region?.equals(regionFilter, ignoreCase = true) == true }
        }

        return when (sortMode) {
            SortMode.NAME -> byRegion.sortedBy { it.name }
            SortMode.POPULATION -> byRegion.sortedWith(
                compareByDescending<Country> { it.population }.thenBy { it.name }
            )
        }
    }
}

enum class SortMode {
    NAME,
    POPULATION
}

data class CountriesUiState(
    val recentVisits: List<RecentCountryEntity> = emptyList(),
    val countries: List<Country> = emptyList(),
    val filteredCountries: List<Country> = emptyList(),
    val availableRegions: List<String> = emptyList(),
    val regionFilter: String? = null,
    val sortMode: SortMode = SortMode.NAME,
    val hasActiveFilters: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasLoaded: Boolean = false,
    val searchQuery: String = ""
)
