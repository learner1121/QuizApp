// userInterface/screens/HistoryScreen.kt
package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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

// ── Semantic colours ───────────────────────────────────────────────────────────

private val GreenAccent = Color(0xFF0F6E56)
private val RedAccent   = Color(0xFF993C1D)
private val AmberAccent = Color(0xFF854F0B)

private fun difficultyColor(difficulty: String) = when (difficulty) {
    "Easy"   -> GreenAccent
    "Medium" -> AmberAccent
    "Hard"   -> RedAccent
    else     -> TextMuted
}

private fun scoreColor(pct: Float) = when {
    pct >= 70 -> GreenAccent
    pct >= 50 -> AmberAccent
    else      -> RedAccent
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

// ── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController : NavController,
    viewModel     : HistoryViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsState()
    val scope        = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState    = rememberPullToRefreshState()

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
            // ── Top bar ────────────────────────────────────────────────────
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "History",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 19.sp
                        )
                        Text(
                            text     = "Your past attempts",
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
                            .clickable { viewModel.load() },
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgDeep
                )
            )

            // ── Animated content ───────────────────────────────────────────
            AnimatedContent(
                targetState    = uiState,
                transitionSpec = { fadeIn(initialAlpha = 0.3f) togetherWith fadeOut() },
                modifier       = Modifier.fillMaxSize(),
                label          = "historyState"
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
                        message = state.message,
                        onRetry = { viewModel.load() }
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
                            state    = pullState,
                            modifier = Modifier.fillMaxSize()
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
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            HistorySummaryCard(entries)
            Spacer(Modifier.height(4.dp))
        }

        if (best != null) {
            item {
                SpecialAttemptCard(entry = best, label = "Best Attempt", labelColor = GreenAccent)
            }
        }

        if (worst != null && worst.id != best?.id) {
            item {
                SpecialAttemptCard(entry = worst, label = "Worst Attempt", labelColor = RedAccent)
            }
        }

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
                    text       = "All Attempts (${entries.size})",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }
        }

        items(entries, key = { it.id }) { entry ->
            HistoryCard(entry = entry)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ── Summary card ───────────────────────────────────────────────────────────────

@Composable
private fun HistorySummaryCard(entries: List<History>) {
    val avgPct      = if (entries.isEmpty()) 0f else entries.map { it.percentage }.average().toFloat()
    val highest     = entries.maxOfOrNull { it.percentage } ?: 0f
    val totalPlayed = entries.size

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
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryStatItem(label = "Played",  value = "$totalPlayed",         color = TextPrimary)
            SummaryStatItem(label = "Best",    value = "${highest.toInt()}%",  color = GreenAccent)
            SummaryStatItem(label = "Average", value = "${avgPct.toInt()}%",   color = AccentMid)
        }
    }
}

@Composable
private fun SummaryStatItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface2)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = TextMuted)
    }
}

// ── Special attempt card ───────────────────────────────────────────────────────

@Composable
private fun SpecialAttemptCard(
    entry      : History,
    label      : String,
    labelColor : Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(labelColor.copy(alpha = 0.08f))
            .drawBehind {
                drawRoundRect(
                    color        = labelColor.copy(alpha = 0.35f),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style        = Stroke(width = 1.5.dp.toPx())
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
                        listOf(labelColor, labelColor.copy(alpha = 0.3f))
                    )
                )
        )
        Column(modifier = Modifier.padding(start = 20.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = labelColor,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                // Tag pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(labelColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text       = label.uppercase(),
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color      = labelColor,
                        letterSpacing = 1.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            HistoryCardContent(entry)
        }
    }
}

// ── Regular history card ───────────────────────────────────────────────────────

@Composable
private fun HistoryCard(entry: History) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .drawBehind {
                drawRoundRect(
                    color        = Border,
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style        = Stroke(width = 0.8.dp.toPx())
                )
            }
    ) {
        // Left accent bar using score colour
        val barColor = scoreColor(entry.percentage)
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(listOf(barColor, barColor.copy(alpha = 0.3f)))
                )
        )
        Column(modifier = Modifier.padding(start = 20.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)) {
            HistoryCardContent(entry)
        }
    }
}

// ── Shared card content ────────────────────────────────────────────────────────

@Composable
private fun HistoryCardContent(entry: History) {
    val pct     = entry.percentage
    val color   = scoreColor(pct)
    val dateStr = SimpleDateFormat("dd MMM yyyy  hh:mm a", Locale.getDefault())
        .format(Date(entry.date))

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = entry.section,
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
            Spacer(Modifier.height(4.dp))
            DifficultyChip(entry.difficulty)
        }

        // Score chip
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.12f))
                .drawBehind {
                    drawRoundRect(
                        color        = color.copy(alpha = 0.35f),
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style        = Stroke(width = 1.dp.toPx())
                    )
                }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "${entry.score}/${entry.total}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = color
                )
                Text(
                    text     = "${pct.toInt()}%",
                    fontSize = 11.sp,
                    color    = color
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(dateStr,                   fontSize = 11.sp, color = TextMuted)
        Text("⏱ ${entry.timeFormatted}", fontSize = 11.sp, color = TextMuted)
    }
}

@Composable
private fun DifficultyChip(difficulty: String) {
    val color = difficultyColor(difficulty)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .drawBehind {
                drawRoundRect(
                    color        = color.copy(alpha = 0.4f),
                    cornerRadius = CornerRadius(50.dp.toPx()),
                    style        = Stroke(width = 0.5.dp.toPx())
                )
            }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text       = difficulty,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = color
        )
    }
}

// ── Empty state ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(onStartQuiz: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Surface1),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.History,
                    contentDescription = null,
                    tint               = TextMuted,
                    modifier           = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = "No attempts yet",
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = "Complete a quiz to see your history here.",
                color     = TextMuted,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick        = onStartQuiz,
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
                        text       = "Start a Quiz",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}

// ── Guest state ────────────────────────────────────────────────────────────────

@Composable
private fun GuestState(onLogin: () -> Unit) {
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
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Surface1),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint               = TextMuted,
                    modifier           = Modifier.size(44.dp)
                )
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
                text      = "Sign in to track your quiz history and see your progress over time.",
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

// ── Loading state ──────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
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

// ── Error state ────────────────────────────────────────────────────────────────

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(RedAccent.copy(alpha = 0.12f))
                    .drawBehind {
                        drawCircle(
                            color  = RedAccent.copy(alpha = 0.35f),
                            style  = Stroke(width = 1.dp.toPx())
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
            // Outlined retry button
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
                    verticalAlignment    = Alignment.CenterVertically,
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