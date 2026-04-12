package com.example.worldscope.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.data.repository.CountriesRepository
import com.example.worldscope.data.repository.WorldBankRepository
import com.example.worldscope.domain.model.Country
import com.example.worldscope.domain.model.EconomicInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val worldBankRepository: WorldBankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    init {
        loadCountryList()
    }

    fun loadCountryList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingList = true, listError = null) }
            countriesRepository.getAllCountries().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val sorted = list.sortedBy { it.name }
                        _uiState.update {
                            it.copy(
                                allCountries = sorted,
                                isLoadingList = false,
                                listError = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                allCountries = emptyList(),
                                isLoadingList = false,
                                listError = e.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun updateCodeA(code: String?) {
        _uiState.update {
            it.copy(
                codeA = code,
                countryA = null,
                countryB = null,
                economicA = null,
                economicB = null,
                compareUserError = null
            )
        }
    }

    fun updateCodeB(code: String?) {
        _uiState.update {
            it.copy(
                codeB = code,
                countryA = null,
                countryB = null,
                economicA = null,
                economicB = null,
                compareUserError = null
            )
        }
    }

    fun runCompare() {
        val codeA = _uiState.value.codeA
        val codeB = _uiState.value.codeB
        if (codeA.isNullOrBlank() || codeB.isNullOrBlank()) {
            _uiState.update { it.copy(compareUserError = CompareUserError.NeedTwo) }
            return
        }
        if (codeA.equals(codeB, ignoreCase = true)) {
            _uiState.update { it.copy(compareUserError = CompareUserError.SameCountry) }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingCompare = true,
                    compareUserError = null,
                    loadError = null,
                    countryA = null,
                    countryB = null,
                    economicA = null,
                    economicB = null
                )
            }
            val resA = countriesRepository.getCountryByCode(codeA)
            val resB = countriesRepository.getCountryByCode(codeB)
            if (resA.isFailure || resB.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoadingCompare = false,
                        loadError = resA.exceptionOrNull()?.message
                            ?: resB.exceptionOrNull()?.message
                    )
                }
                return@launch
            }
            val ca = resA.getOrNull()!!
            val cb = resB.getOrNull()!!
            val (ea, eb) = coroutineScope {
                val jobA = async {
                    ca.alpha2Code?.let { worldBankRepository.getEconomicInfo(it).getOrNull() }
                }
                val jobB = async {
                    cb.alpha2Code?.let { worldBankRepository.getEconomicInfo(it).getOrNull() }
                }
                Pair(jobA.await(), jobB.await())
            }
            _uiState.update {
                it.copy(
                    countryA = ca,
                    countryB = cb,
                    economicA = ea,
                    economicB = eb,
                    isLoadingCompare = false
                )
            }
        }
    }
}

enum class CompareUserError {
    NeedTwo,
    SameCountry
}

data class CompareUiState(
    val allCountries: List<Country> = emptyList(),
    val isLoadingList: Boolean = false,
    val listError: String? = null,
    val codeA: String? = null,
    val codeB: String? = null,
    val countryA: Country? = null,
    val countryB: Country? = null,
    val economicA: EconomicInfo? = null,
    val economicB: EconomicInfo? = null,
    val isLoadingCompare: Boolean = false,
    val compareUserError: CompareUserError? = null,
    val loadError: String? = null
)
