package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gautam.quiz_app.data.model.Question
import com.gautam.quiz_app.data.model.QuizResultUiModel
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

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

// ── Badge colours ──────────────────────────────────────────────────────────────

private val BadgeExpert       = Color(0xFF1565C0)
private val BadgeAdvanced     = Color(0xFF0F6E56)
private val BadgeIntermediate = Color(0xFF854F0B)
private val BadgeBeginner     = Color(0xFF993C1D)

private fun badgeColor(badge: String) = when (badge) {
    "Expert"       -> BadgeExpert
    "Advanced"     -> BadgeAdvanced
    "Intermediate" -> BadgeIntermediate
    else           -> BadgeBeginner
}

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
fun QuizResultScreen(
    navController : NavController,
    result        : QuizResultUiModel,
    viewModel     : QuestionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.submitResultIfAuthenticated(result)
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
                brush  = Brush.radialGradient(
                    colors = listOf(AccentStart.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.05f),
                    radius = size.width * 0.55f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.05f),
                radius = size.width * 0.55f
            )
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(AccentEnd.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.75f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.75f),
                radius = size.width * 0.5f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .offset(y = offsetY.dp)
                .graphicsLayer(alpha = contentAlpha),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { ScoreHero(result) }

            item {
                StatRow(result)
                Spacer(Modifier.height(8.dp))
            }

            item {
                MetaRow(result)
                Spacer(Modifier.height(20.dp))
            }

            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(18.dp)
                            .background(
                                Brush.verticalGradient(listOf(AccentStart, AccentEnd)),
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "Review Answers",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(result.questions) { index, question ->
                ReviewCard(
                    index    = index,
                    question = question,
                    userAns  = result.answers[index]
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                ActionButtons(
                    onTryAgain = {
                        viewModel.resetQuiz()
                        navController.navigate(
                            "quizSetup/${result.section}/${result.section == "random"}"
                        ) {
                            popUpTo("HomeScreen") { inclusive = false }
                        }
                    },
                    onHome = {
                        viewModel.resetQuiz()
                        navController.navigate("HomeScreen") {
                            popUpTo("HomeScreen") { inclusive = true }
                        }
                    }
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ── Score hero ─────────────────────────────────────────────────────────────────

@Composable
private fun ScoreHero(result: QuizResultUiModel) {
    val color = badgeColor(result.badge)

    val animatedPct by animateFloatAsState(
        targetValue   = result.percentage / 100f,
        animationSpec = tween(1000),
        label         = "scoreAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgCard)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular score ring
        Box(contentAlignment = Alignment.Center) {
            CircularScoreRing(progress = animatedPct, color = color)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "${result.correct}/${result.total}",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
                Text(
                    text     = "${result.percentage.toInt()}%",
                    fontSize = 13.sp,
                    color    = TextMuted
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Badge pill
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
                .padding(horizontal = 20.dp, vertical = 7.dp)
        ) {
            Text(
                text       = result.badge,
                color      = color,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp
            )
        }
    }
}

@Composable
private fun CircularScoreRing(progress: Float, color: Color) {
    Box(
        modifier         = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress    = { 1f },
            modifier    = Modifier.size(140.dp),
            color       = color.copy(alpha = 0.12f),
            strokeWidth = 10.dp
        )
        CircularProgressIndicator(
            progress    = { progress },
            modifier    = Modifier.size(140.dp),
            color       = color,
            strokeWidth = 10.dp,
            trackColor  = Color.Transparent
        )
    }
}

// ── Stat row ───────────────────────────────────────────────────────────────────

@Composable
private fun StatRow(result: QuizResultUiModel) {
    val mins    = result.timeTaken / 60
    val secs    = result.timeTaken % 60
    val timeStr = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .drawBehind {
                drawRoundRect(
                    color        = Border,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style        = Stroke(width = 0.8.dp.toPx())
                )
            }
            .padding(16.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            StatItem(label = "Correct",  value = "${result.correct}",  color = Color(0xFF0F6E56))
            VerticalDivider()
            StatItem(label = "Wrong",    value = "${result.wrong}",    color = Color(0xFF993C1D))
            VerticalDivider()
            StatItem(label = "Skipped",  value = "${result.skipped}",  color = Color(0xFF854F0B))
            VerticalDivider()
            StatItem(label = "Time",     value = timeStr,              color = AccentMid)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
        Text(label, fontSize = 11.sp, color = TextMuted)
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        Modifier
            .height(32.dp)
            .width(1.dp)
            .background(Border)
    )
}

// ── Meta row ───────────────────────────────────────────────────────────────────

@Composable
private fun MetaRow(result: QuizResultUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetaChip(label = result.section)
        MetaChip(label = result.difficulty)
    }
}

@Composable
private fun MetaChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Surface1)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(AccentStart.copy(alpha = 0.5f), AccentEnd.copy(alpha = 0.5f))
                    ),
                    cornerRadius = CornerRadius(50.dp.toPx()),
                    style        = Stroke(width = 0.8.dp.toPx())
                )
            }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = TextPrimary
        )
    }
}

