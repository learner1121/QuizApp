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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// ── Shared Theme (mirrors LoginScreen) ───────────────────────────────────────
private val BgDeep      = Color(0xFF0A0A0F)
private val BgCard      = Color(0xFF13131A)
private val AccentStart = Color(0xFF7C3AED)
private val AccentEnd   = Color(0xFF4F8EF7)
private val AccentMid   = Color(0xFF9B5CF6)
private val Surface1    = Color(0xFF1C1C27)
private val Surface2    = Color(0xFF1E1E2E)
private val Border      = Color(0xFF2A2A3A)
private val TextPrimary = Color(0xFFF0F0FF)
private val TextMuted   = Color(0xFF8888AA)

// ── Glow (same as LoginScreen) ────────────────────────────────────────────────
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

// ── Bottom Nav Items ──────────────────────────────────────────────────────────
private data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Home",        "HomeScreen",          Icons.Filled.Home,        Icons.Outlined.Home),
    BottomNavItem("Sections",    "SectionScreen",       Icons.Filled.GridView,    Icons.Outlined.GridView),
    BottomNavItem("Random",      "RandomSectionScreen", Icons.Filled.Shuffle,     Icons.Outlined.Shuffle),
    BottomNavItem("Leaderboard", "leaderboard",         Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
    BottomNavItem("Profile",     "profile",             Icons.Filled.Person,      Icons.Outlined.Person)
)

// ── Drawer Nav Items ──────────────────────────────────────────────────────────
private data class DrawerNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val tint: Color
)

private val drawerNavItems = listOf(
    DrawerNavItem("Home",           "HomeScreen",          Icons.Outlined.Home,        Color(0xFF9B5CF6)),
    DrawerNavItem("Sections",       "SectionScreen",       Icons.Outlined.GridView,    Color(0xFF4F8EF7)),
    DrawerNavItem("Random Quiz",    "RandomSectionScreen", Icons.Outlined.Shuffle,     Color(0xFF34D399)),
    DrawerNavItem("History",        "history",             Icons.Outlined.History,     Color(0xFFFBBF24)),
    DrawerNavItem("Leaderboard",    "leaderboard",         Icons.Outlined.Leaderboard, Color(0xFFF87171)),
    DrawerNavItem("Profile",        "profile",             Icons.Outlined.Person,      Color(0xFF60A5FA))
)

// ── Quiz Card Data ────────────────────────────────────────────────────────────
private data class QuizCard(
    val title: String,
    val subtitle: String,
    val route: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val icon: ImageVector,
    val tag: String
)

private val quizCards = listOf(
    QuizCard(
        title = "Normal Quiz",
        subtitle = "Topic-wise structured questions",
        route = "SectionScreen",
        gradientStart = Color(0xFF7C3AED),
        gradientEnd   = Color(0xFF4F8EF7),
        icon  = Icons.Filled.MenuBook,
        tag   = "CLASSIC"
    ),
    QuizCard(
        title = "Random Quiz",
        subtitle = "Surprise questions from all topics",
        route = "RandomSectionScreen",
        gradientStart = Color(0xFF059669),
        gradientEnd   = Color(0xFF34D399),
        icon  = Icons.Filled.Shuffle,
        tag   = "RANDOM"
    )
)

// ── HomeScreen ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val currentRoute  = remember { mutableStateOf("HomeScreen") }

    // Entry animation
    val offsetY by produceState(initialValue = 40f) {
        animate(40f, 0f, animationSpec = tween(600, easing = EaseOutCubic)) { v, _ -> value = v }
    }
    val contentAlpha by produceState(initialValue = 0f) {
        animate(0f, 1f, animationSpec = tween(600, easing = EaseOutCubic)) { v, _ -> value = v }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute.value,
                onNavigate   = { route ->
                    currentRoute.value = route
                    navController.navigate(route)
                    scope.launch { drawerState.close() }
                }
            )
        },
        scrimColor = Color(0xFF000000).copy(alpha = 0.6f)
    ) {
        Scaffold(
            containerColor = BgDeep,
            topBar = {
                HomeTopBar(onMenuClick = { scope.launch { drawerState.open() } })
            },
            bottomBar = {
                AppBottomNav(
                    currentRoute = currentRoute.value,
                    onNavigate   = { route ->
                        currentRoute.value = route
                        navController.navigate(route)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgDeep)
            ) {
                // Background orbs (same as Login)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush  = Brush.radialGradient(
                            colors = listOf(AccentStart.copy(alpha = 0.14f), Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.1f),
                            radius = size.width * 0.6f
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.1f),
                        radius = size.width * 0.6f
                    )
                    drawCircle(
                        brush  = Brush.radialGradient(
                            colors = listOf(AccentEnd.copy(alpha = 0.10f), Color.Transparent),
                            center = Offset(size.width * 0.1f, size.height * 0.7f),
                            radius = size.width * 0.5f
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.7f),
                        radius = size.width * 0.5f
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                        .offset(y = offsetY.dp)
                        .graphicsLayer(alpha = contentAlpha)
                ) {
                    Spacer(Modifier.height(24.dp))

                    // Greeting
                    Text(
                        text = "Welcome back 👋",
                        fontSize = 13.sp,
                        color = TextMuted,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "What will you\nlearn today?",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 32.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    // Stats strip
                    StatsStrip()

                    Spacer(Modifier.height(28.dp))

                    // Section header
                    SectionHeader("Choose a Mode")

                    Spacer(Modifier.height(12.dp))

                    // Quiz cards
                    quizCards.forEach { card ->
                        QuizModeCard(card = card, onClick = { navController.navigate(card.route) })
                        Spacer(Modifier.height(12.dp))
                    }

                    Spacer(Modifier.height(8.dp))

                    // Quick links
                    SectionHeader("Quick Access")
                    Spacer(Modifier.height(12.dp))
                    QuickAccessRow(navController)

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ── Top App Bar ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "QuizMaster",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface1),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentStart, AccentEnd),
                                start  = Offset(0f, 0f),
                                end    = Offset(100f, 100f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BgDeep,
            scrolledContainerColor = BgCard
        )
    )
}

