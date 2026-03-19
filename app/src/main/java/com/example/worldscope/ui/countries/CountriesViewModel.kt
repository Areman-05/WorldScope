package com.example.worldscope.ui.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.domain.model.Country
import com.example.worldscope.data.repository.CountriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountriesUiState())
    val uiState: StateFlow<CountriesUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    fun loadCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
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
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = e.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredCountries = applyFilters(
                    list = state.countries,
                    searchQuery = query,
                    regionFilter = state.regionFilter,
                    sortMode = state.sortMode
                )
            )
        }
    }

    fun updateRegionFilter(region: String?) {
        _uiState.update { state ->
            state.copy(
                regionFilter = region,
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
        _uiState.update { state ->
            state.copy(
                sortMode = sortMode,
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
        val clearedQuery = ""
        val clearedRegion: String? = null
        val clearedSort = SortMode.NAME
        _uiState.update { state ->
            state.copy(
                searchQuery = clearedQuery,
                regionFilter = clearedRegion,
                sortMode = clearedSort,
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
    val countries: List<Country> = emptyList(),
    val filteredCountries: List<Country> = emptyList(),
    val availableRegions: List<String> = emptyList(),
    val regionFilter: String? = null,
    val sortMode: SortMode = SortMode.NAME,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)
