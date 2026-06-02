package com.gautam.quiz_app.userInterface.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ── Theme ─────────────────────────────────────────────────────────────────────
private val BgDeep      = Color(0xFF0A0A0F)
private val Surface1    = Color(0xFF1C1C27)
private val Border      = Color(0xFF2A2A3A)
private val AccentStart = Color(0xFF7C3AED)
private val AccentEnd   = Color(0xFF4F8EF7)
private val AccentMid   = Color(0xFF9B5CF6)
private val TextPrimary = Color(0xFFF0F0FF)
private val TextMuted   = Color(0xFF8888AA)

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

// ── Section Data ──────────────────────────────────────────────────────────────
private data class RandomSection(
    val name: String,
    val label: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tag: String
)

private val randomSections = listOf(
    RandomSection(
        name        = "OOPs",
        label       = "OOPs",
        description = "Random mix of OOP concepts & patterns",
        icon        = Icons.Outlined.Hub,
        accentColor = Color(0xFF7C3AED),
        tag         = "RANDOM"
    ),
    RandomSection(
        name        = "OS",
        label       = "Operating Systems",
        description = "Surprise OS questions across all topics",
        icon        = Icons.Outlined.Memory,
        accentColor = Color(0xFF4F8EF7),
        tag         = "RANDOM"
    ),
    RandomSection(
        name        = "DBMS",
        label       = "DBMS",
        description = "Random SQL, normalization & more",
        icon        = Icons.Outlined.Storage,
        accentColor = Color(0xFF059669),
        tag         = "RANDOM"
    ),
    RandomSection(
        name        = "CN",
        label       = "Computer Networks",
        description = "Mixed CN questions from all layers",
        icon        = Icons.Outlined.Wifi,
        accentColor = Color(0xFFF59E0B),
        tag         = "RANDOM"
    )
)

// ── RandomSectionScreen ───────────────────────────────────────────────────────
@Composable
fun RandomSectionScreen(navHostController: NavHostController) {

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
        // Background orbs
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .offset(y = offsetY.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navHostController.navigate("HomeScreen") }) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface1),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "Random Questions",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.3).sp
                )

                Spacer(Modifier.weight(1f))

                // Balance spacer
                Spacer(Modifier.size(48.dp))
            }

            // ── Subtitle + pill ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Pick a topic for a random challenge",
                    fontSize = 13.sp,
                    color = TextMuted,
                    letterSpacing = 0.2.sp
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentMid.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Shuffle,
                            contentDescription = null,
                            tint = AccentMid,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${randomSections.size} sections · randomised",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentMid,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Section Cards ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                randomSections.forEach { section ->
                    RandomSectionCard(
                        section = section,
                        onClick = {
                            navHostController.navigate("quizSetup/${section.name}/true")
                        }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── Random Section Card ───────────────────────────────────────────────────────
@Composable
private fun RandomSectionCard(section: RandomSection, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .glowEffect(section.accentColor, 16.dp, 0.18f)
            .clickable(onClick = onClick)
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
                        colors = listOf(
                            section.accentColor,
                            section.accentColor.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        // Decorative bg circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp)
                .background(section.accentColor.copy(alpha = 0.06f), CircleShape)
        )

        // Shuffle badge — distinguishes this from normal SectionScreen
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clip(CircleShape)
                .background(section.accentColor.copy(alpha = 0.12f))
                .padding(5.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shuffle,
                contentDescription = "Random",
                tint = section.accentColor,
                modifier = Modifier.size(11.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(section.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = section.label,
                    tint = section.accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Text block
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(section.accentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = section.tag,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = section.accentColor,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Random${section.label} Question",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = section.description,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }

            // Chevron
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(section.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = section.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}