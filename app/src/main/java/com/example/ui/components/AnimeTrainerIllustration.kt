package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ComicBorder
import com.example.ui.theme.HologramCyan
import com.example.ui.theme.HologramPurple
import kotlin.math.sin

@Composable
fun AnimeTrainerIllustration(
    name: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "TrainerAnimation")
    
    // Animation states for auras and sparks
    val pulseEffect by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    val energyOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "energy_particles"
    )

    Canvas(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(2.5.dp, ComicBorder, RoundedCornerShape(12.dp))
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        when (name.uppercase()) {
            "SUNG JIN-WOO" -> {
                // SUNG JIN-WOO: Shadow Monarch Dark Theme
                // 1. Dark purple/blue energy background
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E152A), Color(0xFF09040F)),
                        center = Offset(cx, cy),
                        radius = w * 0.8f
                    )
                )

                // 2. Monarch Purple Shadow Soldiers Aura
                drawCircle(
                    color = Color(0xFF7C3AED).copy(alpha = 0.35f * pulseEffect),
                    radius = w * 0.45f,
                    center = Offset(cx, cy - 10f)
                )

                // 3. Draw hood/collar curves
                val hoodPath = Path().apply {
                    moveTo(cx - w * 0.45f, h)
                    quadraticTo(cx - w * 0.25f, cy + h * 0.1f, cx, cy + h * 0.15f)
                    quadraticTo(cx + w * 0.25f, cy + h * 0.1f, cx + w * 0.45f, h)
                    lineTo(cx + w * 0.45f, h + 50f)
                    lineTo(cx - w * 0.45f, h + 50f)
                    close()
                }
                drawPath(path = hoodPath, color = Color(0xFF1E1B29))
                drawPath(path = hoodPath, color = ComicBorder, style = Stroke(width = 2.5.dp.toPx()))

                // 4. Sharp dark spiky hair
                val hairPath = Path().apply {
                    moveTo(cx - w * 0.3f, cy - h * 0.15f)
                    lineTo(cx - w * 0.12f, cy - h * 0.38f) // spike 1
                    lineTo(cx - w * 0.05f, cy - h * 0.2f)
                    lineTo(cx + w * 0.05f, cy - h * 0.42f) // central high spike
                    lineTo(cx + w * 0.12f, cy - h * 0.22f)
                    lineTo(cx + w * 0.28f, cy - h * 0.36f) // spike 2
                    lineTo(cx + w * 0.18f, cy - h * 0.1f)
                    lineTo(cx + w * 0.32f, cy + h * 0.08f) // cheek spill hair
                    lineTo(cx + w * 0.15f, cy + h * 0.05f)
                    lineTo(cx - w * 0.12f, cy + h * 0.05f)
                    lineTo(cx - w * 0.32f, cy + h * 0.08f)
                    close()
                }
                drawPath(path = hairPath, color = Color(0xFF111116))
                drawPath(path = hairPath, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))

                // 5. Glowing monarch cyan/blue eyes
                val leftEye = Offset(cx - w * 0.12f, cy - h * 0.04f)
                val rightEye = Offset(cx + w * 0.12f, cy - h * 0.04f)
                
                // Outer eye glow
                drawCircle(color = HologramCyan.copy(alpha = 0.6f * pulseEffect), radius = 10.dp.toPx(), center = leftEye)
                drawCircle(color = HologramCyan.copy(alpha = 0.6f * pulseEffect), radius = 10.dp.toPx(), center = rightEye)
                
                // Eye centers
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = leftEye)
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = rightEye)

                // Neon slash arcs (energy wisps from eyes)
                val leftEyeSlash = Path().apply {
                    moveTo(leftEye.x, leftEye.y)
                    quadraticTo(leftEye.x - w * 0.15f, leftEye.y - h * 0.1f, leftEye.x - w * 0.28f, leftEye.y - h * 0.04f)
                }
                val rightEyeSlash = Path().apply {
                    moveTo(rightEye.x, rightEye.y)
                    quadraticTo(rightEye.x + w * 0.15f, rightEye.y - h * 0.1f, rightEye.x + w * 0.28f, rightEye.y - h * 0.04f)
                }
                drawPath(path = leftEyeSlash, color = HologramCyan, style = Stroke(width = 2.dp.toPx()))
                drawPath(path = rightEyeSlash, color = HologramCyan, style = Stroke(width = 2.dp.toPx()))
            }
            "GOKU" -> {
                // GOKU: Super Saiyan Golden Theme
                // 1. Fiery bright orange gradient background
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF5221), Color(0xFFFF9100)),
                        startY = 0f,
                        endY = h
                    )
                )

                // 2. Yellow Ki Aura pulsing
                drawCircle(
                    color = Color(0xFFFFEA3B).copy(alpha = 0.4f * pulseEffect),
                    radius = w * 0.5f,
                    center = Offset(cx, cy)
                )

                // 3. Spiky golden Super Saiyan hair (massive triangular paths)
                val hairPath = Path().apply {
                    moveTo(cx - w * 0.42f, cy - h * 0.05f)
                    lineTo(cx - w * 0.38f, cy - h * 0.42f) // left massive spike
                    lineTo(cx - w * 0.15f, cy - h * 0.25f)
                    lineTo(cx - w * 0.18f, cy - h * 0.55f) // top left spike
                    lineTo(cx, cy - h * 0.32f)
                    lineTo(cx + w * 0.18f, cy - h * 0.55f) // top right spike
                    lineTo(cx + w * 0.15f, cy - h * 0.25f)
                    lineTo(cx + w * 0.38f, cy - h * 0.42f) // right massive spike
                    lineTo(cx + w * 0.42f, cy - h * 0.05f)
                    lineTo(cx + w * 0.15f, cy + h * 0.1f)
                    lineTo(cx - w * 0.15f, cy + h * 0.1f)
                    close()
                }
                drawPath(path = hairPath, color = Color(0xFFFFD54F)) // Golden Super Saiyan Yellow
                drawPath(path = hairPath, color = ComicBorder, style = Stroke(width = 2.5.dp.toPx()))

                // 4. Iconic Blue/Orange Gi neck wrap
                val giPath = Path().apply {
                    moveTo(cx - w * 0.35f, h)
                    lineTo(cx - w * 0.2f, cy + h * 0.15f)
                    lineTo(cx, cy + h * 0.3f) // V neck bottom
                    lineTo(cx + w * 0.2f, cy + h * 0.15f)
                    lineTo(cx + w * 0.35f, h)
                    close()
                }
                drawPath(path = giPath, color = Color(0xFFFF5722)) // Orange GI
                drawPath(path = giPath, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))

                val innerGiPath = Path().apply {
                    moveTo(cx - w * 0.15f, cy + h * 0.22f)
                    lineTo(cx, cy + h * 0.3f)
                    lineTo(cx + w * 0.15f, cy + h * 0.22f)
                    lineTo(cx, h)
                    close()
                }
                drawPath(path = innerGiPath, color = Color(0xFF0D47A1)) // Blue inner shirt

                // 5. Sharp determined eyes (teal blue)
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(cx - w * 0.1f, cy - h * 0.02f))
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(cx + w * 0.1f, cy - h * 0.02f))
                drawCircle(color = Color(0xFF00E5FF), radius = 2.dp.toPx(), center = Offset(cx - w * 0.1f, cy - h * 0.02f))
                drawCircle(color = Color(0xFF00E5FF), radius = 2.dp.toPx(), center = Offset(cx + w * 0.1f, cy - h * 0.02f))
            }
            "SAITAMA" -> {
                // SAITAMA: Yellow suit and bald head
                // 1. Warm cream background card
                drawRect(color = Color(0xFFFFF9C4))

                // 2. Comic serious focus circles
                drawCircle(
                    color = Color(0xFFFFF176).copy(alpha = 0.4f * pulseEffect),
                    radius = w * 0.45f,
                    center = Offset(cx, cy)
                )

                // 3. Bald Head (A perfect bold circular jawline)
                val headCenter = Offset(cx, cy - h * 0.08f)
                val headR = w * 0.35f
                drawCircle(color = Color(0xFFFEEBDE), radius = headR, center = headCenter) // skin tone
                drawCircle(color = ComicBorder, radius = headR, center = headCenter, style = Stroke(width = 3.dp.toPx()))

                // 4. Serious intense eyes (Saitama serious punch mode)
                val leftEyeStart = Offset(cx - w * 0.18f, cy - h * 0.12f)
                val leftEyeEnd = Offset(cx - w * 0.04f, cy - h * 0.08f)
                val rightEyeStart = Offset(cx + w * 0.04f, cy - h * 0.08f)
                val rightEyeEnd = Offset(cx + w * 0.18f, cy - h * 0.12f)

                // Left Eye Slit
                val leftEyePath = Path().apply {
                    moveTo(leftEyeStart.x, leftEyeStart.y)
                    quadraticTo(cx - w * 0.11f, cy - h * 0.14f, leftEyeEnd.x, leftEyeEnd.y)
                    quadraticTo(cx - w * 0.11f, cy - h * 0.06f, leftEyeStart.x, leftEyeStart.y)
                }
                drawPath(path = leftEyePath, color = Color.White)
                drawPath(path = leftEyePath, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = Color.Black, radius = 3.5.dp.toPx(), center = Offset(cx - w * 0.11f, cy - h * 0.1f))

                // Right Eye Slit
                val rightEyePath = Path().apply {
                    moveTo(rightEyeStart.x, rightEyeStart.y)
                    quadraticTo(cx + w * 0.11f, cy - h * 0.14f, rightEyeEnd.x, rightEyeEnd.y)
                    quadraticTo(cx + w * 0.11f, cy - h * 0.06f, rightEyeStart.x, rightEyeStart.y)
                }
                drawPath(path = rightEyePath, color = Color.White)
                drawPath(path = rightEyePath, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = Color.Black, radius = 3.5.dp.toPx(), center = Offset(cx + w * 0.11f, cy - h * 0.1f))

                // Serious mouth line
                drawLine(
                    color = ComicBorder,
                    start = Offset(cx - w * 0.08f, cy + h * 0.08f),
                    end = Offset(cx + w * 0.08f, cy + h * 0.08f),
                    strokeWidth = 2.5.dp.toPx()
                )

                // 5. Red hero collar cape detail
                val capeCollar = Path().apply {
                    moveTo(cx - w * 0.4f, h)
                    quadraticTo(cx - w * 0.2f, cy + h * 0.18f, cx, cy + h * 0.22f)
                    quadraticTo(cx + w * 0.2f, cy + h * 0.18f, cx + w * 0.4f, h)
                    close()
                }
                drawPath(path = capeCollar, color = Color(0xFFEF5350)) // Red Cape
                drawPath(path = capeCollar, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))

                // Large white zip disk
                drawCircle(color = Color.White, radius = 8.dp.toPx(), center = Offset(cx, cy + h * 0.22f))
                drawCircle(color = ComicBorder, radius = 8.dp.toPx(), center = Offset(cx, cy + h * 0.22f), style = Stroke(width = 1.5.dp.toPx()))
            }
            "ALL MIGHT" -> {
                // ALL MIGHT: Intense heavy line shading and antennae hair
                // 1. Red background card with epic yellow action burst
                drawRect(color = Color(0xFF1E3A8A)) // Blue background

                // Golden burst
                val burstPath = Path().apply {
                    moveTo(cx, cy)
                    lineTo(0f, 0f)
                    lineTo(cx * 0.5f, 0f)
                    lineTo(cx, cy)
                    lineTo(w, 0f)
                    lineTo(w, cy * 0.5f)
                    lineTo(cx, cy)
                    lineTo(w, h)
                    lineTo(cx * 1.5f, h)
                    lineTo(cx, cy)
                    lineTo(0f, h)
                    lineTo(0f, cy * 1.5f)
                    close()
                }
                drawPath(path = burstPath, color = Color(0xFFFBBF24))

                // 2. Twin massive antennae gold hair tufts
                val hairLeft = Path().apply {
                    moveTo(cx - w * 0.12f, cy - h * 0.12f)
                    quadraticTo(cx - w * 0.45f, cy - h * 0.55f, cx - w * 0.35f, cy - h * 0.65f)
                    quadraticTo(cx - w * 0.18f, cy - h * 0.45f, cx - w * 0.04f, cy - h * 0.15f)
                    close()
                }
                val hairRight = Path().apply {
                    moveTo(cx + w * 0.12f, cy - h * 0.12f)
                    quadraticTo(cx + w * 0.45f, cy - h * 0.55f, cx + w * 0.35f, cy - h * 0.65f)
                    quadraticTo(cx + w * 0.18f, cy - h * 0.45f, cx + w * 0.04f, cy - h * 0.15f)
                    close()
                }
                drawPath(path = hairLeft, color = Color(0xFFF59E0B))
                drawPath(path = hairLeft, color = ComicBorder, style = Stroke(width = 2.5.dp.toPx()))
                drawPath(path = hairRight, color = Color(0xFFF59E0B))
                drawPath(path = hairRight, color = ComicBorder, style = Stroke(width = 2.5.dp.toPx()))

                // 3. Massive muscular neck & chest
                val suitCollar = Path().apply {
                    moveTo(cx - w * 0.35f, h)
                    lineTo(cx - w * 0.25f, cy + h * 0.15f)
                    lineTo(cx + w * 0.25f, cy + h * 0.15f)
                    lineTo(cx + w * 0.35f, h)
                    close()
                }
                drawPath(path = suitCollar, color = Color(0xFFDC2626)) // Red and White suit
                drawPath(path = suitCollar, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))

                // 4. Iconic dramatic black shade eyes (his face is mostly dark in shadows)
                val shadowFacePath = Path().apply {
                    moveTo(cx - w * 0.22f, cy - h * 0.15f)
                    lineTo(cx + w * 0.22f, cy - h * 0.15f)
                    lineTo(cx + w * 0.18f, cy + h * 0.12f)
                    lineTo(cx - w * 0.18f, cy + h * 0.12f)
                    close()
                }
                drawPath(path = shadowFacePath, color = Color(0xFF1E293B)) // Deep dark shadow face mask
                drawPath(path = shadowFacePath, color = ComicBorder, style = Stroke(width = 2.5.dp.toPx()))

                // Twin small glowing blue eyes in the dark shadow mask
                drawCircle(color = HologramCyan, radius = 3.5.dp.toPx(), center = Offset(cx - w * 0.09f, cy - h * 0.02f))
                drawCircle(color = HologramCyan, radius = 3.5.dp.toPx(), center = Offset(cx + w * 0.09f, cy - h * 0.02f))

                // 5. Giant smile
                val smilePath = Path().apply {
                    moveTo(cx - w * 0.12f, cy + h * 0.04f)
                    quadraticTo(cx, cy + h * 0.13f, cx + w * 0.12f, cy + h * 0.04f)
                    quadraticTo(cx, cy + h * 0.02f, cx - w * 0.12f, cy + h * 0.04f)
                    close()
                }
                drawPath(path = smilePath, color = Color.White)
                drawPath(path = smilePath, color = ComicBorder, style = Stroke(width = 1.5.dp.toPx()))
            }
            else -> {
                // ASH KETCHUM (Default/Other): Cap & Lightning cheeks
                // 1. Bright sky blue background
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                        startY = 0f,
                        endY = h
                    )
                )

                // 2. Multi-angled spiky black hair
                val hairPath = Path().apply {
                    moveTo(cx - w * 0.35f, cy)
                    lineTo(cx - w * 0.38f, cy - h * 0.25f)
                    lineTo(cx - w * 0.2f, cy - h * 0.15f)
                    lineTo(cx - w * 0.15f, cy - h * 0.32f)
                    lineTo(cx, cy - h * 0.2f)
                    lineTo(cx + w * 0.15f, cy - h * 0.32f)
                    lineTo(cx + w * 0.2f, cy - h * 0.15f)
                    lineTo(cx + w * 0.38f, cy - h * 0.25f)
                    lineTo(cx + w * 0.35f, cy)
                    close()
                }
                drawPath(path = hairPath, color = Color(0xFF27272A))
                drawPath(path = hairPath, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))

                // 3. Trainer Red & White Cap (overlapping hair)
                val capPath = Path().apply {
                    moveTo(cx - w * 0.35f, cy - h * 0.1f)
                    quadraticTo(cx - w * 0.32f, cy - h * 0.45f, cx, cy - h * 0.48f)
                    quadraticTo(cx + w * 0.32f, cy - h * 0.45f, cx + w * 0.35f, cy - h * 0.1f)
                    close()
                }
                drawPath(path = capPath, color = Color.White)
                drawPath(path = capPath, color = ComicBorder, style = Stroke(width = 2.5.dp.toPx()))

                // Red front panel of the cap
                val capRedFront = Path().apply {
                    moveTo(cx - w * 0.35f, cy - h * 0.15f)
                    quadraticTo(cx - w * 0.32f, cy - h * 0.45f, cx, cy - h * 0.48f)
                    quadraticTo(cx + w * 0.1f, cy - h * 0.45f, cx + w * 0.18f, cy - h * 0.25f)
                    quadraticTo(cx - w * 0.1f, cy - h * 0.25f, cx - w * 0.35f, cy - h * 0.15f)
                    close()
                }
                drawPath(path = capRedFront, color = Color(0xFFEF4444))

                // Green leaf symbol on cap (C emblem)
                drawCircle(color = Color(0xFF22C55E), radius = 6.dp.toPx(), center = Offset(cx - w * 0.08f, cy - h * 0.33f))

                // Cap brim (visor pointing to right side)
                val brimPath = Path().apply {
                    moveTo(cx - w * 0.32f, cy - h * 0.12f)
                    lineTo(cx + w * 0.35f, cy - h * 0.12f)
                    quadraticTo(cx + w * 0.45f, cy - h * 0.05f, cx + w * 0.42f, cy + h * 0.04f)
                    quadraticTo(cx + w * 0.1f, cy - h * 0.05f, cx - w * 0.32f, cy - h * 0.12f)
                    close()
                }
                drawPath(path = brimPath, color = Color(0xFFEF4444))
                drawPath(path = brimPath, color = ComicBorder, style = Stroke(width = 2.dp.toPx()))

                // 4. Face cheeks with zig-zag lightning decals
                drawCircle(color = Color(0xFFFCA5A5).copy(alpha = 0.5f), radius = 8.dp.toPx(), center = Offset(cx - w * 0.16f, cy + h * 0.1f))
                drawCircle(color = Color(0xFFFCA5A5).copy(alpha = 0.5f), radius = 8.dp.toPx(), center = Offset(cx + w * 0.16f, cy + h * 0.1f))

                // Left zig-zag cheek mark
                drawLine(color = ComicBorder, start = Offset(cx - w * 0.18f, cy + h * 0.06f), end = Offset(cx - w * 0.14f, cy + h * 0.09f), strokeWidth = 1.5.dp.toPx())
                drawLine(color = ComicBorder, start = Offset(cx - w * 0.14f, cy + h * 0.09f), end = Offset(cx - w * 0.16f, cy + h * 0.12f), strokeWidth = 1.5.dp.toPx())

                // Right zig-zag cheek mark
                drawLine(color = ComicBorder, start = Offset(cx + w * 0.14f, cy + h * 0.06f), end = Offset(cx + w * 0.18f, cy + h * 0.09f), strokeWidth = 1.5.dp.toPx())
                drawLine(color = ComicBorder, start = Offset(cx + w * 0.18f, cy + h * 0.09f), end = Offset(cx + w * 0.16f, cy + h * 0.12f), strokeWidth = 1.5.dp.toPx())

                // 5. Huge laughing mouth
                val mouthPath = Path().apply {
                    moveTo(cx - w * 0.08f, cy + h * 0.08f)
                    quadraticTo(cx, cy + h * 0.18f, cx + w * 0.08f, cy + h * 0.08f)
                    close()
                }
                drawPath(path = mouthPath, color = Color(0xFFF43F5E)) // Pink mouth
                drawPath(path = mouthPath, color = ComicBorder, style = Stroke(width = 1.5.dp.toPx()))
            }
        }
    }
}
