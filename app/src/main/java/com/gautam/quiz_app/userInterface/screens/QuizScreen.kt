package com.gautam.quiz_app.userInterface.screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

@Composable
fun QuizScreen(
    navController: NavController,
    section: String,
    difficulty: String,
    questionCount: Int,
    timerPerQuestion: Int,
    isRandom: Boolean,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    // Trigger fetch once on entry
    LaunchedEffect(section, isRandom) {
        if (isRandom) viewModel.fetchRandomQuestions(section, questionCount, difficulty)
        else          viewModel.fetchQuestions(section, questionCount, difficulty)
    }

    val uiState by viewModel.quizUiState.collectAsState()

    // Drive the countdown from the ViewModel
    LaunchedEffect(uiState.currentIndex, uiState.isLoading) {
        if (!uiState.isLoading && uiState.questions.isNotEmpty()) {
            viewModel.startTimer(timerPerQuestion)
        }
    }

    when {
        uiState.isLoading -> LoadingContent()
        uiState.error != null -> ErrorContent(uiState.error!!) {
            navController.popBackStack()
        }
        uiState.questions.isEmpty() -> ErrorContent("No questions found.") {
            navController.popBackStack()
        }
        else -> QuizContent(
            uiState          = uiState,
            timerPerQuestion = timerPerQuestion,
            onAnswerSelected = { viewModel.selectAnswer(it) },
            onPrev           = { viewModel.goToPrev() },
            onNext           = { viewModel.goToNext(timerPerQuestion) },
            onSubmit = {
                viewModel.submitQuiz(timerPerQuestion * uiState.questions.size)
                navController.navigate("quizResult/$section/$difficulty") {
                    popUpTo("quizPlay/{section}/{difficulty}/{questionCount}/{timerPerQuestion}/{isRandom}") {
                        inclusive = true
                    }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }
}

// ── Main quiz content ──────────────────────────────────────────────────────────

@Composable
private fun QuizContent(
    uiState: com.gautam.quiz_app.userInterface.viewModel.QuizUiState,
    timerPerQuestion: Int,
    onAnswerSelected: (String) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val currentIndex = uiState.currentIndex
    val total        = uiState.questions.size
    val question     = uiState.questions.getOrNull(currentIndex) ?: return
    val chosen       = uiState.answers[currentIndex]
    val timeLeft     = uiState.timeLeft
    val isLastQ      = currentIndex == total - 1

    // Animate progress bar
    val animatedProgress by animateFloatAsState(
        targetValue      = (currentIndex + 1).toFloat() / total,
        animationSpec    = tween(400),
        label            = "quizProgress"
    )

    // Timer colour: green → amber → red
    val timerColor = when {
        timeLeft > timerPerQuestion * 0.5  -> Color(0xFF0F6E56)
        timeLeft > timerPerQuestion * 0.25 -> Color(0xFF854F0B)
        else                                -> Color(0xFF993C1D)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Top bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.weight(1f))
            Text(
                "${currentIndex + 1} / $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            // Timer badge
            TimerBadge(timeLeft = timeLeft, color = timerColor)
        }

        // ── Progress bar ───────────────────────────────────────────────────
        LinearProgressIndicator(
            progress         = { animatedProgress },
            modifier         = Modifier.fillMaxWidth().height(4.dp),
            color            = MaterialTheme.colorScheme.primary,
            trackColor       = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap        = ProgressIndicatorDefaults.LinearStrokeCap
        )

        // ── Timer track bar ────────────────────────────────────────────────
        val animatedTimer by animateFloatAsState(
            targetValue   = timeLeft.toFloat() / timerPerQuestion,
            animationSpec = tween(900),
            label         = "timerTrack"
        )
        LinearProgressIndicator(
            progress      = { animatedTimer },
            modifier      = Modifier.fillMaxWidth().height(3.dp),
            color         = timerColor,
            trackColor    = timerColor.copy(alpha = 0.15f),
            strokeCap     = ProgressIndicatorDefaults.LinearStrokeCap
        )

        // ── Question + options (animated slide) ────────────────────────────
        AnimatedContent(
            targetState   = currentIndex,
            transitionSpec = {
                if (targetState > initialState)
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                else
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
            },
            modifier = Modifier.weight(1f),
            label    = "questionSlide"
        ) { index ->
            val q      = uiState.questions.getOrNull(index) ?: return@AnimatedContent
            val answer = uiState.answers[index]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Question label + text
                Text(
                    "Question ${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text     = q.questionText ?: "",
                        modifier = Modifier.padding(16.dp),
                        style    = MaterialTheme.typography.titleMedium,
                        lineHeight = 26.sp,
                        color    = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Options
                q.options.forEach { option ->
                    OptionCard(
                        option   = option,
                        chosen   = answer,
                        correct  = null, // reveal only on submit/result screen
                        onClick  = { if (answer == null) onAnswerSelected(option) }
                    )
                }
            }
        }

        // ── Navigation buttons ─────────────────────────────────────────────
        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentIndex > 0) {
                    OutlinedButton(
                        onClick  = onPrev,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) { Text("← Prev") }
                }

                if (!isLastQ) {
                    Button(
                        onClick  = onNext,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) { Text("Next →") }
                } else {
                    Button(
                        onClick  = onSubmit,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled  = uiState.answers.size == total
                    ) {
                        Text("Submit", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Option card ────────────────────────────────────────────────────────────────

@Composable
private fun OptionCard(
    option: String,
    chosen: String?,
    correct: String?,        // pass null during quiz; pass correct answer on review
    onClick: () -> Unit
) {
    val isChosen  = chosen == option
    val isCorrect = correct != null && option == correct
    val isWrong   = correct != null && isChosen && !isCorrect

    val containerColor by animateColorAsState(
        targetValue = when {
            isCorrect -> Color(0xFFE1F5EE)
            isWrong   -> Color(0xFFFAECE7)
            isChosen  -> MaterialTheme.colorScheme.primaryContainer
            else      -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200), label = "optionBg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrect -> Color(0xFF0F6E56)
            isWrong   -> Color(0xFF993C1D)
            isChosen  -> MaterialTheme.colorScheme.primary
            else      -> MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(200), label = "optionBorder"
    )

    Surface(
        onClick       = onClick,
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(12.dp),
        color         = containerColor,
        border        = BorderStroke(
            width = if (isChosen || isCorrect) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier           = Modifier.padding(14.dp),
            verticalAlignment  = Alignment.CenterVertically
        ) {
            Text(
                text     = option,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurface
            )
            if (isCorrect) {
                Icon(
                    Icons.Default.Check, null,
                    tint     = Color(0xFF0F6E56),
                    modifier = Modifier.size(18.dp)
                )
            } else if (isWrong) {
                Icon(
                    Icons.Default.Close, null,
                    tint     = Color(0xFF993C1D),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Timer badge ────────────────────────────────────────────────────────────────

@Composable
private fun TimerBadge(timeLeft: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text     = "${timeLeft}s",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color    = color,
            style    = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Loading / Error ────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Go Back") }
        }
    }
}