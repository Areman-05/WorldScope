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

enum class QuizDifficulty(val questionCount: Int) {
    EASY(10),
    MEDIUM(15),
    HARD(20)
}

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

    fun selectDifficulty(difficulty: QuizDifficulty) {
        _uiState.update { state ->
            if (state.gameStarted) state else state.copy(selectedDifficulty = difficulty)
        }
    }

    fun startGame() {
        val state = _uiState.value
        val restarted = state.copy(
            score = 0,
            roundsPlayed = 0,
            target = null,
            options = emptyList(),
            correctCapital = null,
            answered = false,
            lastCorrect = null,
            gameStarted = true,
            completed = false,
            currentQuestionIndex = 0,
            totalQuestions = state.selectedDifficulty.questionCount
        )
        _uiState.value = buildRound(restarted) ?: restarted.copy(gameStarted = false)
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.gameStarted) return
        if (!state.answered) return

        val nextIndex = state.currentQuestionIndex + 1
        if (nextIndex >= state.totalQuestions) {
            _uiState.update {
                it.copy(
                    gameStarted = false,
                    completed = true,
                    target = null,
                    options = emptyList(),
                    correctCapital = null,
                    answered = false
                )
            }
            return
        }

        val nextState = state.copy(
            currentQuestionIndex = nextIndex,
            target = null,
            options = emptyList(),
            correctCapital = null,
            answered = false,
            lastCorrect = null
        )
        _uiState.value = buildRound(nextState) ?: nextState.copy(gameStarted = false)
    }

    private fun buildRound(state: QuizUiState): QuizUiState? {
        val pool = state.pool
        if (pool.size < 4) return null
        val target = pool.random(random)
        val correct = target.capital!!.trim()
        val wrongOptions = pool
            .filter { it.alpha2Code != target.alpha2Code }
            .mapNotNull { it.capital?.trim() }
            .distinct()
            .filter { it != correct }
            .shuffled(random)
            .take(3)
        if (wrongOptions.size < 3) return null
        val options = (wrongOptions + correct).shuffled(random)
        return state.copy(
            target = target,
            options = options,
            correctCapital = correct,
            answered = false,
            lastCorrect = null,
            selectedChoice = null
        )
    }

    fun answer(choice: String) {
        val s = _uiState.value
        if (s.answered || s.correctCapital == null || !s.gameStarted) return
        val ok = choice.equals(s.correctCapital, ignoreCase = true)
        _uiState.update {
            it.copy(
                answered = true,
                lastCorrect = ok,
                selectedChoice = choice,
                score = if (ok) it.score + 1 else it.score,
                roundsPlayed = it.roundsPlayed + 1
            )
        }
    }

    fun resetGame() {
        _uiState.update {
            it.copy(
                score = 0,
                roundsPlayed = 0,
                target = null,
                options = emptyList(),
                correctCapital = null,
                answered = false,
                lastCorrect = null,
                selectedChoice = null,
                gameStarted = false,
                completed = false,
                currentQuestionIndex = 0,
                totalQuestions = it.selectedDifficulty.questionCount
            )
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
    val selectedChoice: String? = null,
    val score: Int = 0,
    val roundsPlayed: Int = 0,
    val gameStarted: Boolean = false,
    val selectedDifficulty: QuizDifficulty = QuizDifficulty.EASY,
    val totalQuestions: Int = QuizDifficulty.EASY.questionCount,
    val currentQuestionIndex: Int = 0,
    val completed: Boolean = false
)
