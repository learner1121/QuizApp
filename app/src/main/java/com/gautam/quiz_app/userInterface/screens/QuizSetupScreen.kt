package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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

// ── Main screen ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    navController: NavController,
    section: String,
    isRandom: Boolean
) {
    var state by remember { mutableStateOf(QuizSetupState()) }

    // Entry animation — same pattern as reference
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
                    colors = listOf(AccentStart.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.05f),
                    radius = size.width * 0.55f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.05f),
                radius = size.width * 0.55f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AccentEnd.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.75f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.75f),
                radius = size.width * 0.5f
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (isRandom) "Random Quiz" else section,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            )
                            Text(
                                text = "Configure your session",
                                color = TextMuted,
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BgDeep
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .background(BgCard)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
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
                            .height(52.dp)
                            .glowEffect(AccentMid, 18.dp, 0.35f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(listOf(AccentStart, AccentEnd)),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Start Quiz",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .offset(y = offsetY.dp)
                    .graphicsLayer(alpha = contentAlpha)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SetupSummaryRow(state)

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

                SetupSection(title = "Questions", subtitle = "How many questions in this session?") {
                    OptionChipRow(
                        options = listOf("5", "10", "20"),
                        selected = state.questionCount.toString(),
                        chipColors = emptyMap()
                    ) { state = state.copy(questionCount = it.toInt()) }
                }

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

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Summary row ────────────────────────────────────────────────────────────────

@Composable
private fun SetupSummaryRow(state: QuizSetupState) {
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
            modifier = Modifier.fillMaxWidth(),
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface2)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

// ── Section header ─────────────────────────────────────────────────────────────

@Composable
private fun SetupSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 11.dp)
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
    val resolvedAccent = accentColor ?: AccentMid

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) resolvedAccent.copy(alpha = 0.22f) else Surface1,
        animationSpec = tween(200),
        label = "chipContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) TextPrimary else TextMuted,
        animationSpec = tween(200),
        label = "chipContentColor"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .drawBehind {
                drawRoundRect(
                    brush = if (isSelected)
                        Brush.horizontalGradient(
                            listOf(resolvedAccent, resolvedAccent.copy(alpha = 0.7f))
                        )
                    else
                        Brush.horizontalGradient(listOf(Border, Border)),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = Stroke(width = if (isSelected) 1.5.dp.toPx() else 0.8.dp.toPx())
                )
            }
            .clickable(onClick = onClick),
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
                    tint = resolvedAccent,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}