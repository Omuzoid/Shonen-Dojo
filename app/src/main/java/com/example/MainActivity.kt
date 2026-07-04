package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStats
import com.example.data.WorkoutLog
import com.example.ui.DojoViewModel
import com.example.ui.components.AnatomyCoach
import com.example.ui.components.AnimeTrainerIllustration
import com.example.ui.components.AuraEffects
import com.example.ui.components.CompanionRenderer
import com.example.ui.components.RadarChart
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: DojoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(viewModel) }
                ) { innerPadding ->
                    // Dynamic theme background coloring (Night vs Day temple mode)
                    val baseBackground = if (viewModel.isDayMode) CosmicBackgroundDay else CosmicBackgroundNight
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(baseBackground)
                            .padding(innerPadding)
                    ) {
                        // Ambient purple/cyan aura background glow
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            HologramPurple.copy(alpha = 0.12f),
                                            Color.Transparent
                                        ),
                                        radius = 2000f
                                    )
                                )
                        )
                        // Header elegant top purple gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF1A0B2E).copy(alpha = 0.45f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Scrollable content area
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            DojoHeader(viewModel)
                            Spacer(modifier = Modifier.height(16.dp))

                            when (viewModel.activeTab) {
                                "Dojo" -> DojoDashboardTab(viewModel)
                                "Roadmap" -> RoadmapProgressTab(viewModel)
                                "Train" -> ActiveTrainTab(viewModel)
                                "Companion" -> CompanionPetTab(viewModel)
                                "Store" -> SystemStoreTab(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(viewModel: DojoViewModel) {
    NavigationBar(
        containerColor = CosmicBackgroundNight.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        modifier = Modifier.border(0.5.dp, HologramPurple.copy(alpha = 0.25f))
    ) {
        val tabs = listOf(
            "Dojo" to Icons.Default.Home,
            "Roadmap" to Icons.Default.Map,
            "Train" to Icons.Default.FitnessCenter,
            "Companion" to Icons.Default.Pets,
            "Store" to Icons.Default.ShoppingCart
        )

        tabs.forEach { (tabName, icon) ->
            val isSelected = viewModel.activeTab == tabName
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    viewModel.playClickSound()
                    viewModel.activeTab = tabName
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tabName,
                        tint = if (isSelected) HologramCyan else TextGray.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = tabName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) HologramCyan else TextGray.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = HologramCyan.copy(alpha = 0.12f)
                ),
                modifier = Modifier.testTag("tab_${tabName.lowercase()}")
            )
        }
    }
}

