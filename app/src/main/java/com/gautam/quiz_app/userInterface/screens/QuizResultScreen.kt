package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gautam.quiz_app.data.model.Question
import com.gautam.quiz_app.data.model.QuizResultUiModel
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

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

// ── Entry point ────────────────────────────────────────────────────────────────

@Composable
fun QuizResultScreen(
    navController : NavController,
    result        : QuizResultUiModel,
    viewModel     : QuestionViewModel = hiltViewModel()
) {

    // Post result to backend if user is logged in
    LaunchedEffect(Unit) {
        viewModel.submitResultIfAuthenticated(result)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── Score hero ─────────────────────────────────────────────────────
        item { ScoreHero(result) }

        // ── Stat row ───────────────────────────────────────────────────────
        item {
            StatRow(result)
            Spacer(Modifier.height(8.dp))
        }

        // ── Section / difficulty tag ───────────────────────────────────────
        item {
            MetaRow(result)
            Spacer(Modifier.height(16.dp))
        }

        // ── Review header ──────────────────────────────────────────────────
        item {
            Text(
                "Review Answers",
                modifier = Modifier.padding(horizontal = 16.dp),
                style    = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }

        // ── Per-question review cards ──────────────────────────────────────
        itemsIndexed(result.questions) { index, question ->
            ReviewCard(
                index    = index,
                question = question,
                userAns  = result.answers[index]
            )
        }

        // ── Bottom buttons ─────────────────────────────────────────────────
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
        modifier            = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular score ring
        Box(contentAlignment = Alignment.Center) {
            CircularScoreRing(progress = animatedPct, color = color)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${result.correct}/${result.total}",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
                Text(
                    "${result.percentage.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Badge pill
        Surface(
            shape  = RoundedCornerShape(50),
            color  = color.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
        ) {
            Text(
                result.badge,
                modifier   = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
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
        modifier            = Modifier.size(140.dp),
        contentAlignment    = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            progress         = { 1f },
            modifier         = Modifier.size(140.dp),
            color            = color.copy(alpha = 0.12f),
            strokeWidth      = 10.dp
        )
        androidx.compose.material3.CircularProgressIndicator(
            progress         = { progress },
            modifier         = Modifier.size(140.dp),
            color            = color,
            strokeWidth      = 10.dp,
            trackColor       = Color.Transparent
        )
    }
}

// ── Stat row ───────────────────────────────────────────────────────────────────

@Composable
private fun StatRow(result: QuizResultUiModel) {
    val mins = result.timeTaken / 60
    val secs = result.timeTaken % 60
    val timeStr = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Correct",  "${result.correct}",  Color(0xFF0F6E56))
            VerticalDivider()
            StatItem("Wrong",    "${result.wrong}",    Color(0xFF993C1D))
            VerticalDivider()
            StatItem("Skipped",  "${result.skipped}",  Color(0xFF854F0B))
            VerticalDivider()
            StatItem("Time",     timeStr,              MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        Modifier
            .height(36.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
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
    Surface(
        shape  = RoundedCornerShape(50),
        color  = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style    = MaterialTheme.typography.labelSmall,
            color    = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium
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
    val isCorrect  = userAns != null && userAns == question.correctAnswer
    val isSkipped  = userAns == null
    val accentColor = when {
        isSkipped  -> Color(0xFF854F0B)
        isCorrect  -> Color(0xFF0F6E56)
        else       -> Color(0xFF993C1D)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border   = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Question number + status icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSkipped) {
                            Text(
                                "–",
                                color      = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp
                            )
                        } else {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint       = accentColor,
                                modifier   = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "Q${index + 1}",
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                Text(
                    when {
                        isSkipped -> "Skipped"
                        isCorrect -> "Correct"
                        else      -> "Wrong"
                    },
                    style  = MaterialTheme.typography.labelSmall,
                    color  = accentColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            // Question text
            Text(
                question.questionText ?: "",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(10.dp))

            // Your answer
            AnswerRow(
                label  = "Your answer",
                answer = userAns ?: "Not answered",
                color  = if (isSkipped) Color(0xFF854F0B)
                else if (isCorrect) Color(0xFF0F6E56)
                else Color(0xFF993C1D),
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp),
                    color    = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        question.explanation,
                        modifier   = Modifier.padding(10.dp),
                        style      = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
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
            "$label: ",
            style  = MaterialTheme.typography.labelSmall,
            color  = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            answer,
            style           = MaterialTheme.typography.labelSmall.copy(
                textDecoration = if (strike) TextDecoration.LineThrough else TextDecoration.None
            ),
            color           = color,
            fontWeight      = FontWeight.SemiBold
        )
    }
}

// ── Action buttons ─────────────────────────────────────────────────────────────

@Composable
private fun ActionButtons(onTryAgain: () -> Unit, onHome: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick  = onHome,
            modifier = Modifier.weight(1f).height(50.dp),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Home, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Home")
        }

        Button(
            onClick  = onTryAgain,
            modifier = Modifier.weight(1f).height(50.dp),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Try Again")
        }
    }
}