// userInterface/screens/LeaderboardScreen.kt
package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gautam.quiz_app.data.model.LeaderboardEntry
import com.gautam.quiz_app.userInterface.viewModel.LeaderboardTab
import com.gautam.quiz_app.userInterface.viewModel.LeaderboardUiState
import com.gautam.quiz_app.userInterface.viewModel.LeaderboardViewModel

// ── Theme tokens (mirrors HistoryScreen) ──────────────────────────────────────

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

// ── Rank medal colours ────────────────────────────────────────────────────────

private val Gold   = Color(0xFFFFD700)
private val Silver = Color(0xFFB0BEC5)
private val Bronze = Color(0xFFCD7F32)

private fun medalColor(rank: Int) = when (rank) {
    1    -> Gold
    2    -> Silver
    3    -> Bronze
    else -> Color.Transparent
}

// ── Glow modifier (copied from HistoryScreen) ─────────────────────────────────

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

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController : NavController,
    viewModel     : LeaderboardViewModel = hiltViewModel()
) {
    val selectedTab  by viewModel.selectedTab.collectAsState()
    val states       by viewModel.states.collectAsState()
    val tabs          = LeaderboardTab.entries
    val currentState  = states[selectedTab] ?: LeaderboardUiState.Loading

    // Entry animation
    val offsetY by produceState(initialValue = 40f) {
        animate(40f, 0f, animationSpec = tween(600, easing = EaseOutCubic)) { v, _ -> value = v }
    }
    val contentAlpha by produceState(initialValue = 0f) {
        animate(0f, 1f, animationSpec = tween(600, easing = EaseOutCubic)) { v, _ -> value = v }
    }

    // Guest gate
    if (viewModel.isGuest) {
        GuestLeaderboardScreen(
            onLogin = { navController.navigate("login") },
            onBack  = { navController.popBackStack() }
        )
        return
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
                    center = Offset(size.width * 0.1f, size.height * 0.75f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.75f),
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
            // ── Top bar ───────────────────────────────────────────────────
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Leaderboard",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 19.sp
                        )
                        Text(
                            text     = "Top performers",
                            color    = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface1)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = TextPrimary,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface1)
                            .clickable { viewModel.retry(selectedTab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint               = TextPrimary,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDeep)
            )

            // ── Tab row ───────────────────────────────────────────────────
            PrimaryTabRow(
                selectedTabIndex  = tabs.indexOf(selectedTab),
                containerColor    = BgDeep,
                contentColor      = TextPrimary,
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected         = selectedTab == tab,
                        onClick          = { viewModel.selectTab(tab) },
                        selectedContentColor   = AccentMid,
                        unselectedContentColor = TextMuted,
                        text = {
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

            // ── Animated content ──────────────────────────────────────────
            AnimatedContent(
                targetState    = currentState,
                transitionSpec = { fadeIn(initialAlpha = 0.3f) togetherWith fadeOut() },
                modifier       = Modifier.fillMaxSize(),
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
                    else -> {}
                }
            }
        }
    }
}

// ── Leaderboard list ──────────────────────────────────────────────────────────

