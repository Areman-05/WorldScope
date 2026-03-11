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
                        _uiState.update {
                            it.copy(
                                countries = list,
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
        _uiState.update { it.copy(searchQuery = query) }
    }
}

data class CountriesUiState(
    val countries: List<Country> = emptyList(),
    val filteredCountries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)
