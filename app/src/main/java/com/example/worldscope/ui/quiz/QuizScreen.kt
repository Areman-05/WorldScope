@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.worldscope.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val planetAnim = rememberInfiniteTransition(label = "quiz_planet_anim")
                            val planetRotation by planetAnim.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 9000),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "quiz_planet_rotation"
                            )
                            val planetScale by planetAnim.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1400),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "quiz_planet_scale"
                            )
                            Box(
                                modifier = Modifier
                                    .rotate(planetRotation)
                                    .scale(planetScale)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFFF59D),
                                                Color(0xFFFBC02D)
                                            )
                                        )
                                    )
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = null,
                                    tint = WsGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = stringResource(R.string.quiz_title),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge,
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
                        .testTag("quiz_content"),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.quiz_choose_difficulty),
                                style = MaterialTheme.typography.titleMedium,
                                color = WsGreenDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            DifficultyCardsRow(
                                modifier = Modifier.weight(1f),
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
                                Text(
                                    if (state.completed) {
                                        stringResource(R.string.quiz_play_again)
                                    } else {
                                        stringResource(R.string.quiz_start_game)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !state.isLoading && state.error == null && (state.gameStarted || state.completed),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QuizOverlay(
                    state = state,
                    onAnswer = viewModel::answer,
                    onNext = viewModel::nextQuestion,
                    onClose = viewModel::resetGame,
                    onRestart = viewModel::startGame
                )
            }
        }
    }
}

@Composable
private fun DifficultyCardsRow(
    modifier: Modifier = Modifier,
    selected: QuizDifficulty,
    onSelect: (QuizDifficulty) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuizDifficulty.entries.forEach { difficulty ->
            val label = when (difficulty) {
                QuizDifficulty.EASY -> stringResource(R.string.quiz_easy)
                QuizDifficulty.MEDIUM -> stringResource(R.string.quiz_medium)
                QuizDifficulty.HARD -> stringResource(R.string.quiz_hard)
            }
            val subtitle = when (difficulty) {
                QuizDifficulty.EASY -> "Ideal para empezar"
                QuizDifficulty.MEDIUM -> "Ritmo equilibrado"
                QuizDifficulty.HARD -> "Solo para cracks"
            }
            val isSelected = selected == difficulty
            val background = when (difficulty) {
                QuizDifficulty.EASY -> if (isSelected) Color(0xFF2E7D32) else Color(0xFF4E8F53)
                QuizDifficulty.MEDIUM -> if (isSelected) Color(0xFF1565C0) else Color(0xFF4F86C7)
                QuizDifficulty.HARD -> if (isSelected) Color(0xFFB71C1C) else Color(0xFFC25757)
            }
            val icon = when (difficulty) {
                QuizDifficulty.EASY -> Icons.Filled.Spa
                QuizDifficulty.MEDIUM -> Icons.Filled.Bolt
                QuizDifficulty.HARD -> Icons.Filled.LocalFireDepartment
            }
            val targetScale = if (isSelected) 1.02f else 1f
            val cardScale by androidx.compose.animation.core.animateFloatAsState(
                targetValue = targetScale,
                animationSpec = tween(durationMillis = 220),
                label = "difficulty_card_scale_${difficulty.name}"
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .scale(cardScale)
                    .clickable { onSelect(difficulty) }
                    .testTag("quiz_difficulty_${difficulty.name.lowercase()}"),
                colors = CardDefaults.cardColors(
                    containerColor = background
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
            ) {
                val glowAlpha by animateColorAsState(
                    targetValue = if (isSelected) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.06f),
                    animationSpec = tween(durationMillis = 220),
                    label = "difficulty_glow_${difficulty.name}"
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(glowAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${difficulty.questionCount} preguntas",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizOverlay(
    state: QuizUiState,
    onAnswer: (String) -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onRestart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .testTag("quiz_overlay")
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.quiz_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = WsGreenDark
                        )
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
                    }
                }

                if (state.completed) {
                    Text(
                        text = stringResource(R.string.quiz_result_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = WsGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.quiz_result_score, state.score, state.totalQuestions),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WsGreenDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.quiz_play_again))
                    }
                    return@Column
                }

                val target = state.target ?: return@Column
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
                        .height(170.dp)
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
                        label = "quiz_overlay_option_color_$index"
                    )
                    Button(
                        onClick = { onAnswer(option) },
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
                        onClick = onNext,
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
