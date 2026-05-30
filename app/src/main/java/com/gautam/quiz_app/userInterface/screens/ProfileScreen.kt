// userInterface/screens/ProfileScreen.kt
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gautam.quiz_app.data.model.UserProfile
import com.gautam.quiz_app.userInterface.viewModel.ProfileUiState
import com.gautam.quiz_app.userInterface.viewModel.ProfileViewModel

// ── Colour helpers ─────────────────────────────────────────────────────────────

private val GreenAccent = Color(0xFF0F6E56)
private val RedAccent   = Color(0xFF993C1D)
private val AmberAccent = Color(0xFF854F0B)
private val BlueAccent  = Color(0xFF1565C0)

// ── Entry point ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController : NavController,
    viewModel     : ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Profile",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Your account & stats",
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
                    if (uiState is ProfileUiState.Success) {
                        IconButton(onClick = { viewModel.load() }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                "Logout",
                                tint = RedAccent
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        AnimatedContent(
            targetState    = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label          = "profileState"
        ) { state ->
            when (state) {
                is ProfileUiState.Loading -> ProfileLoading()

                is ProfileUiState.Guest   -> ProfileGuestState(
                    onLogin = { navController.navigate("login") }
                )

                is ProfileUiState.Error   -> ProfileErrorState(
                    message = state.message,
                    onRetry = { viewModel.load() }
                )

                is ProfileUiState.Success -> ProfileContent(
                    state       = state,
                    onLogout    = { showLogoutDialog = true }
                )
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout {
                    navController.navigate("login") {
                        popUpTo("HomeScreen") { inclusive = true }
                    }
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

// ── Success content ────────────────────────────────────────────────────────────

@Composable
private fun ProfileContent(
    state    : ProfileUiState.Success,
    onLogout : () -> Unit
) {
    LazyColumn(
        modifier        = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding  = PaddingValues(bottom = 24.dp)
    ) {
        // Hero header
        item { ProfileHero(state) }

        // Rank banner
        item {
            Spacer(Modifier.height(16.dp))
            RankBanner(rank = state.profile.rank)
            Spacer(Modifier.height(16.dp))
        }

        // Stats grid
        item {
            Text(
                "Statistics",
                modifier   = Modifier.padding(horizontal = 16.dp),
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            StatsGrid(profile = state.profile)
            Spacer(Modifier.height(16.dp))
        }

        // Section performance
        item {
            Text(
                "Section Performance",
                modifier   = Modifier.padding(horizontal = 16.dp),
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            SectionPerformanceRow(profile = state.profile)
            Spacer(Modifier.height(24.dp))
        }

        // Logout button
        item {
            Button(
                onClick  = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = RedAccent
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    null,
                    Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Profile hero ───────────────────────────────────────────────────────────────

@Composable
private fun ProfileHero(state: ProfileUiState.Success) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar
            Box(
                modifier         = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (state.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model             = state.photoUrl,
                        contentDescription = "Profile photo",
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback: initial letter
                    Text(
                        text       = state.displayName.take(1).uppercase(),
                        fontSize   = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                state.displayName,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                state.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Rank banner ────────────────────────────────────────────────────────────────

@Composable
private fun RankBanner(rank: Int) {
    val (label, color) = when {
        rank <= 3   -> "Top 3 🏆"  to Color(0xFFFFD700)
        rank <= 10  -> "Top 10 🥈" to Color(0xFFB0BEC5)
        rank <= 50  -> "Top 50 🥉" to Color(0xFFCD7F32)
        else        -> "Rank #$rank" to BlueAccent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border   = BorderStroke(1.5.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Current Rank",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "#$rank",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = color
            )
            Spacer(Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    label,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style      = MaterialTheme.typography.labelSmall,
                    color      = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Stats grid ─────────────────────────────────────────────────────────────────

@Composable
private fun StatsGrid(profile: UserProfile) {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label    = "Total Played",
                value    = "${profile.totalPlayed}",
                unit     = "quizzes",
                color    = BlueAccent
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label    = "Highest Score",
                value    = "${profile.highestScore}",
                unit     = "points",
                color    = GreenAccent
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label    = "Average Score",
                value    = "${"%.1f".format(profile.averageScore)}",
                unit     = "pts / quiz",
                color    = AmberAccent
            )
            // Placeholder slot — keeps grid even
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(
    modifier : Modifier,
    label    : String,
    value    : String,
    unit     : String,
    color    : Color
) {
    Card(
        modifier = modifier.height(100.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        border   = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Text(
                    value,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
                Text(
                    unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ── Section performance ────────────────────────────────────────────────────────

@Composable
private fun SectionPerformanceRow(profile: UserProfile) {
    Row(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionCard(
            modifier = Modifier.weight(1f),
            label    = "Best Section",
            section  = profile.bestSection.ifBlank { "N/A" },
            color    = GreenAccent,
            emoji    = "🏆"
        )
        SectionCard(
            modifier = Modifier.weight(1f),
            label    = "Needs Work",
            section  = profile.worstSection.ifBlank { "N/A" },
            color    = RedAccent,
            emoji    = "📚"
        )
    }
}

@Composable
private fun SectionCard(
    modifier : Modifier,
    label    : String,
    section  : String,
    color    : Color,
    emoji    : String
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        border   = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(emoji, fontSize = 24.sp)
            Text(
                section,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = color,
                textAlign  = TextAlign.Center
            )
            Text(
                label,
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Logout dialog ──────────────────────────────────────────────────────────────

@Composable
private fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Logout", fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(
                "Are you sure you want to logout?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(containerColor = RedAccent)
            ) { Text("Logout") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ── Guest state ────────────────────────────────────────────────────────────────

@Composable
private fun ProfileGuestState(onLogin: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
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
                "Sign in to view your profile, track statistics, and appear on the leaderboard.",
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick  = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Sign In", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Loading ────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// ── Error ──────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 40.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Failed to load profile",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                message,
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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