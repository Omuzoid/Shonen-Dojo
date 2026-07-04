package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompanionRenderer(
    level: Int,
    modifier: Modifier = Modifier
) {
    // Determine evolution stage
    val stageName = when {
        level >= 50 -> "SUN MONARCH WOLF"
        level >= 25 -> "FIERY SENTINEL"
        level >= 12 -> "FLAME CUB"
        else -> "FIRE CHIBI"
    }

    val stageLevelReq = when {
        level >= 50 -> "LEGENDARY EVOLUTION"
        level >= 25 -> "STAGE 3 (MAX POWER)"
        level >= 12 -> "STAGE 2 (ADOLESCENT)"
        else -> "STAGE 1 (BABY)"
    }

    // Animation loops for breathing, tail wagging, and ember particles
    val transition = rememberInfiniteTransition(label = "CompanionAnimation")
    
    val breatheScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "companion_breathe"
    )

    val tailWagAngle by transition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = SineWaveEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "companion_tail"
    )

    val particleOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles_up"
    )

    Box(
        modifier = modifier
            .testTag("companion_renderer_container")
            .background(Color(0xFF070814).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stageName,
                    color = Color(0xFFFF5252),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stageLevelReq,
                    color = Color(0xFFFF9100),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val cx = size.width / 2f
                val cy = size.height / 1.7f
                val baseRadius = size.width.coerceAtMost(size.height) * 0.22f

                // 1. Draw Fiery Aura Backing
                drawFlameAura(cx, cy, baseRadius, level, breatheScale)

                // 2. Draw Floating Spark Particles
                drawFloatingSparks(cx, cy, baseRadius, particleOffset)

                // 3. Draw Companion Body depending on evolution level
                drawEvolvedCompanion(cx, cy, baseRadius, level, breatheScale, tailWagAngle)

                // 4. Draw Floor Runic Portal Circle
                drawRunicBase(cx, cy + baseRadius * 1.1f, baseRadius)
            }
        }
    }
}

// Custom easing for tail wag
private val SineWaveEasing = Easing { fraction ->
    sin(fraction * Math.PI.toFloat())
}

