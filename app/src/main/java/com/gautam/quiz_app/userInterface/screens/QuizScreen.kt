package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel
import com.gautam.quiz_app.userInterface.viewModel.QuizUiState

// ── Theme tokens ───────────────────────────────────────────────────────────────

private val BgDeep      = Color(0xFF0A0A0F)
private val BgCard      = Color(0xFF13131A)
private val Surface1    = Color(0xFF1C1C27)
private val Surface2    = Color(0xFF1E1E2E)
private val Border      = Color(0xFF2A2A3A)
private val AccentStart = Color(0xFF7C3AED)
private val AccentEnd   = Color(0xFF4F8EF7)
private val AccentMid   = Color(0xFF9B5CF6)
private val TextPrimary = Color(0xFFF0F0FF)
private val TextMuted   = Color(0xFF8888AA)

// ── Glow modifier ──────────────────────────────────────────────────────────────

private fun Modifier.glowEffect(color: Color, radius: Dp, alpha: Float = 0.45f): Modifier =
    this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    this.color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(radius.toPx(), 0f, 0f, color.copy(alpha = alpha).toArgb())
                }
            }
            canvas.drawRoundRect(
                left    = -radius.toPx() / 2,
                top     = -radius.toPx() / 2,
                right   = size.width  + radius.toPx() / 2,
                bottom  = size.height + radius.toPx() / 2,
                radiusX = 16.dp.toPx(),
                radiusY = 16.dp.toPx(),
                paint   = paint
            )
        }
    }

// ── Entry point ────────────────────────────────────────────────────────────────

