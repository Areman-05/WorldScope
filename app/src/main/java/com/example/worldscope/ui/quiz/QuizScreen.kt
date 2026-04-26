@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.worldscope.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.worldscope.R
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsGreenLight
import com.example.worldscope.ui.theme.WsSurfaceSoft

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = WsSurfaceSoft,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(WsGreen, WsGreenDark)
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("quiz_topbar")
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.quiz_title),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .testTag("quiz_root")
        ) {
            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QuizInfoCard(
                    modifier = Modifier.align(Alignment.Center),
                    testTag = "quiz_loading"
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.loading))
                    }
                }
            }

            AnimatedVisibility(
                visible = !state.isLoading && state.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QuizInfoCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("quiz_error_column")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(state.error!!, modifier = Modifier.testTag("quiz_error"))
                        Button(onClick = { viewModel.loadCountries() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !state.isLoading && state.error == null && state.pool.size < 4,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QuizInfoCard(
                    modifier = modifier
                        .align(Alignment.Center)
                        .testTag("quiz_not_enough")
                ) {
                    Text(stringResource(R.string.quiz_not_enough_data))
                }
            }

            AnimatedVisibility(
                visible = !state.isLoading && state.error == null && state.pool.size >= 4,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .testTag("quiz_content"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.quiz_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = WsGreenDark,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stringResource(R.string.quiz_score_format, state.score, state.roundsPlayed),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.testTag("quiz_score")
                            )
                            OutlinedButton(
                                onClick = { viewModel.resetGame() },
                                modifier = Modifier.testTag("quiz_reset_score")
                            ) {
                                Text(stringResource(R.string.quiz_reset))
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.quiz_choose_difficulty),
                                style = MaterialTheme.typography.titleMedium,
                                color = WsGreenDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            DifficultyChips(
                                selected = state.selectedDifficulty,
                                onSelect = viewModel::selectDifficulty
                            )
                            Text(
                                text = stringResource(
                                    R.string.quiz_questions_count,
                                    state.selectedDifficulty.questionCount
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4F6F4F)
                            )
                            if (state.completed) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF8EF)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quiz_result_title),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = WsGreenDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.quiz_result_score,
                                                state.score,
                                                state.totalQuestions
                                            ),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = WsGreenDark
                                        )
                                    }
                                }
                                Button(
                                    onClick = { viewModel.startGame() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quiz_play_again"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WsGreenDark,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(stringResource(R.string.quiz_play_again))
                                }
                            } else if (!state.gameStarted || state.target == null) {
                                Button(
                                    onClick = { viewModel.startGame() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quiz_start"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WsGreen,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(stringResource(R.string.quiz_start_game))
                                }
                            } else {
                                val target = state.target!!
                                Text(
                                    text = stringResource(
                                        R.string.quiz_progress,
                                        state.currentQuestionIndex + 1,
                                        state.totalQuestions
                                    ),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = WsGreenDark,
                                    modifier = Modifier.testTag("quiz_progress")
                                )
                                Text(
                                    stringResource(R.string.quiz_question_capital, target.name),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.testTag("quiz_question")
                                )
                                AsyncImage(
                                    model = target.flagUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .testTag("quiz_flag")
                                )
                                state.options.forEachIndexed { index, option ->
                                    val isCorrect = option.equals(state.correctCapital, ignoreCase = true)
                                    val isSelected = state.selectedChoice == option
                                    val targetColor = when {
                                        !state.answered -> Color.White
                                        isCorrect -> Color(0xFFD8F3DC)
                                        isSelected -> Color(0xFFFFE0E0)
                                        else -> Color(0xFFF4F7F4)
                                    }
                                    val containerColor by animateColorAsState(
                                        targetValue = targetColor,
                                        animationSpec = tween(durationMillis = 260),
                                        label = "quiz_option_color_$index"
                                    )
                                    Button(
                                        onClick = { viewModel.answer(option) },
                                        enabled = !state.answered,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("quiz_option_$index"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = containerColor,
                                            contentColor = WsGreenDark,
                                            disabledContainerColor = containerColor,
                                            disabledContentColor = WsGreenDark
                                        )
                                    ) {
                                        Text(option)
                                    }
                                }
                                if (state.answered) {
                                    val msg = if (state.lastCorrect == true) {
                                        stringResource(R.string.quiz_correct)
                                    } else {
                                        stringResource(R.string.quiz_wrong)
                                    }
                                    Text(
                                        msg,
                                        modifier = Modifier.testTag("quiz_feedback"),
                                        color = if (state.lastCorrect == true) WsGreen else Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = { viewModel.nextQuestion() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("quiz_next"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = WsGreenDark,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(stringResource(R.string.quiz_next))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DifficultyChips(
    selected: QuizDifficulty,
    onSelect: (QuizDifficulty) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuizDifficulty.entries.forEach { difficulty ->
            val label = when (difficulty) {
                QuizDifficulty.EASY -> stringResource(R.string.quiz_easy)
                QuizDifficulty.MEDIUM -> stringResource(R.string.quiz_medium)
                QuizDifficulty.HARD -> stringResource(R.string.quiz_hard)
            }
            FilterChip(
                selected = selected == difficulty,
                onClick = { onSelect(difficulty) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WsGreenLight,
                    selectedLabelColor = WsGreenDark
                ),
                modifier = Modifier.testTag("quiz_difficulty_${difficulty.name.lowercase()}")
            )
        }
    }
}

@Composable
private fun QuizInfoCard(
    modifier: Modifier = Modifier,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val finalModifier = if (testTag != null) modifier.testTag(testTag) else modifier
    Surface(
        modifier = finalModifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}