private fun DrawScope.drawFlameAura(
    cx: Float,
    cy: Float,
    r: Float,
    level: Int,
    breathe: Float
) {
    val auraColor = when {
        level >= 50 -> Color(0xFFE040FB) // purple celestial monarch
        level >= 25 -> Color(0xFFFF3D00) // heavy crimson
        level >= 12 -> Color(0xFFFF9100) // orange flame
        else -> Color(0xFFFFD600) // yellow baby spark
    }

    // Concentric glowing expanding fields
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(auraColor.copy(alpha = 0.4f * breathe), Color.Transparent),
            center = Offset(cx, cy),
            radius = r * 1.8f
        ),
        radius = r * 1.8f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawFloatingSparks(
    cx: Float,
    cy: Float,
    r: Float,
    offset: Float
) {
    val sparks = listOf(
        Offset(-r * 0.8f, r * 0.2f),
        Offset(r * 0.9f, -r * 0.4f),
        Offset(-r * 0.3f, -r * 0.8f),
        Offset(r * 0.5f, r * 0.5f),
        Offset(-r * 1.1f, -r * 0.2f)
    )

    for (spark in sparks) {
        // Spark floats upwards based on anim offset
        val sx = cx + spark.x
        val sy = cy + spark.y - (offset % 60.dp.toPx())
        val alpha = (1f - (offset / 100f)).coerceIn(0f, 1f)
        
        drawCircle(
            color = Color(0xFFFF9100).copy(alpha = alpha * 0.7f),
            radius = 3.dp.toPx(),
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawRunicBase(cx: Float, cy: Float, r: Float) {
    // Floor magic circle
    drawOval(
        color = Color(0xFF00E5FF).copy(alpha = 0.2f),
        topLeft = Offset(cx - r * 1.3f, cy - 8.dp.toPx()),
        size = Size(r * 2.6f, 16.dp.toPx()),
        style = Stroke(width = 1.5.dp.toPx())
    )
    drawOval(
        color = Color(0xFF00E5FF).copy(alpha = 0.08f),
        topLeft = Offset(cx - r * 1.5f, cy - 10.dp.toPx()),
        size = Size(r * 3f, 20.dp.toPx()),
        style = Stroke(width = 1f)
    )
}

private fun DrawScope.drawEvolvedCompanion(
    cx: Float,
    cy: Float,
    r: Float,
    level: Int,
    breathe: Float,
    tailWag: Float
) {
    val bodyColor = when {
        level >= 50 -> Color(0xFF311B92) // Celestial deep indigo
        level >= 25 -> Color(0xFFB71C1C) // Dark blood red
        level >= 12 -> Color(0xFFE65100) // Deep warm orange
        else -> Color(0xFFFF9100) // Friendly bright yellow-orange
    }

    val accentColor = when {
        level >= 50 -> Color(0xFFE040FB) // Radiant purple
        level >= 25 -> Color(0xFFFF1744) // Bright red
        level >= 12 -> Color(0xFFFF3D00) // Hot orange
        else -> Color(0xFFFFEB3B) // Light yellow
    }

    val sizeMultiplier = when {
        level >= 50 -> 1.3f
        level >= 25 -> 1.15f
        level >= 12 -> 1.0f
        else -> 0.85f
    }

    val finalRadius = r * sizeMultiplier * breathe

    // 1. Draw Tail (Wagging!)
    val tailPath = Path().apply {
        val tailBaseX = cx - finalRadius * 0.4f
        val tailBaseY = cy + finalRadius * 0.3f
        moveTo(tailBaseX, tailBaseY)
        
        // Sway end of tail
        val swayX = tailBaseX - finalRadius * 0.8f + (tailWag * 0.4f)
        val swayY = tailBaseY - finalRadius * 0.4f + (tailWag * 0.2f)
        
        quadraticTo(
            tailBaseX - finalRadius * 0.5f, tailBaseY - finalRadius * 0.1f,
            swayX, swayY
        )
        // Fluffy flame tail end
        lineTo(swayX - 10f, swayY - 15f)
        quadraticTo(
            tailBaseX - finalRadius * 0.4f, tailBaseY - finalRadius * 0.4f,
            tailBaseX, tailBaseY
        )
    }
    drawPath(path = tailPath, color = accentColor)

    // 2. Body Draw
    drawCircle(
        color = bodyColor,
        radius = finalRadius * 0.5f,
        center = Offset(cx, cy + finalRadius * 0.2f)
    )

    // 3. Head Draw (breathes up and down)
    val headY = cy - finalRadius * 0.3f
    drawCircle(
        color = bodyColor,
        radius = finalRadius * 0.45f,
        center = Offset(cx, headY)
    )

    // 4. Ears
    val leftEar = Path().apply {
        moveTo(cx - finalRadius * 0.4f, headY - finalRadius * 0.1f)
        lineTo(cx - finalRadius * 0.55f, headY - finalRadius * 0.75f)
        lineTo(cx - finalRadius * 0.15f, headY - finalRadius * 0.35f)
        close()
    }
    val rightEar = Path().apply {
        moveTo(cx + finalRadius * 0.4f, headY - finalRadius * 0.1f)
        lineTo(cx + finalRadius * 0.55f, headY - finalRadius * 0.75f)
        lineTo(cx + finalRadius * 0.15f, headY - finalRadius * 0.35f)
        close()
    }
    drawPath(path = leftEar, color = bodyColor)
    drawPath(path = rightEar, color = bodyColor)

    // Inside ears (inner glowing color)
    val leftEarInner = Path().apply {
        moveTo(cx - finalRadius * 0.35f, headY - finalRadius * 0.15f)
        lineTo(cx - finalRadius * 0.48f, headY - finalRadius * 0.6f)
        lineTo(cx - finalRadius * 0.2f, headY - finalRadius * 0.3f)
        close()
    }
    val rightEarInner = Path().apply {
        moveTo(cx + finalRadius * 0.35f, headY - finalRadius * 0.15f)
        lineTo(cx + finalRadius * 0.48f, headY - finalRadius * 0.6f)
        lineTo(cx + finalRadius * 0.2f, headY - finalRadius * 0.3f)
        close()
    }
    drawPath(path = leftEarInner, color = accentColor)
    drawPath(path = rightEarInner, color = accentColor)

    // 5. Legendary Crown/Horns if Level >= 25
    if (level >= 25) {
        val hornPath = Path().apply {
            moveTo(cx, headY - finalRadius * 0.4f)
            lineTo(cx - finalRadius * 0.15f, headY - finalRadius * 0.9f)
            lineTo(cx, headY - finalRadius * 0.6f)
            lineTo(cx + finalRadius * 0.15f, headY - finalRadius * 0.9f)
            close()
        }
        drawPath(path = hornPath, color = Color(0xFFFFD600)) // Golden Crown Horn
    }

    // 6. Cute / Fierce Anime Glowing Eyes
    val eyeColor = if (level >= 25) Color(0xFF00E5FF) else Color.White
    val eyeRadius = finalRadius * 0.08f
    drawCircle(
        color = eyeColor,
        radius = eyeRadius,
        center = Offset(cx - finalRadius * 0.18f, headY - finalRadius * 0.05f)
    )
    drawCircle(
        color = eyeColor,
        radius = eyeRadius,
        center = Offset(cx + finalRadius * 0.18f, headY - finalRadius * 0.05f)
    )
    
    // Slits for fierce look at higher levels
    if (level >= 12) {
        drawLine(
            color = Color.Black,
            start = Offset(cx - finalRadius * 0.23f, headY - finalRadius * 0.12f),
            end = Offset(cx - finalRadius * 0.13f, headY - finalRadius * 0.03f),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Black,
            start = Offset(cx + finalRadius * 0.23f, headY - finalRadius * 0.12f),
            end = Offset(cx + finalRadius * 0.13f, headY - finalRadius * 0.03f),
            strokeWidth = 2.dp.toPx()
        )
    }

    // Cheek blush if baby Chibi
    if (level < 12) {
        drawCircle(
            color = Color(0xFFFF1744).copy(alpha = 0.5f),
            radius = finalRadius * 0.08f,
            center = Offset(cx - finalRadius * 0.25f, headY + finalRadius * 0.1f)
        )
        drawCircle(
            color = Color(0xFFFF1744).copy(alpha = 0.5f),
            radius = finalRadius * 0.08f,
            center = Offset(cx + finalRadius * 0.25f, headY + finalRadius * 0.1f)
        )
    }

    // 7. Fire Mane / Tuft
    val manePath = Path().apply {
        moveTo(cx - finalRadius * 0.2f, headY + finalRadius * 0.35f)
        lineTo(cx, headY + finalRadius * 0.65f)
        lineTo(cx + finalRadius * 0.2f, headY + finalRadius * 0.35f)
        close()
    }
    drawPath(path = manePath, color = accentColor)
}
