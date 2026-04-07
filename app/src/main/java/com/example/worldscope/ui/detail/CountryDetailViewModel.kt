package com.example.worldscope.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.domain.model.Country
import com.example.worldscope.data.repository.CountriesRepository
import com.example.worldscope.data.repository.FavoritesRepository
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val code: String = savedStateHandle.get<String>("code") ?: ""

    private val _uiState = MutableStateFlow(CountryDetailUiState())
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    init {
        loadCountry()
    }

    fun toggleFavorite() {
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
        if (code.isBlank() || _uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCountryByCode(code)
                .onSuccess { country ->
                    val isFav = favoritesRepository.isFavorite(country.alpha2Code ?: "")
                    _uiState.update {
                        it.copy(country = country, isLoading = false, error = null, isFavorite = isFav)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
        }
    }
}

data class CountryDetailUiState(
    val country: Country? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val hasLoaded: Boolean = false
)