@Composable
private fun LeaderboardList(
    entries       : List<LeaderboardEntry>,
    currentUserId : String?
) {
    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Podium
        if (entries.size >= 3) {
            item {
                PodiumRow(
                    first         = entries.getOrNull(0),
                    second        = entries.getOrNull(1),
                    third         = entries.getOrNull(2),
                    currentUserId = currentUserId
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        // Section header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text       = "Rankings (${entries.size})",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
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

// ── Podium ────────────────────────────────────────────────────────────────────

@Composable
private fun PodiumRow(
    first         : LeaderboardEntry?,
    second        : LeaderboardEntry?,
    third         : LeaderboardEntry?,
    currentUserId : String?
) {
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
            .padding(16.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            PodiumSlot(entry = second, rank = 2, height = 70.dp,  isCurrentUser = second?.userId == currentUserId)
            PodiumSlot(entry = first,  rank = 1, height = 100.dp, isCurrentUser = first?.userId  == currentUserId)
            PodiumSlot(entry = third,  rank = 3, height = 55.dp,  isCurrentUser = third?.userId  == currentUserId)
        }
    }
}

@Composable
private fun PodiumSlot(
    entry         : LeaderboardEntry?,
    rank          : Int,
    height        : Dp,
    isCurrentUser : Boolean
) {
    if (entry == null) return
    val medal = medalColor(rank)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isCurrentUser) AccentMid.copy(alpha = 0.25f) else Surface2
                )
                .drawBehind {
                    drawCircle(
                        color = (medal.takeIf { it != Color.Transparent } ?: Border),
                        style = Stroke(width = 2.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = entry.name.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = if (isCurrentUser) AccentMid else TextPrimary
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text       = entry.name.take(10),
            fontSize   = 11.sp,
            fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
            color      = if (isCurrentUser) TextPrimary else TextMuted,
            maxLines   = 1
        )
        Text(
            text     = "${entry.score} pts",
            fontSize = 10.sp,
            color    = TextMuted
        )
        Spacer(Modifier.height(4.dp))
        // Podium block
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    if (medal != Color.Transparent) medal.copy(alpha = 0.18f)
                    else Surface2
                )
                .drawBehind {
                    drawRoundRect(
                        color        = (medal.takeIf { it != Color.Transparent } ?: Border).copy(alpha = 0.4f),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                        style        = Stroke(width = 0.8.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "#$rank",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = if (medal != Color.Transparent) medal else TextMuted
            )
        }
    }
}

// ── Row entry ─────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardRow(
    entry         : LeaderboardEntry,
    rank          : Int,
    isCurrentUser : Boolean
) {
    val medal    = medalColor(rank)
    val hasMedal = medal != Color.Transparent
    val barColor = when {
        isCurrentUser -> AccentMid
        hasMedal      -> medal
        else          -> Border
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCurrentUser) AccentMid.copy(alpha = 0.08f) else Surface1)
            .drawBehind {
                drawRoundRect(
                    color        = if (isCurrentUser || hasMedal)
                        barColor.copy(alpha = 0.45f)
                    else Border,
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style        = Stroke(
                        width = if (isCurrentUser || hasMedal) 1.5.dp.toPx() else 0.8.dp.toPx()
                    )
                )
            }
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(listOf(barColor, barColor.copy(alpha = 0.3f)))
                )
        )

        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            RankBadge(rank = rank, medal = medal)

            Spacer(Modifier.width(12.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentUser) AccentMid.copy(alpha = 0.20f) else Surface2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = entry.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = if (isCurrentUser) AccentMid else TextMuted
                )
            }

            Spacer(Modifier.width(12.dp))

            // Name + quiz count
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = entry.name,
                        fontSize   = 15.sp,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        color      = TextPrimary,
                        maxLines   = 1
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        YouChip()
                    }
                }
                Text(
                    text     = "${entry.quizCount} quizzes",
                    fontSize = 11.sp,
                    color    = TextMuted
                )
            }

            // Score chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(barColor.copy(alpha = 0.12f))
                    .drawBehind {
                        drawRoundRect(
                            color        = barColor.copy(alpha = 0.35f),
                            cornerRadius = CornerRadius(10.dp.toPx()),
                            style        = Stroke(width = 1.dp.toPx())
                        )
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "${entry.score}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = barColor
                    )
                    Text(
                        text     = "pts",
                        fontSize = 11.sp,
                        color    = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int, medal: Color) {
    val hasMedal = medal != Color.Transparent
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (hasMedal) medal.copy(alpha = 0.15f) else Surface2)
            .drawBehind {
                drawCircle(
                    color = (if (hasMedal) medal else Border).copy(alpha = 0.4f),
                    style = Stroke(width = 0.8.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = if (hasMedal) when (rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
            else "#$rank",
            fontSize   = if (hasMedal) 16.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            color      = if (hasMedal) medal else TextMuted
        )
    }
}

@Composable
private fun YouChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(AccentMid.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text       = "you",
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = AccentMid
        )
    }
}

// ── Guest screen ──────────────────────────────────────────────────────────────

@Composable
private fun GuestLeaderboardScreen(onLogin: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        // Decorative orb
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(AccentStart.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.3f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.6f
            )
        }

        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 12.dp, top = 8.dp)
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface1)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = TextPrimary,
                modifier           = Modifier.size(18.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Surface1)
                    .drawBehind {
                        drawCircle(
                            color = Border,
                            style = Stroke(width = 0.8.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = 36.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = "Login Required",
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = "Sign in to view the leaderboard and see where you rank against other players.",
                color     = TextMuted,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick        = onLogin,
                modifier       = Modifier
                    .fillMaxWidth()
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
                    Text(
                        text       = "Sign In",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}

// ── Loading ───────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardLoading() {
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

// ── Empty ─────────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardEmpty() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Surface1)
                    .drawBehind {
                        drawCircle(
                            color = Border,
                            style = Stroke(width = 0.8.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("🏅", fontSize = 32.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = "No entries yet",
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = "Be the first to complete a quiz in this section!",
                color     = TextMuted,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardError(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF993C1D).copy(alpha = 0.12f))
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF993C1D).copy(alpha = 0.35f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("⚠️", fontSize = 32.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = "Something went wrong",
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = message,
                color     = TextMuted,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    .clickable { onRetry() },
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
                        modifier           = Modifier.size(16.dp)
                    )
                    Text(
                        text       = "Retry",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}