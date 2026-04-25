package com.example.worldscope.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.data.repository.FavoriteGroupData
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
                _uiState.update { state -> state.copy(favorites = list, hasLoaded = true) }
            }
        }
        viewModelScope.launch {
            favoritesRepository.observeFavoriteGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups.map(FavoriteGroupData::toUi)) }
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
            favoritesRepository.addCountryToGroup(groupId, alpha2Code)
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
