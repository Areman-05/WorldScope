package com.example.worldscope.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.data.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.getAllFavorites().collect { list ->
                _uiState.update { it.copy(favorites = list, hasLoaded = true) }
            }
        }
    }

    fun removeFavorite(alpha2Code: String) {
        if (alpha2Code.isBlank()) return
        viewModelScope.launch {
            favoritesRepository.removeFavorite(alpha2Code)
        }
    }
}

data class FavoritesUiState(
    val favorites: List<FavoriteCountryEntity> = emptyList(),
    val hasLoaded: Boolean = false
)
