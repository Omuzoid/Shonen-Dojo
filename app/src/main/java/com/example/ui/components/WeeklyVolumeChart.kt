package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyVolumeChart(
    logs: List<WorkoutLog>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("volume") } // "volume" or "progress"
    var selectedBarIndex by remember { mutableStateOf(-1) } // Interactive tooltip index
    val textMeasurer = rememberTextMeasurer()

    // Retrieve last 7 days
    val last7DaysData = remember(logs) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayLabelSdf = SimpleDateFormat("E", Locale.getDefault()) // Mon, Tue...
        val dateLabelSdf = SimpleDateFormat("MM/dd", Locale.getDefault()) // 07/04...

        val calendar = Calendar.getInstance()
        val days = (0..6).map { offset ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            cal.time
        }.reversed()

        days.map { day ->
            val dateStr = sdf.format(day)
            val label = dayLabelSdf.format(day)
            val dateLabel = dateLabelSdf.format(day)
            
            // Filter logs for this day
            val dayLogs = logs.filter { it.date == dateStr }
            val repsCount = dayLogs.sumOf { it.reps * it.sets }
            val caloriesCount = dayLogs.sumOf { it.calories }
            val setsCount = dayLogs.sumOf { it.sets }

            DailyWorkoutSummary(
                dateStr = dateStr,
                label = label,
                dateLabel = dateLabel,
                totalReps = repsCount,
                totalCalories = caloriesCount,
                totalSets = setsCount
            )
        }
    }

    Column(
        modifier = modifier
            .testTag("weekly_chart_panel")
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Section Header with Anime Styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SYSTEM GROWTH METRICS",
                    color = HologramPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = if (selectedTab == "volume") "Weekly Rep Volume" else "Limit Break Progress",
                    color = TextLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
            
            // Segmented Button Bar with Manga styling
            Row(
                modifier = Modifier
                    .background(CosmicBackgroundNight, RoundedCornerShape(8.dp))
                    .border(1.5.dp, ComicBorder, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedTab == "volume") HologramCyan else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            selectedTab = "volume"
                            selectedBarIndex = -1
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Volume",
                        tint = if (selectedTab == "volume") Color.White else TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedTab == "progress") HologramPurple else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            selectedTab = "progress"
                            selectedBarIndex = -1
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Progress",
                        tint = if (selectedTab == "progress") Color.White else TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            if (selectedTab == "volume") {
                // Volume Bar Chart
                val maxVolume = last7DaysData.maxOf { it.totalReps }.coerceAtLeast(40).toFloat()
                
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(last7DaysData) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val itemWidth = width / 7f
                                val index = (offset.x / itemWidth).toInt().coerceIn(0, 6)
                                selectedBarIndex = if (selectedBarIndex == index) -1 else index
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val spacing = 12.dp.toPx()
                    val barWidth = (canvasWidth / 7f) - spacing
                    
                    // Draw horizontal baseline
                    drawLine(
                        color = ComicBorder,
                        start = Offset(0f, canvasHeight - 20.dp.toPx()),
                        end = Offset(canvasWidth, canvasHeight - 20.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )

                    last7DaysData.forEachIndexed { index, summary ->
                        val leftX = (index * (canvasWidth / 7f)) + (spacing / 2f)
                        val barHeightMax = canvasHeight - 45.dp.toPx()
                        val pct = summary.totalReps / maxVolume
                        val barHeight = barHeightMax * pct
                        val topY = (canvasHeight - 20.dp.toPx() - barHeight).coerceIn(10.dp.toPx(), canvasHeight)

                        val isSelected = selectedBarIndex == index
                        
                        // Draw shadow effect for bars (classic manga stylistic shading)
                        if (summary.totalReps > 0) {
                            drawRoundRect(
                                color = ComicBorder.copy(alpha = 0.2f),
                                topLeft = Offset(leftX + 4.dp.toPx(), topY + 4.dp.toPx()),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )

                            // Main Bar Body (Neon Cyan fill)
                            drawRoundRect(
                                color = if (isSelected) HologramPurple else HologramCyan,
                                topLeft = Offset(leftX, topY),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )

                            // Manga Outline Border around Bar
                            drawRoundRect(
                                color = ComicBorder,
                                topLeft = Offset(leftX, topY),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }

                        // Day label text drawing
                        val textLayoutResult = textMeasurer.measure(
                            text = summary.label,
                            style = TextStyle(
                                color = if (isSelected) HologramPurple else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        val tx = leftX + (barWidth / 2f) - (textLayoutResult.size.width / 2f)
                        val ty = canvasHeight - 16.dp.toPx()
                        drawText(
                            textMeasurer = textMeasurer,
                            text = summary.label,
                            topLeft = Offset(tx, ty),
                            style = TextStyle(
                                color = if (isSelected) HologramPurple else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            } else {
                // Progress Line Chart (Cumulative Work Tracker)
                var cumulativeCalories = 0
                val progressPoints = last7DaysData.map {
                    cumulativeCalories += it.totalCalories
                    cumulativeCalories
                }
                val maxProgress = progressPoints.maxOrNull()?.coerceAtLeast(100)?.toFloat() ?: 100f

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(progressPoints) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val itemWidth = width / 7f
                                val index = (offset.x / itemWidth).toInt().coerceIn(0, 6)
                                selectedBarIndex = if (selectedBarIndex == index) -1 else index
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val spacing = canvasWidth / 7f
                    val bottomY = canvasHeight - 20.dp.toPx()
                    val chartHeight = canvasHeight - 45.dp.toPx()

                    // Draw baseline
                    drawLine(
                        color = ComicBorder,
                        start = Offset(0f, bottomY),
                        end = Offset(canvasWidth, bottomY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw connecting line pathway
                    val path = Path()
                    val points = mutableListOf<Offset>()

                    progressPoints.forEachIndexed { index, energy ->
                        val x = (index * spacing) + (spacing / 2f)
                        val pct = energy / maxProgress
                        val y = bottomY - (chartHeight * pct)
                        points.add(Offset(x, y))
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    // Draw neon glow shadow path
                    val shadowPath = Path().apply {
                        addPath(path)
                        lineTo(points.last().x, bottomY)
                        lineTo(points.first().x, bottomY)
                        close()
                    }
                    drawPath(
                        path = shadowPath,
                        color = HologramPurple.copy(alpha = 0.08f)
                    )

                    // Draw line stroke
                    drawPath(
                        path = path,
                        color = HologramPurple,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw outline for additional crispness
                    drawPath(
                        path = path,
                        color = ComicBorder,
                        style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw circular nodes
                    points.forEachIndexed { index, pt ->
                        val isSelected = selectedBarIndex == index
                        // Thick border circles
                        drawCircle(
                            color = ComicBorder,
                            radius = if (isSelected) 7.dp.toPx() else 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = if (isSelected) HologramCyan else Color.White,
                            radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                            center = pt
                        )

                        // Day label text
                        val label = last7DaysData[index].label
                        val textLayoutResult = textMeasurer.measure(
                            text = label,
                            style = TextStyle(
                                color = if (isSelected) HologramPurple else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        val tx = pt.x - (textLayoutResult.size.width / 2f)
                        val ty = canvasHeight - 16.dp.toPx()
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = Offset(tx, ty),
                            style = TextStyle(
                                color = if (isSelected) HologramPurple else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Active Interactive Tooltip Box
        if (selectedBarIndex != -1) {
            val summary = last7DaysData[selectedBarIndex]
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicBackgroundNight, RoundedCornerShape(12.dp))
                    .border(1.5.dp, ComicBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DAY STATS: ${summary.dateLabel} (${summary.label.uppercase()})",
                            color = HologramPurple,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Volume: ${summary.totalReps} Reps",
                                color = TextLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${summary.totalSets} Sets",
                                color = TextGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${summary.totalCalories} kcal",
                                color = AccentOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Text(
                        text = if (summary.totalReps >= 80) "S-RANK" else if (summary.totalReps >= 40) "A-RANK" else if (summary.totalReps > 0) "B-RANK" else "REST DAY",
                        color = if (summary.totalReps >= 80) AccentCrimson else if (summary.totalReps >= 40) HologramCyan else TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(
                                (if (summary.totalReps >= 80) AccentCrimson else if (summary.totalReps >= 40) HologramCyan else TextGray).copy(alpha = 0.08f),
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (summary.totalReps >= 80) AccentCrimson else if (summary.totalReps >= 40) HologramCyan else TextGray,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        } else {
            // General status footer of Weekly Training Volume
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 Tap any chart point/bar to inspect detailed training ranks.",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class DailyWorkoutSummary(
    val dateStr: String,
    val label: String,
    val dateLabel: String,
    val totalReps: Int,
    val totalCalories: Int,
    val totalSets: Int
)
