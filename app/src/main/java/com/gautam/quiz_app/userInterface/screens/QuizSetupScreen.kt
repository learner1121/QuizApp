package com.gautam.quiz_app.userInterface.screens


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    navController: NavController,
    section: String,
    isRandom: Boolean
) {
    var state by remember { mutableStateOf(QuizSetupState()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isRandom) "Random Quiz" else section,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Configure your session",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        val route = buildString {
                            append("quizPlay")
                            append("/$section")
                            append("/${state.difficulty}")
                            append("/${state.questionCount}")
                            append("/${state.timerPerQuestion}")
                            append("/$isRandom")
                        }
                        navController.navigate(route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Start Quiz",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Summary chip row
            item {
                SetupSummaryRow(state)
            }

            // Difficulty
            item {
                SetupSection(title = "Difficulty", subtitle = "How hard should the questions be?") {
                    OptionChipRow(
                        options = listOf("Easy", "Medium", "Hard"),
                        selected = state.difficulty,
                        chipColors = mapOf(
                            "Easy"   to Color(0xFF0F6E56),
                            "Medium" to Color(0xFF854F0B),
                            "Hard"   to Color(0xFF993C1D)
                        )
                    ) { state = state.copy(difficulty = it) }
                }
            }

            // Question Count
            item {
                SetupSection(title = "Questions", subtitle = "How many questions in this session?") {
                    OptionChipRow(
                        options = listOf("5", "10", "20"),
                        selected = state.questionCount.toString(),
                        chipColors = emptyMap()
                    ) { state = state.copy(questionCount = it.toInt()) }
                }
            }

            // Timer
            item {
                SetupSection(title = "Timer", subtitle = "Time allowed per question") {
                    OptionChipRow(
                        options = listOf("30s", "60s", "120s"),
                        selected = "${state.timerPerQuestion}s",
                        chipColors = emptyMap()
                    ) {
                        state = state.copy(
                            timerPerQuestion = it.removeSuffix("s").toInt()
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

// ── Summary row ────────────────────────────────────────────────────────────────

@Composable
private fun SetupSummaryRow(state: QuizSetupState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(label = "Difficulty", value = state.difficulty)
            SummaryItem(label = "Questions",  value = state.questionCount.toString())
            SummaryItem(label = "Timer",      value = "${state.timerPerQuestion}s")
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

// ── Section wrapper ────────────────────────────────────────────────────────────

@Composable
private fun SetupSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        content()
    }
}

// ── Option chip row ────────────────────────────────────────────────────────────

@Composable
private fun OptionChipRow(
    options: List<String>,
    selected: String,
    chipColors: Map<String, Color>,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            OptionChip(
                label = option,
                isSelected = selected == option,
                accentColor = chipColors[option],
                modifier = Modifier.weight(1f),
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val resolvedAccent = accentColor ?: MaterialTheme.colorScheme.primary

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) resolvedAccent else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "chipContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "chipContentColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) resolvedAccent
        else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(200),
        label = "chipBorderColor"
    )

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = label,
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}