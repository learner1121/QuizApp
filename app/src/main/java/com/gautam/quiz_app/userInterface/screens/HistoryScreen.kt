// userInterface/screens/HistoryScreen.kt
package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gautam.quiz_app.data.model.History
import com.gautam.quiz_app.userInterface.viewModel.HistoryUiState
import com.gautam.quiz_app.userInterface.viewModel.HistoryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Colour helpers ─────────────────────────────────────────────────────────────

private val GreenAccent  = Color(0xFF0F6E56)
private val RedAccent    = Color(0xFF993C1D)
private val AmberAccent  = Color(0xFF854F0B)

private fun difficultyColor(difficulty: String) = when (difficulty) {
    "Easy"   -> GreenAccent
    "Medium" -> AmberAccent
    "Hard"   -> RedAccent
    else     -> Color.Gray
}

private fun scoreColor(pct: Float) = when {
    pct >= 70 -> GreenAccent
    pct >= 50 -> AmberAccent
    else      -> RedAccent
}

// ── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController : NavController,
    viewModel     : HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope   = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "History",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Your past attempts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.load() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        AnimatedContent(
            targetState   = uiState,
            transitionSpec = { fadeIn(initialAlpha = 0.3f) togetherWith fadeOut() },
            modifier      = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label         = "historyState"
        ) { state ->
            when (state) {

                is HistoryUiState.Loading -> LoadingState()

                is HistoryUiState.Guest   -> GuestState(
                    onLogin = { navController.navigate("login") }
                )

                is HistoryUiState.Empty   -> EmptyState(
                    onStartQuiz = { navController.navigate("HomeScreen") }
                )

                is HistoryUiState.Error   -> ErrorState(
                    message   = state.message,
                    onRetry   = { viewModel.load() }
                )

                is HistoryUiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh    = {
                            scope.launch {
                                isRefreshing = true
                                viewModel.load()
                                isRefreshing = false
                            }
                        },
                        state        = pullState,
                        modifier     = Modifier.fillMaxSize()
                    ) {
                        HistoryList(
                            entries = state.entries,
                            best    = state.best,
                            worst   = state.worst
                        )
                    }
                }
            }
        }
    }
}

// ── History list ───────────────────────────────────────────────────────────────

@Composable
private fun HistoryList(
    entries : List<History>,
    best    : History?,
    worst   : History?
) {
    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding      = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical   = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Summary header
        item {
            HistorySummaryCard(entries)
            Spacer(Modifier.height(4.dp))
        }

        // Best attempt
        if (best != null) {
            item {
                SpecialAttemptCard(
                    entry       = best,
                    label       = "Best Attempt",
                    labelColor  = GreenAccent
                )
            }
        }

        // Worst attempt — only show if there are at least 2 entries
        if (worst != null && worst.id != best?.id) {
            item {
                SpecialAttemptCard(
                    entry       = worst,
                    label       = "Worst Attempt",
                    labelColor  = RedAccent
                )
            }
        }

        // Section header
        item {
            Text(
                "All Attempts (${entries.size})",
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // All entries
        items(entries, key = { it.id }) { entry ->
            HistoryCard(entry = entry)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ── Summary card ───────────────────────────────────────────────────────────────

@Composable
private fun HistorySummaryCard(entries: List<History>) {
    val avgPct   = if (entries.isEmpty()) 0f
    else entries.map { it.percentage }.average().toFloat()
    val highest  = entries.maxOfOrNull { it.percentage } ?: 0f
    val totalPlayed = entries.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryStatItem("Played",   "$totalPlayed",          MaterialTheme.colorScheme.onPrimaryContainer)
            SummaryStatItem("Best",     "${highest.toInt()}%",   GreenAccent)
            SummaryStatItem("Average",  "${avgPct.toInt()}%",    MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun SummaryStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Special attempt card (Best / Worst) ───────────────────────────────────────

@Composable
private fun SpecialAttemptCard(
    entry      : History,
    label      : String,
    labelColor : Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = labelColor.copy(alpha = 0.08f)
        ),
        border   = BorderStroke(1.5.dp, labelColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Label row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint     = labelColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    style      = MaterialTheme.typography.labelMedium,
                    color      = labelColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))
            HistoryCardContent(entry)
        }
    }
}

// ── Regular history card ───────────────────────────────────────────────────────

@Composable
private fun HistoryCard(entry: History) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            HistoryCardContent(entry)
        }
    }
}

// ── Shared card content ────────────────────────────────────────────────────────

@Composable
private fun HistoryCardContent(entry: History) {
    val pct        = entry.percentage
    val color      = scoreColor(pct)
    val dateStr    = SimpleDateFormat("dd MMM yyyy  hh:mm a", Locale.getDefault())
        .format(Date(entry.date))

    // Top row: section + score
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Section + difficulty
        Column {
            Text(
                entry.section,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            DifficultyChip(entry.difficulty)
        }

        // Score circle
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
        ) {
            Column(
                modifier            = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${entry.score}/${entry.total}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = color
                )
                Text(
                    "${pct.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))

    // Bottom row: date + time taken
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            dateStr,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "⏱ ${entry.timeFormatted}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DifficultyChip(difficulty: String) {
    val color = difficultyColor(difficulty)
    Surface(
        shape  = RoundedCornerShape(50),
        color  = color.copy(alpha = 0.1f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            difficulty,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Empty state ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(onStartQuiz: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📋", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "No attempts yet",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Complete a quiz to see your history here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            Button(onClick = onStartQuiz) { Text("Start a Quiz") }
        }
    }
}

// ── Guest state ────────────────────────────────────────────────────────────────

@Composable
private fun GuestState(onLogin: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint     = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Login Required",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sign in to track your quiz history and see your progress over time.",
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick  = onLogin,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) { Text("Sign In", fontWeight = FontWeight.SemiBold) }
        }
    }
}

// ── Loading state ──────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// ── Error state ────────────────────────────────────────────────────────────────

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 40.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Something went wrong",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            OutlinedButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Retry")
            }
        }
    }
}