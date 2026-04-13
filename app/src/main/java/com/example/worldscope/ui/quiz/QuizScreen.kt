@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.worldscope.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.worldscope.R

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quiz_title)) },
                modifier = Modifier.testTag("quiz_topbar")
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.testTag("quiz_loading"))
                    Text(stringResource(R.string.loading))
                }
            }
            state.error != null -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .testTag("quiz_error_column"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error!!, modifier = Modifier.testTag("quiz_error"))
                    Button(onClick = { viewModel.loadCountries() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
            state.pool.size < 4 -> {
                Text(
                    stringResource(R.string.quiz_not_enough_data),
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .testTag("quiz_not_enough")
                )
            }
            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .testTag("quiz_content"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.quiz_score_format, state.score, state.roundsPlayed),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("quiz_score")
                    )
                    OutlinedButton(
                        onClick = { viewModel.resetScore() },
                        modifier = Modifier.testTag("quiz_reset_score")
                    ) {
                        Text(stringResource(R.string.quiz_reset))
                    }
                    if (!state.gameStarted || state.target == null) {
                        Button(
                            onClick = { viewModel.startRound() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quiz_start")
                        ) {
                            Text(stringResource(R.string.quiz_start))
                        }
                    } else {
                        val target = state.target!!
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
                                .testTag("quiz_flag")
                        )
                        state.options.forEachIndexed { index, option ->
                            Button(
                                onClick = { viewModel.answer(option) },
                                enabled = !state.answered,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_option_$index")
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
                            Text(msg, modifier = Modifier.testTag("quiz_feedback"))
                            Button(
                                onClick = { viewModel.startRound() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_next")
                            ) {
                                Text(stringResource(R.string.quiz_next))
                            }
                        }
                    }
                }
            }
        }
    }
}