@Composable
fun DojoHeader(viewModel: DojoViewModel) {
    val stats by viewModel.userStats.collectAsState()
    val level = stats?.level ?: 84
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0B2E).copy(alpha = 0.5f), Color.Transparent)
                ),
                RoundedCornerShape(16.dp)
            )
            .border(1.dp, HologramPurple.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "SYSTEM PROTOCOL V4.2",
                color = HologramCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SHONEN DOJO",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                style = TextStyle(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    shadow = Shadow(HologramPurple, Offset(1f, 1f), 10f)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HeaderStatusBadge(text = "100% OFFLINE", icon = Icons.Default.WifiOff)
                HeaderStatusBadge(text = "NO ADS", icon = Icons.Default.Check)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(
                modifier = Modifier
                    .background(Color(0xFF1A1A2E), RoundedCornerShape(50.dp))
                    .border(1.dp, HologramPurple.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pulsing dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(HologramCyan, CircleShape)
                )
                Text(
                    text = "LVL. $level",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RANK: S-CLASS",
                color = TextGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun HeaderStatusBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .background(HologramCyan.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
            .border(0.5.dp, HologramCyan.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = HologramCyan, modifier = Modifier.size(10.dp))
            Text(text = text, color = HologramCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DojoDashboardTab(viewModel: DojoViewModel) {
    val stats by viewModel.userStats.collectAsState()
    val logs by viewModel.workoutLogs.collectAsState()

    stats?.let { userStats ->
        // Profile Summary Panel
        ProfileSummaryPanel(userStats, viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Daily Quest Highlight Banner
        DailyQuestHighlightBanner(userStats, viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Mentor Selector Matrix
        MentorSelectorSection(userStats, viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Two Column Panel layout (Logs & Radar Chart stats)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TodayWorkoutLogsSection(logs, viewModel)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ShadowStatsPointAllocationSection(userStats, viewModel)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Advanced controls: Warning system Penalty Events & Day/Night Toggle HUD
        HUDControlsSection(viewModel)
    }
}

@Composable
fun ProfileSummaryPanel(stats: UserStats, viewModel: DojoViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar wrapped with active equipped Store Aura!
        AuraEffects(
            auraName = stats.equippedAura,
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, ComicBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AnimeTrainerIllustration(
                    name = stats.selectedMentor,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE HUNTER SYSTEM",
                    color = HologramPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                
                // Gold display with proper dark contrast on light background
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Gold", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    Text(text = "${stats.gold}", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }

            Text(
                text = "Lv. ${stats.level}",
                color = TextLight,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // XP Progression linear bar
            val currentRequiredXp = stats.level * 2500
            val progressPct = stats.xp.toFloat() / currentRequiredXp.toFloat()
            LinearProgressIndicator(
                progress = { progressPct },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, ComicBorder, RoundedCornerShape(4.dp)),
                color = HologramCyan,
                trackColor = CosmicBackgroundNight
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${stats.xp} / $currentRequiredXp XP",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyQuestHighlightBanner(stats: UserStats, viewModel: DojoViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "DAILY SYSTEM PROTOCOL: ${stats.activeRoadmap}",
                color = HologramCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "TODAY'S QUEST: Chest & Triceps (Day ${stats.activeRoadmapDay})",
                color = TextLight,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Complete your daily training set to secure limit break stats.",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { viewModel.startWorkout("Push-Ups") },
                colors = ButtonDefaults.buttonColors(containerColor = HologramCyan),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, ComicBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_workout_button")
            ) {
                Text(
                    text = "START TRAINING SET",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
fun MentorSelectorSection(stats: UserStats, viewModel: DojoViewModel) {
    Column {
        Text(
            text = "CHOOSE YOUR SYSTEM MENTOR",
            color = HologramPurple,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val mentors = listOf(
            Triple("SUNG JIN-WOO", "Agility & Stamina", HologramPurple),
            Triple("GOKU", "Power & Core Track", Color(0xFFFF9100)),
            Triple("SAITAMA", "100-Day Challenge", Color(0xFFFFD600)),
            Triple("ALL MIGHT", "High-Intensity Vitality", Color(0xFF2979FF)),
            Triple("ASH KETCHUM", "Routine & Companions", Color(0xFF00E676))
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(mentors) { (name, subtitle, color) ->
                val isSelected = stats.selectedMentor == name
                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .background(
                            if (isSelected) color.copy(alpha = 0.08f) else CosmicCardNight,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = if (isSelected) 2.5.dp else 1.5.dp,
                            color = if (isSelected) color else ComicBorder.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setMentor(name) }
                        .padding(12.dp)
                        .testTag("mentor_${name.lowercase().replace(" ", "_")}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Custom Hand-Drawn 2D Anime Avatar!
                        AnimeTrainerIllustration(
                            name = name,
                            modifier = Modifier.size(68.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = name,
                            color = TextLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = subtitle,
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayWorkoutLogsSection(logs: List<WorkoutLog>, viewModel: DojoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY'S DAILY QUEST LOG",
                color = TextLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Icon(
                imageVector = Icons.Default.FactCheck,
                contentDescription = null,
                tint = HologramCyan,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val workouts = listOf(
            "Push-Ups" to "4 x 20",
            "Squats" to "4 x 20",
            "Sit-Ups" to "4 x 20",
            "Plank" to "3 x 60s",
            "Running" to "20 min"
        )

        workouts.forEach { (ex, format) ->
            // Check if user has done any of this exercise in logs
            val isDone = logs.any { it.exerciseName == ex }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isDone) Color(0xFF16A34A) else TextGray.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ex,
                        color = if (isDone) TextLight else TextGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = format,
                    color = HologramCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = ComicBorder.copy(alpha = 0.15f))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "DAILY REWARD:", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "+320 XP / +150 Gold / +5 Crystals", color = Color(0xFFD97706), fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun ShadowStatsPointAllocationSection(stats: UserStats, viewModel: DojoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SHADOW STAT POINTS",
                    color = TextLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Available Points: ${stats.statPoints} to Allocate",
                    color = if (stats.statPoints > 0) Color(0xFFEA580C) else TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Icon(imageVector = Icons.Default.QueryStats, contentDescription = null, tint = HologramCyan)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Radar chart showing actual stats
        RadarChart(
            strength = stats.strength,
            speed = stats.speed,
            stamina = stats.stamina,
            intelligence = stats.intelligence,
            vitality = stats.vitality,
            agility = stats.agility,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid buttons for allocating points
        val statsList = listOf(
            "STRENGTH" to stats.strength,
            "SPEED" to stats.speed,
            "STAMINA" to stats.stamina,
            "INTELLIGENCE" to stats.intelligence,
            "VITALITY" to stats.vitality,
            "AGILITY" to stats.agility
        )

        statsList.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEach { (type, valNum) ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(CosmicBackgroundNight, RoundedCornerShape(8.dp))
                            .border(1.5.dp, ComicBorder.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = type, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "$valNum", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }

                        if (stats.statPoints > 0) {
                            IconButton(
                                onClick = { viewModel.allocateStatPoint(type) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(HologramCyan.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, ComicBorder, CircleShape)
                                    .testTag("allocate_${type.lowercase()}")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ComicBorder, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HUDControlsSection(viewModel: DojoViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Penalty warning activator
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF2E0A0F), RoundedCornerShape(12.dp))
                .border(1.dp, AccentCrimson.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable { viewModel.triggerPenaltyEvent() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = AccentCrimson
                )
                Text(
                    text = "PENALTY EVENT",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Day/Night toggle
        Box(
            modifier = Modifier
                .weight(1f)
                .background(CosmicCardNight, RoundedCornerShape(12.dp))
                .border(2.dp, ComicBorder, RoundedCornerShape(12.dp))
                .clickable {
                    viewModel.playClickSound()
                    viewModel.isDayMode = !viewModel.isDayMode
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isDayMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = HologramCyan
                )
                Text(
                    text = if (viewModel.isDayMode) "DAY TEMPLE" else "NIGHT DOJO",
                    color = TextLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun RoadmapProgressTab(viewModel: DojoViewModel) {
    Text(
        text = "CHOOSE YOUR TRAINING ROADMAP",
        color = TextLight,
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    val tracks = listOf(
        Triple("1-Week Induction", "Foundation conditioning baseline", Pair(7, HologramCyan)),
        Triple("4-Week Awakening", "Hypertrophy conditioning threshold", Pair(28, Color(0xFF16A34A))),
        Triple("12-Week Dungeon Raid", "Endurance & massive force transition", Pair(84, Color(0xFFEA580C))),
        Triple("100-Day System Protocol", "Saitama conditioning limit breaker", Pair(100, AccentCrimson)),
        Triple("365-Day Monarch Ascent", "Absolute sovereign year-long track", Pair(365, Color(0xFF7C3AED)))
    )

    tracks.forEach { (title, subtitle, meta) ->
        val (days, color) = meta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(CosmicCardNight, RoundedCornerShape(16.dp))
                .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
                .clickable { viewModel.selectRoadmap(title) }
                .padding(16.dp)
                .testTag("roadmap_${title.lowercase().replace(" ", "_")}")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color.copy(alpha = 0.08f), CircleShape)
                            .border(2.dp, color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = color)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = title, color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text(text = subtitle, color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "$days", color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text(text = "DAYS", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ActiveTrainTab(viewModel: DojoViewModel) {
    if (!viewModel.isWorkoutActive) {
        // Landing state if no workout is run
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicCardNight, RoundedCornerShape(16.dp))
                .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = HologramCyan,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "NO ACTIVE WORKOUT",
                    color = TextLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Launch a routine workout from the main Dojo dashboard to begin.",
                    color = TextGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        return
    }

    // Holographic Workout HUD Overlays
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.currentExerciseName.uppercase(),
                    color = TextLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "${viewModel.currentExerciseSet} / ${viewModel.totalSetsRequired} SETS",
                    color = HologramCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic 3D Anatomy Coach Render Viewport
            AnatomyCoach(
                exerciseName = viewModel.currentExerciseName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WorkoutStatWidget(
                    title = "REPS COMPLETED",
                    value = "${viewModel.repsCompleted} / ${viewModel.repsRequired}",
                    color = HologramCyan
                )
                WorkoutStatWidget(
                    title = "EST CALORIES",
                    value = "${viewModel.caloriesBurned} kcal",
                    color = AccentCrimson
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mentor voice box with synchronized Text-to-Speech active character highlighting!
            MentorSpeechSubtitlesBox(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Action Triggers
            if (viewModel.isResting) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "REST PERIOD: ${viewModel.restTimerSeconds}s",
                        color = Color(0xFFEA580C),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.skipRest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, ComicBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skip_rest_button")
                    ) {
                        Text(text = "SKIP REST", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.completeActiveRep() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, ComicBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("complete_rep_button")
                ) {
                    Text(
                        text = if (viewModel.currentExerciseName == "Plank" || viewModel.currentExerciseName == "Running") "COMPLETE SET" else "PERFORM REP",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutStatWidget(title: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(CosmicBackgroundNight, RoundedCornerShape(8.dp))
            .border(1.5.dp, ComicBorder.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = title, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun MentorSpeechSubtitlesBox(viewModel: DojoViewModel) {
    val quote = viewModel.activeMentorQuote
    val highlight = viewModel.activeWordRange
    val stats by viewModel.userStats.collectAsState()
    val mentorName = stats?.selectedMentor ?: "SUNG JIN-WOO"

    val annotatedString = buildAnnotatedString {
        if (highlight.first != -1 && highlight.second != -1 && highlight.first < quote.length && highlight.second <= quote.length) {
            // Highlight active word in karaoke theme
            append(quote.substring(0, highlight.first))
            withStyle(
                style = SpanStyle(
                    color = HologramPurple,
                    fontWeight = FontWeight.Black,
                    background = HologramCyan.copy(alpha = 0.25f)
                )
            ) {
                append(quote.substring(highlight.first, highlight.second))
            }
            append(quote.substring(highlight.second))
        } else {
            append(quote)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.dp, ComicBorder, RoundedCornerShape(16.dp))
            .clickable { viewModel.speakPhrase(quote) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimeTrainerIllustration(
            name = mentorName,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mentorName.uppercase() + " SPEAKS:",
                color = HologramPurple,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = annotatedString,
                color = TextLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Speak",
            tint = HologramCyan,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CompanionPetTab(viewModel: DojoViewModel) {
    val stats by viewModel.userStats.collectAsState()

    stats?.let { userStats ->
        Column {
            Text(
                text = "ACTIVE SYSTEM COMPANION & MENTOR",
                color = TextLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dynamic 2D Anime Trainer portrait view!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(CosmicCardNight, RoundedCornerShape(16.dp))
                    .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimeTrainerIllustration(
                    name = userStats.selectedMentor,
                    modifier = Modifier.size(240.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Experience Stats bar
            val reqXp = userStats.companionLevel * 180
            val companionProgress = userStats.companionXp.toFloat() / reqXp.toFloat()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicCardNight, RoundedCornerShape(16.dp))
                    .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Mentor Affinity Level ${userStats.companionLevel}", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Text(text = "${userStats.companionXp} / $reqXp XP", color = Color(0xFFEA580C), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { companionProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, ComicBorder, RoundedCornerShape(4.dp)),
                    color = Color(0xFFEA580C),
                    trackColor = CosmicBackgroundNight
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Affinity Mood Status", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF16A34A).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFF16A34A), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "MAX TRUST!", color = Color(0xFF16A34A), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun SystemStoreTab(viewModel: DojoViewModel) {
    val stats by viewModel.userStats.collectAsState()

    stats?.let { userStats ->
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SYSTEM AURA STORE",
                    color = TextLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                
                // Gold display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Gold", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    Text(text = "${userStats.gold}", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }

            Text(
                text = "Buy powerful dynamic elemental auras to surround your avatar frame",
                color = TextGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val auras = listOf(
                "Green Lightning" to 2000,
                "Red Aura" to 2000,
                "Blue Flame" to 2000,
                "Shadow Smoke" to 2000,
                "Golden Sparks" to 2000,
                "Purple Mist" to 2000,
                "Dragon Force" to 2000,
                "Monarch Wings" to 2000
            )

            // Dynamic grid list
            auras.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { (aura, price) ->
                        val purchasedList = try {
                            val arr = org.json.JSONArray(userStats.purchasedAurasJson)
                            val list = mutableListOf<String>()
                            for (i in 0 until arr.length()) {
                                list.add(arr.getString(i))
                            }
                            list
                        } catch (e: Exception) {
                            emptyList()
                        }

                        val isPurchased = purchasedList.contains(aura)
                        val isEquipped = userStats.equippedAura == aura

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 6.dp)
                                .background(CosmicCardNight, RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isEquipped) 2.5.dp else 1.5.dp,
                                    color = if (isEquipped) HologramCyan else ComicBorder.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Live micro preview of the Aura Effects around a tiny star indicator
                                Box(
                                    modifier = Modifier.size(60.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AuraEffects(auraName = aura, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = aura,
                                    color = TextLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.purchaseAura(aura, price) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isEquipped) HologramCyan
                                        else if (isPurchased) HologramPurple
                                        else Color(0xFFEA580C)
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.5.dp, ComicBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("purchase_${aura.lowercase().replace(" ", "_")}")
                                ) {
                                    Text(
                                        text = if (isEquipped) "EQUIPPED"
                                        else if (isPurchased) "EQUIP"
                                        else "$price G",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isEquipped) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
