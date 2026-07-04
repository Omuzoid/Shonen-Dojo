package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuraEffects(
    auraName: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "AuraAnimation")
    
    // Core animation values
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    val rotateAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura_rotate"
    )

    val lightningTrigger by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura_lightning"
    )

    Box(
        modifier = modifier.testTag("aura_effects_container"),
        contentAlignment = Alignment.Center
    ) {
        if (auraName != "None" && auraName.isNotEmpty()) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = size.width.coerceAtMost(size.height) * 0.45f

                when (auraName) {
                    "Green Lightning" -> {
                        // Drawing glowing green sparks and lightning arcs
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00E676).copy(alpha = 0.25f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r * 1.5f
                            ),
                            radius = r * 1.5f
                        )
                        
                        // Draw crackling lines based on random triggers
                        val path = Path().apply {
                            var px = cx + r * cos(rotateAngle * Math.PI / 180).toFloat()
                            var py = cy + r * sin(rotateAngle * Math.PI / 180).toFloat()
                            moveTo(px, py)
                            
                            val segments = 5
                            for (j in 1..segments) {
                                val tAngle = (rotateAngle + (j * 40)) * Math.PI / 180
                                val wiggle = if (lightningTrigger > 0.5) 15f else -15f
                                val tx = cx + (r + wiggle) * cos(tAngle).toFloat()
                                val ty = cy + (r + wiggle) * sin(tAngle).toFloat()
                                lineTo(tx, ty)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFFB9F6CA),
                            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = path,
                            color = Color(0xFF00E676).copy(alpha = 0.6f),
                            style = Stroke(width = 7f, cap = StrokeCap.Round)
                        )
                    }
                    "Red Aura" -> {
                        // Rising fiery crimson waves
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFF3D00).copy(alpha = 0.35f * pulseScale), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r * 1.6f
                            ),
                            radius = r * 1.6f
                        )
                        
                        // Flickering flame tips rising up
                        for (i in 0 until 6) {
                            val waveOffset = (lightningTrigger * 40f)
                            val fx = cx - r + (i * (r * 2 / 5))
                            val fy = cy - r + (sin(lightningTrigger * 2 * Math.PI + i).toFloat() * 12f) - waveOffset
                            
                            drawCircle(
                                color = Color(0xFFFF3D00).copy(alpha = (1f - (waveOffset / 40f)) * 0.5f),
                                radius = 12f,
                                center = Offset(fx, fy)
                            )
                        }
                    }
                    "Blue Flame" -> {
                        // Cyber magic blue flame ring
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.3f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r * 1.5f
                            ),
                            radius = r * 1.5f
                        )
                        
                        val steps = 18
                        val firePath = Path()
                        for (i in 0 until steps) {
                            val angle = (i * (360f / steps) + rotateAngle) * Math.PI / 180
                            // Wave flare
                            val flare = r + 12f * sin(rotateAngle * 0.1 + i).toFloat()
                            val px = cx + flare * cos(angle).toFloat()
                            val py = cy + flare * sin(angle).toFloat()
                            
                            if (i == 0) firePath.moveTo(px, py) else firePath.lineTo(px, py)
                        }
                        firePath.close()
                        drawPath(
                            path = firePath,
                            color = Color(0xFF00E5FF).copy(alpha = 0.4f),
                            style = Stroke(width = 4f)
                        )
                    }
                    "Shadow Smoke" -> {
                        // Dark purple tendrils
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF7C4DFF).copy(alpha = 0.4f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r * 1.7f
                            ),
                            radius = r * 1.7f
                        )
                        
                        for (i in 0 until 4) {
                            val angle = (i * 90 + rotateAngle * 0.5) * Math.PI / 180
                            val offsetR = r * (0.9f + 0.2f * pulseScale)
                            val tx = cx + offsetR * cos(angle).toFloat()
                            val ty = cy + offsetR * sin(angle).toFloat()
                            
                            drawCircle(
                                color = Color(0xFFE040FB).copy(alpha = 0.35f),
                                radius = 16f,
                                center = Offset(tx, ty)
                            )
                        }
                    }
                    "Golden Sparks" -> {
                        // Twinkling golden points
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFD600).copy(alpha = 0.2f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r * 1.4f
                            ),
                            radius = r * 1.4f
                        )
                        
                        for (i in 0 until 8) {
                            val angle = (i * 45 + rotateAngle) * Math.PI / 180
                            val px = cx + (r + 15f * sin(rotateAngle * 0.05 + i).toFloat()) * cos(angle).toFloat()
                            val py = cy + (r + 15f * sin(rotateAngle * 0.05 + i).toFloat()) * sin(angle).toFloat()
                            
                            drawCircle(
                                color = Color(0xFFFFD600),
                                radius = 4f,
                                center = Offset(px, py)
                            )
                        }
                    }
                    "Purple Mist" -> {
                        // Warm neon violet mist
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFD500F9).copy(alpha = 0.3f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r * 1.8f
                            ),
                            radius = r * 1.8f
                        )
                    }
                    "Dragon Force" -> {
                        // Concentric energy waves pulsating outwards
                        drawCircle(
                            color = Color(0xFFFF6D00).copy(alpha = (1.1f - pulseScale) * 0.4f),
                            radius = r * 1.3f * pulseScale,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3f)
                        )
                        drawCircle(
                            color = Color(0xFFFFD600).copy(alpha = (1.1f - pulseScale) * 0.2f),
                            radius = r * 1.6f * pulseScale,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1.5f)
                        )
                    }
                    "Monarch Wings" -> {
                        // Glowing wings flanking behind
                        val leftWing = Path().apply {
                            moveTo(cx - r * 0.5f, cy)
                            cubicTo(
                                cx - r * 1.5f, cy - r * 0.8f,
                                cx - r * 1.8f, cy + r * 0.2f,
                                cx - r * 0.8f, cy + r * 0.6f
                            )
                            close()
                        }
                        val rightWing = Path().apply {
                            moveTo(cx + r * 0.5f, cy)
                            cubicTo(
                                cx + r * 1.5f, cy - r * 0.8f,
                                cx + r * 1.8f, cy + r * 0.2f,
                                cx + r * 0.8f, cy + r * 0.6f
                            )
                            close()
                        }
                        drawPath(path = leftWing, color = Color(0xFF00E5FF).copy(alpha = 0.35f * pulseScale))
                        drawPath(path = rightWing, color = Color(0xFF00E5FF).copy(alpha = 0.35f * pulseScale))
                    }
                }
            }
        }
        content()
    }
}