// ── Review card ────────────────────────────────────────────────────────────────

@Composable
private fun ReviewCard(
    index    : Int,
    question : Question,
    userAns  : String?
) {
    val isCorrect   = userAns != null && userAns == question.correctAnswer
    val isSkipped   = userAns == null
    val accentColor = when {
        isSkipped -> Color(0xFF854F0B)
        isCorrect -> Color(0xFF0F6E56)
        else      -> Color(0xFF993C1D)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .drawBehind {
                drawRoundRect(
                    color        = accentColor.copy(alpha = 0.30f),
                    cornerRadius = CornerRadius(14.dp.toPx()),
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
                    Brush.verticalGradient(listOf(accentColor, accentColor.copy(alpha = 0.3f)))
                )
        )

        Column(
            modifier = Modifier.padding(start = 20.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)
        ) {
            // Number row + status
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status icon circle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSkipped) {
                        Text("–", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    } else {
                        Icon(
                            imageVector        = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint               = accentColor,
                            modifier           = Modifier.size(15.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                // Tag pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text       = "Q${index + 1}",
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color      = accentColor,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text       = when {
                        isSkipped -> "Skipped"
                        isCorrect -> "Correct"
                        else      -> "Wrong"
                    },
                    fontSize   = 11.sp,
                    color      = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))

            // Question text
            Text(
                text       = question.questionText ?: "",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp,
                color      = TextPrimary
            )

            Spacer(Modifier.height(10.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Border)
            )

            Spacer(Modifier.height(10.dp))

            // Your answer
            AnswerRow(
                label  = "Your answer",
                answer = userAns ?: "Not answered",
                color  = accentColor,
                strike = !isCorrect && !isSkipped
            )

            // Correct answer — only show if wrong or skipped
            if (!isCorrect) {
                Spacer(Modifier.height(6.dp))
                AnswerRow(
                    label  = "Correct answer",
                    answer = question.correctAnswer ?: "",
                    color  = Color(0xFF0F6E56),
                    strike = false
                )
            }

            // Explanation
            if (!question.explanation.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface2)
                        .drawBehind {
                            drawRoundRect(
                                color        = Border,
                                cornerRadius = CornerRadius(8.dp.toPx()),
                                style        = Stroke(width = 0.5.dp.toPx())
                            )
                        }
                        .padding(10.dp)
                ) {
                    Text(
                        text       = question.explanation,
                        fontSize   = 12.sp,
                        lineHeight = 18.sp,
                        color      = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerRow(label: String, answer: String, color: Color, strike: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text     = "$label: ",
            fontSize = 11.sp,
            color    = TextMuted
        )
        Text(
            text           = answer,
            fontSize       = 11.sp,
            color          = color,
            fontWeight     = FontWeight.SemiBold,
            textDecoration = if (strike) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

// ── Action buttons ─────────────────────────────────────────────────────────────

@Composable
private fun ActionButtons(onTryAgain: () -> Unit, onHome: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Outlined Home button
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
                .clickable { onHome() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Home,
                    contentDescription = null,
                    tint               = TextPrimary,
                    modifier           = Modifier.size(18.dp)
                )
                Text(
                    text       = "Home",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            }
        }

        // Gradient Try Again button
        Button(
            onClick        = onTryAgain,
            modifier       = Modifier
                .weight(1f)
                .height(50.dp)
                .glowEffect(AccentMid, 14.dp, 0.30f),
            shape          = RoundedCornerShape(12.dp),
            colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            elevation      = ButtonDefaults.buttonElevation(0.dp)
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
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Refresh,
                        contentDescription = null,
                        tint               = TextPrimary,
                        modifier           = Modifier.size(18.dp)
                    )
                    Text(
                        text       = "Try Again",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}