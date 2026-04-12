package com.example.worldscope.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldscope.data.repository.CountriesRepository
import com.example.worldscope.domain.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val random = Random(System.currentTimeMillis())

    init {
        loadCountries()
    }

    fun loadCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            countriesRepository.getAllCountries().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val withCapitals = list.filter { !it.capital.isNullOrBlank() }
                        _uiState.update {
                            it.copy(
                                pool = withCapitals,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                pool = emptyList(),
                                isLoading = false,
                                error = e.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun startRound() {
        val pool = _uiState.value.pool
        if (pool.size < 4) return
        val target = pool.random(random)
        val correct = target.capital!!.trim()
        val wrongOptions = pool
            .filter { it.alpha2Code != target.alpha2Code }
            .mapNotNull { it.capital?.trim() }
            .distinct()
            .filter { it != correct }
            .shuffled(random)
            .take(3)
        if (wrongOptions.size < 3) return
        val options = (wrongOptions + correct).shuffled(random)
        _uiState.update {
            it.copy(
                target = target,
                options = options,
                correctCapital = correct,
                answered = false,
                lastCorrect = null,
                gameStarted = true
            )
        }
    }

    fun answer(choice: String) {
        val s = _uiState.value
        if (s.answered || s.correctCapital == null) return
        val ok = choice.equals(s.correctCapital, ignoreCase = true)
        _uiState.update {
            it.copy(
                answered = true,
                lastCorrect = ok,
                score = if (ok) it.score + 1 else it.score,
                roundsPlayed = it.roundsPlayed + 1
            )
        }
    }

    fun resetScore() {
        _uiState.update {
            it.copy(score = 0, roundsPlayed = 0, target = null, options = emptyList(), answered = false)
        }
    }
}

data class QuizUiState(
    val pool: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val target: Country? = null,
    val options: List<String> = emptyList(),
    val correctCapital: String? = null,
    val answered: Boolean = false,
    val lastCorrect: Boolean? = null,
    val score: Int = 0,
    val roundsPlayed: Int = 0,
    val gameStarted: Boolean = false
)
