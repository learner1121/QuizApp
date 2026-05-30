// userInterface/screens/LeaderboardScreen.kt
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gautam.quiz_app.data.model.LeaderboardEntry
import com.gautam.quiz_app.userInterface.viewModel.LeaderboardTab
import com.gautam.quiz_app.userInterface.viewModel.LeaderboardUiState
import com.gautam.quiz_app.userInterface.viewModel.LeaderboardViewModel

// ── Rank medal colours ─────────────────────────────────────────────────────────

private val Gold   = Color(0xFFFFD700)
private val Silver = Color(0xFFB0BEC5)
private val Bronze = Color(0xFFCD7F32)

private fun medalColor(rank: Int) = when (rank) {
    1    -> Gold
    2    -> Silver
    3    -> Bronze
    else -> Color.Transparent
}

private val GreenAccent = Color(0xFF0F6E56)
private val RedAccent   = Color(0xFF993C1D)

// ── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController : NavController,
    viewModel     : LeaderboardViewModel = hiltViewModel()
) {
    val selectedTab   by viewModel.selectedTab.collectAsState()
    val states        by viewModel.states.collectAsState()
    val tabs           = LeaderboardTab.entries
    val currentState   = states[selectedTab] ?: LeaderboardUiState.Loading

    // Guest gate — show inline, no navigation needed
    if (viewModel.isGuest) {
        GuestLeaderboardScreen(onLogin = { navController.navigate("login") },
            onBack  = { navController.popBackStack() })
        return
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Leaderboard",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Top performers",
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Tab row
                PrimaryTabRow(selectedTabIndex = tabs.indexOf(selectedTab)) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick  = { viewModel.selectTab(tab) },
                            text     = {
                                Text(
                                    tab.label,
                                    style      = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selectedTab == tab) FontWeight.SemiBold
                                    else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState    = currentState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label          = "leaderboardState"
        ) { state ->
            when (state) {
                is LeaderboardUiState.Loading -> LeaderboardLoading()
                is LeaderboardUiState.Empty   -> LeaderboardEmpty()
                is LeaderboardUiState.Error   -> LeaderboardError(
                    message = state.message,
                    onRetry = { viewModel.retry(selectedTab) }
                )
                is LeaderboardUiState.Success -> LeaderboardList(
                    entries       = state.entries,
                    currentUserId = viewModel.currentUserId
                )
                // Guest is handled above before Scaffold
                else -> {}
            }
        }
    }
}

// ── Leaderboard list ───────────────────────────────────────────────────────────

@Composable
private fun LeaderboardList(
    entries       : List<LeaderboardEntry>,
    currentUserId : String?
) {
    LazyColumn(
        modifier        = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top 3 podium
        if (entries.size >= 3) {
            item {
                PodiumRow(
                    first  = entries.getOrNull(0),
                    second = entries.getOrNull(1),
                    third  = entries.getOrNull(2),
                    currentUserId = currentUserId
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Rankings",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        // Full list
        itemsIndexed(entries, key = { _, e -> e.userId }) { index, entry ->
            LeaderboardRow(
                entry         = entry,
                rank          = index + 1,
                isCurrentUser = entry.userId == currentUserId
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ── Podium ─────────────────────────────────────────────────────────────────────

@Composable
private fun PodiumRow(
    first         : LeaderboardEntry?,
    second        : LeaderboardEntry?,
    third         : LeaderboardEntry?,
    currentUserId : String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            // 2nd
            PodiumSlot(
                entry         = second,
                rank          = 2,
                height        = 70.dp,
                isCurrentUser = second?.userId == currentUserId
            )
            // 1st — tallest
            PodiumSlot(
                entry         = first,
                rank          = 1,
                height        = 100.dp,
                isCurrentUser = first?.userId == currentUserId
            )
            // 3rd
            PodiumSlot(
                entry         = third,
                rank          = 3,
                height        = 55.dp,
                isCurrentUser = third?.userId == currentUserId
            )
        }
    }
}

@Composable
private fun PodiumSlot(
    entry         : LeaderboardEntry?,
    rank          : Int,
    height        : androidx.compose.ui.unit.Dp,
    isCurrentUser : Boolean
) {
    if (entry == null) return
    val medal = medalColor(rank)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Avatar circle
        Surface(
            modifier = Modifier.size(48.dp),
            shape    = CircleShape,
            color    = if (isCurrentUser) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface,
            border   = BorderStroke(2.dp, medal.takeIf { it != Color.Transparent }
                ?: MaterialTheme.colorScheme.outline)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    entry.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            entry.name.take(10),
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
            maxLines   = 1
        )
        Text(
            "${entry.score} pts",
            style  = MaterialTheme.typography.labelSmall,
            color  = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
        Spacer(Modifier.height(4.dp))
        // Podium block
        Box(
            modifier            = Modifier
                .width(70.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(medal.copy(alpha = 0.6f), medal.copy(alpha = 0.2f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "#$rank",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = if (medal != Color.Transparent) medal else Color.Gray
            )
        }
    }
}

// ── Row entry ──────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardRow(
    entry         : LeaderboardEntry,
    rank          : Int,
    isCurrentUser : Boolean
) {
    val medal = medalColor(rank)
    val containerColor = when {
        isCurrentUser -> MaterialTheme.colorScheme.primaryContainer
        else          -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isCurrentUser          -> MaterialTheme.colorScheme.primary
        medal != Color.Transparent -> medal
        else                   -> MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = containerColor),
        border   = BorderStroke(
            width = if (isCurrentUser || medal != Color.Transparent) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            RankBadge(rank = rank, medal = medal)

            Spacer(Modifier.width(12.dp))

            // Avatar
            Surface(
                modifier = Modifier.size(38.dp),
                shape    = CircleShape,
                color    = if (isCurrentUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        entry.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Name + quiz count
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        entry.name,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        maxLines   = 1
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        YouChip()
                    }
                }
                Text(
                    "${entry.quizCount} quizzes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${entry.score}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = if (isCurrentUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int, medal: Color) {
    val hasMedal = medal != Color.Transparent
    Box(
        modifier         = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (hasMedal) medal.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = if (hasMedal) when (rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
            else "#$rank",
            fontSize   = if (hasMedal) 16.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            color      = if (hasMedal) medal else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun YouChip() {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    ) {
        Text(
            "you",
            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
            style      = MaterialTheme.typography.labelSmall,
            color      = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Guest screen ───────────────────────────────────────────────────────────────

@Composable
private fun GuestLeaderboardScreen(onLogin: () -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title           = { Text("Leaderboard") },
                navigationIcon  = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏆", fontSize = 56.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Login Required",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Sign in to view the leaderboard and see where you rank against other players.",
                    style     = MaterialTheme.typography.bodySmall,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                androidx.compose.material3.Button(
                    onClick  = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign In", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Loading ────────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// ── Empty ──────────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardEmpty() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏅", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "No entries yet",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Be the first to complete a quiz in this section!",
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Error ──────────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardError(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 40.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Failed to load",
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