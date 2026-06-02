package com.example.app.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gautam.quiz_app.R
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.auth.GoogleAuthActivity

// ── Colours ──────────────────────────────────────────────────────────────────
private val BgDeep      = Color(0xFF0A0A0F)
private val BgCard      = Color(0xFF13131A)
private val AccentStart = Color(0xFF7C3AED)   // violet
private val AccentEnd   = Color(0xFF4F8EF7)   // blue
private val AccentMid   = Color(0xFF9B5CF6)
private val Surface1    = Color(0xFF1C1C27)
private val Border      = Color(0xFF2A2A3A)
private val TextPrimary = Color(0xFFF0F0FF)
private val TextMuted   = Color(0xFF8888AA)

// ── Glow helper ──────────────────────────────────────────────────────────────
fun Modifier.glowEffect(color: Color, radius: Dp, alpha: Float = 0.45f): Modifier =
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
                left   = -radius.toPx() / 2,
                top    = -radius.toPx() / 2,
                right  = size.width  + radius.toPx() / 2,
                bottom = size.height + radius.toPx() / 2,
                radiusX = 24.dp.toPx(),
                radiusY = 24.dp.toPx(),
                paint   = paint
            )
        }
    }

// ── Logo + Slogan ─────────────────────────────────────────────────────────────
@Composable
fun LogoWithSlogan() {
    // Floating pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue  = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Glowing icon container
        Box(contentAlignment = Alignment.Center) {
            // Outer glow blob
            Box(
                Modifier
                    .size(90.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentMid.copy(alpha = pulseAlpha), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            // Icon pill container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentStart, AccentEnd),
                            start = Offset(0f, 0f),
                            end   = Offset(200f, 200f)
                        )
                    )
                    .glowEffect(AccentMid, 20.dp, 0.55f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = "QuizMaster Logo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // App name
        Text(
            text = "QuizMaster",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = TextPrimary
        )

        Spacer(Modifier.height(4.dp))

        // Slogan with gradient text trick via canvas
        Text(
            text = "Level up your knowledge",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.3.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

// ── Login Screen ──────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(navController: NavController) {
    val context      = LocalContext.current
    val loginSuccess = remember { mutableStateOf(false) }

    // Check current user
    val currentUser = FirebaseInstanceProvider.firebaseAuthInstance.currentUser
    if (currentUser != null) {
        LaunchedEffect(Unit) {
            navController.navigate("HomeScreen") {
                popUpTo("login") { inclusive = true }
            }
        }
        return
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loginSuccess.value = true
        }
    }

    // Entry animation
    val offsetY by produceState(initialValue = 60f) {
        animate(
            initialValue = 60f, targetValue = 0f,
            animationSpec = tween(700, easing = EaseOutCubic)
        ) { v, _ -> value = v }
    }
    val alpha by produceState(initialValue = 0f) {
        animate(
            initialValue = 0f, targetValue = 1f,
            animationSpec = tween(700, easing = EaseOutCubic)
        ) { v, _ -> value = v }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center
    ) {

        // Background decorative orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(AccentStart.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.15f),
                    radius = size.width * 0.55f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.15f),
                radius = size.width * 0.55f
            )
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(AccentEnd.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.85f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.85f),
                radius = size.width * 0.5f
            )
        }

        // Content card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .offset(y = offsetY.dp)
                .graphicsLayer(alpha = alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            LogoWithSlogan()

            Spacer(Modifier.height(48.dp))

            // ── Google Sign-in Button ──────────────────────────────────────
            Button(
                onClick = {
                    val intent = Intent(context, GoogleAuthActivity::class.java)
                    launcher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
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
                            Brush.horizontalGradient(
                                colors = listOf(AccentStart, AccentEnd)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.google),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Divider OR ────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Border
                )
                Text(
                    text = "  OR  ",
                    color = TextMuted,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Border
                )
            }

            Spacer(Modifier.height(14.dp))

            // ── Guest Button ──────────────────────────────────────────────
            OutlinedButton(
                onClick = { navController.navigate("HomeScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(AccentStart.copy(0.6f), AccentEnd.copy(0.6f))
                    )
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Surface1,
                    contentColor   = TextPrimary
                )
            ) {
                Text(
                    text = "Try as a Guest",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            // Footer note
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                fontSize = 11.sp,
                color = TextMuted.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }

    // Navigate on login success
    if (loginSuccess.value) {
        LaunchedEffect(Unit) {
            navController.navigate("HomeScreen") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}