@Composable
fun QuizScreen(
    navController: NavController,
    section: String,
    difficulty: String,
    questionCount: Int,
    timerPerQuestion: Int,
    isRandom: Boolean,
    viewModel: QuestionViewModel
) {
    LaunchedEffect(section, isRandom) {
        if (isRandom) viewModel.fetchRandomQuestions(section, questionCount, difficulty)
        else          viewModel.fetchQuestions(section, questionCount, difficulty)
    }

    val uiState by viewModel.quizUiState.collectAsState()

    LaunchedEffect(uiState.currentIndex, uiState.isLoading) {
        if (!uiState.isLoading && uiState.questions.isNotEmpty()) {
            viewModel.startTimer(timerPerQuestion)
        }
    }

    when {
        uiState.isLoading -> LoadingContent()
        uiState.error != null -> ErrorContent(uiState.error!!) { navController.popBackStack() }
        uiState.questions.isEmpty() -> ErrorContent("No questions found.") { navController.popBackStack() }
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
    uiState: QuizUiState,
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
    val timeLeft     = uiState.timeLeft
    val isLastQ      = currentIndex == total - 1

    val animatedProgress by animateFloatAsState(
        targetValue   = (currentIndex + 1).toFloat() / total,
        animationSpec = tween(400),
        label         = "quizProgress"
    )
    val animatedTimer by animateFloatAsState(
        targetValue   = timeLeft.toFloat() / timerPerQuestion,
        animationSpec = tween(900),
        label         = "timerTrack"
    )

    val timerColor = when {
        timeLeft > timerPerQuestion * 0.5  -> Color(0xFF0F6E56)
        timeLeft > timerPerQuestion * 0.25 -> Color(0xFF854F0B)
        else                                -> Color(0xFF993C1D)
    }

    // Entry animation
    val offsetY by produceState(initialValue = 40f) {
        animate(40f, 0f, animationSpec = tween(600, easing = EaseOutCubic)) { v, _ -> value = v }
    }
    val contentAlpha by produceState(initialValue = 0f) {
        animate(0f, 1f, animationSpec = tween(600, easing = EaseOutCubic)) { v, _ -> value = v }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        // Decorative orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AccentStart.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.05f),
                    radius = size.width * 0.55f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.05f),
                radius = size.width * 0.55f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AccentEnd.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.8f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.8f),
                radius = size.width * 0.5f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .offset(y = offsetY.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface1)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                // Question counter chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Surface1)
                        .drawBehind {
                            drawRoundRect(
                                color        = Border,
                                cornerRadius = CornerRadius(20.dp.toPx()),
                                style        = Stroke(width = 0.8.dp.toPx())
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${currentIndex + 1}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = " / $total",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Timer badge
                TimerBadge(timeLeft = timeLeft, color = timerColor)
            }

            // ── Progress bar ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Surface1)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(AccentStart, AccentEnd)))
                )
            }

            Spacer(Modifier.height(4.dp))

            // ── Timer track bar ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(timerColor.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedTimer)
                        .clip(RoundedCornerShape(50))
                        .background(timerColor)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Question + options (animated slide) ────────────────────────
            AnimatedContent(
                targetState = currentIndex,
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section label
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(16.dp)
                                .background(
                                    Brush.verticalGradient(listOf(AccentStart, AccentEnd)),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Question ${index + 1}",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Question card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Surface1)
                            .drawBehind {
                                drawRoundRect(
                                    color        = Border,
                                    cornerRadius = CornerRadius(16.dp.toPx()),
                                    style        = Stroke(width = 0.8.dp.toPx())
                                )
                            }
                    ) {
                        // Left accent bar
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(AccentStart, AccentEnd)
                                    )
                                )
                        )
                        Text(
                            text       = q.questionText ?: "",
                            modifier   = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 26.sp,
                            color      = TextPrimary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Options
                    q.options.forEach { option ->
                        OptionCard(
                            option  = option,
                            chosen  = answer,
                            correct = null,
                            onClick = { onAnswerSelected(option) }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Navigation buttons ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .background(BgCard)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentIndex > 0) {
                        // Outlined prev button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Surface1)
                                .drawBehind {
                                    drawRoundRect(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                AccentStart.copy(alpha = 0.6f),
                                                AccentEnd.copy(alpha = 0.6f)
                                            )
                                        ),
                                        cornerRadius = CornerRadius(12.dp.toPx()),
                                        style        = Stroke(width = 1.dp.toPx())
                                    )
                                }
                                .clickable { onPrev() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "← Prev",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (!isLastQ) {
                        // Gradient next button
                        Button(
                            onClick         = onNext,
                            modifier        = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .glowEffect(AccentMid, 14.dp, 0.30f),
                            shape           = RoundedCornerShape(12.dp),
                            colors          = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding  = PaddingValues(0.dp),
                            elevation       = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(listOf(AccentStart, AccentEnd)),
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Next →",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        // Gradient submit button
                        Button(
                            onClick        = onSubmit,
                            modifier       = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .glowEffect(AccentMid, 14.dp, 0.30f),
                            shape          = RoundedCornerShape(12.dp),
                            colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            elevation      = ButtonDefaults.buttonElevation(0.dp),
                            enabled        = uiState.answers.isNotEmpty()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(listOf(AccentStart, AccentEnd)),
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Submit",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
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
    correct: String?,
    onClick: () -> Unit
) {
    val isChosen  = chosen == option
    val isCorrect = correct != null && option == correct
    val isWrong   = correct != null && isChosen && !isCorrect

    val accentColor = when {
        isCorrect -> Color(0xFF0F6E56)
        isWrong   -> Color(0xFF993C1D)
        isChosen  -> AccentMid
        else      -> Border
    }

    val containerColor by animateColorAsState(
        targetValue = when {
            isCorrect -> Color(0xFF0F6E56).copy(alpha = 0.15f)
            isWrong   -> Color(0xFF993C1D).copy(alpha = 0.15f)
            isChosen  -> AccentMid.copy(alpha = 0.15f)
            else      -> Surface1
        },
        animationSpec = tween(200),
        label = "optionBg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = if (isChosen || isCorrect) 1f else 0.5f),
                            accentColor.copy(alpha = if (isChosen || isCorrect) 0.7f else 0.3f)
                        )
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style        = Stroke(
                        width = if (isChosen || isCorrect) 1.5.dp.toPx() else 0.8.dp.toPx()
                    )
                )
            }
            .clickable(onClick = onClick)
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text     = option,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color    = if (isChosen || isCorrect) TextPrimary else TextMuted
            )
            when {
                isCorrect -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF0F6E56),
                    modifier = Modifier.size(18.dp)
                )
                isWrong -> Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFF993C1D),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Timer badge ────────────────────────────────────────────────────────────────

@Composable
private fun TimerBadge(timeLeft: Int, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .drawBehind {
                drawRoundRect(
                    color        = color.copy(alpha = 0.4f),
                    cornerRadius = CornerRadius(50.dp.toPx()),
                    style        = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text       = "${timeLeft}s",
            color      = color,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Loading / Error ────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color       = AccentMid,
            strokeWidth = 2.dp,
            modifier    = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text       = message,
                color      = Color(0xFF993C1D),
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick        = onBack,
                shape          = RoundedCornerShape(12.dp),
                colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                elevation      = ButtonDefaults.buttonElevation(0.dp),
                modifier       = Modifier
                    .height(46.dp)
                    .glowEffect(AccentMid, 14.dp, 0.30f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(AccentStart, AccentEnd)),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "Go Back",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}