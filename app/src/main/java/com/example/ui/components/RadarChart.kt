package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    strength: Int,
    speed: Int,
    stamina: Int,
    intelligence: Int,
    vitality: Int,
    agility: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val attributes = listOf(
        "STRENGTH" to strength,
        "SPEED" to speed,
        "STAMINA" to stamina,
        "INTELLIGENCE" to intelligence,
        "VITALITY" to vitality,
        "AGILITY" to agility
    )

    Box(
        modifier = modifier
            .testTag("radar_chart_container")
            .background(Color(0xFF070814).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.width.coerceAtMost(size.height) / 2.5f)

            // 1. Draw concentric hexagon background rings (Grid steps)
            val numSteps = 5
            val stepColor = Color(0xFF8C9EFF).copy(alpha = 0.15f)
            for (step in 1..numSteps) {
                val radius = maxRadius * (step.toFloat() / numSteps)
                val gridPath = Path().apply {
                    for (i in 0 until 6) {
                        val angle = i * (Math.PI / 3) - (Math.PI / 2) // rotate -90 deg to put Strength at top
                        val x = center.x + radius * cos(angle).toFloat()
                        val y = center.y + radius * sin(angle).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(
                    path = gridPath,
                    color = stepColor,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // 2. Draw axis lines from center to vertices
            for (i in 0 until 6) {
                val angle = i * (Math.PI / 3) - (Math.PI / 2)
                val target = Offset(
                    center.x + maxRadius * cos(angle).toFloat(),
                    center.y + maxRadius * sin(angle).toFloat()
                )
                drawLine(
                    color = stepColor,
                    start = center,
                    end = target,
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 3. Draw user stats polygon (Value overlay)
            val valuePath = Path()
            val statPoints = mutableListOf<Offset>()
            
            for (i in 0 until 6) {
                val value = attributes[i].second
                // Normalize value out of 100 max
                val pct = (value.toFloat() / 100f).coerceIn(0.1f, 1.0f)
                val radius = maxRadius * pct
                val angle = i * (Math.PI / 3) - (Math.PI / 2)
                
                val px = center.x + radius * cos(angle).toFloat()
                val py = center.y + radius * sin(angle).toFloat()
                val pt = Offset(px, py)
                statPoints.add(pt)
                
                if (i == 0) valuePath.moveTo(px, py) else valuePath.lineTo(px, py)
            }
            valuePath.close()

            // Draw filled stat overlay (glowing transparent neon-blue)
            drawPath(
                path = valuePath,
                color = Color(0xFF00E5FF).copy(alpha = 0.25f)
            )

            // Draw glowing outline
            drawPath(
                path = valuePath,
                color = Color(0xFF00E5FF),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points at vertices
            for (pt in statPoints) {
                drawCircle(
                    color = Color(0xFF8C9EFF),
                    radius = 4.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = pt
                )
            }

            // 4. Draw Attribute Text labels
            for (i in 0 until 6) {
                val name = attributes[i].first
                val value = attributes[i].second
                val angle = i * (Math.PI / 3) - (Math.PI / 2)
                
                // Offset label slightly outside vertex
                val textDistance = maxRadius + 18.dp.toPx()
                val lx = center.x + textDistance * cos(angle).toFloat()
                val ly = center.y + textDistance * sin(angle).toFloat()

                val labelString = "$name\n$value"
                val textLayoutResult = textMeasurer.measure(
                    text = labelString,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp
                    )
                )

                // Adjust positioning based on vertex quadrant to center text nicely
                val tx = lx - (textLayoutResult.size.width / 2f)
                val ty = ly - (textLayoutResult.size.height / 2f)
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = labelString,
                    topLeft = Offset(tx, ty),
                    style = TextStyle(
                        color = if (i == 0) Color(0xFFFF3D00) else Color(0xFF8C9EFF), // Accent strength
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp
                    )
                )
            }
        }
    }
}