// ── Stats Strip ───────────────────────────────────────────────────────────────
@Composable
private fun StatsStrip() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatChip(label = "Streak", value = "7 🔥", modifier = Modifier.weight(1f))
        StatChip(label = "Solved", value = "142",  modifier = Modifier.weight(1f))
        StatChip(label = "Rank",   value = "#24",  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(text = label, fontSize = 11.sp, color = TextMuted, letterSpacing = 0.3.sp)
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.verticalGradient(colors = listOf(AccentStart, AccentEnd))
                )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            letterSpacing = 0.2.sp
        )
    }
}

// ── Quiz Mode Card ────────────────────────────────────────────────────────────
@Composable
private fun QuizModeCard(card: QuizCard, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(card.gradientStart, card.gradientEnd),
                    start  = Offset(0f, 0f),
                    end    = Offset(900f, 300f)
                )
            )
            .glowEffect(card.gradientStart, 20.dp, 0.30f)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp)
    ) {
        // Decorative circle in background
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-16).dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Tag pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.20f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.tag,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = card.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = card.subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ── Quick Access Row ──────────────────────────────────────────────────────────
@Composable
private fun QuickAccessRow(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickChip(
            label   = "History",
            icon    = Icons.Outlined.History,
            tint    = Color(0xFFFBBF24),
            modifier = Modifier.weight(1f),
            onClick  = { navController.navigate("history") }
        )
        QuickChip(
            label   = "Leaderboard",
            icon    = Icons.Outlined.Leaderboard,
            tint    = Color(0xFFF87171),
            modifier = Modifier.weight(1f),
            onClick  = { navController.navigate("leaderboard") }
        )
        QuickChip(
            label   = "Profile",
            icon    = Icons.Outlined.Person,
            tint    = Color(0xFF60A5FA),
            modifier = Modifier.weight(1f),
            onClick  = { navController.navigate("profile") }
        )
    }
}

@Composable
private fun QuickChip(
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            letterSpacing = 0.2.sp
        )
    }
}

// ── Bottom Navigation ─────────────────────────────────────────────────────────
@Composable
private fun AppBottomNav(currentRoute: String, onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDeep)
    ) {
        // Top divider line
        HorizontalDivider(color = Border, thickness = 0.5.dp)

        NavigationBar(
            containerColor = BgCard,
            contentColor   = TextMuted,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(top = 0.5.dp)
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick  = { if (!selected) onNavigate(item.route) },
                    icon = {
                        Box(contentAlignment = Alignment.Center) {
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AccentMid.copy(alpha = 0.15f))
                                )
                            }
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            letterSpacing = 0.2.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = AccentMid,
                        selectedTextColor   = AccentMid,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor      = Color.Transparent
                    )
                )
            }
        }
    }
}

// ── Navigation Drawer ─────────────────────────────────────────────────────────
@Composable
private fun AppDrawer(currentRoute: String, onNavigate: (String) -> Unit) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = BgCard,
        drawerContentColor   = TextPrimary
    ) {
        // Drawer header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface2)
                .padding(24.dp)
        ) {
            // Bg orb
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(AccentStart.copy(alpha = 0.25f), Color.Transparent),
                        radius = 300f
                    ),
                    center = Offset(-40f, 40f),
                    radius = 300f
                )
            }

            Column {
                // App icon small
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentStart, AccentEnd),
                                start  = Offset(0f, 0f),
                                end    = Offset(200f, 200f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Psychology,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "QuizMaster",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Level up your knowledge",
                    fontSize = 12.sp,
                    color = TextMuted,
                    letterSpacing = 0.2.sp
                )
            }
        }

        HorizontalDivider(color = Border, thickness = 0.5.dp)

        Spacer(Modifier.height(8.dp))

        // Nav items
        drawerNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            DrawerItem(
                item       = item,
                isSelected = isSelected,
                onClick    = { onNavigate(item.route) }
            )
        }

        Spacer(Modifier.weight(1f))

        HorizontalDivider(color = Border, thickness = 0.5.dp)

        // Footer version tag
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "v1.0 · QuizMaster",
                fontSize = 11.sp,
                color = TextMuted.copy(alpha = 0.5f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun DrawerItem(item: DrawerNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) item.tint.copy(alpha = 0.10f) else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.tint.copy(alpha = if (isSelected) 0.20f else 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = item.tint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = item.label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) item.tint else TextPrimary
            )

            if (isSelected) {
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(item.tint)
                )
            }
        }
    }
}

// ── ContentCards (kept for backward compat) ───────────────────────────────────
@Composable
fun ContentCards(
    text: String,
    color: Color,
    width: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}