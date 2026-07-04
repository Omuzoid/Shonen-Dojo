package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnatomyCoach(
    exerciseName: String,
    modifier: Modifier = Modifier
) {
    // Spin angles (user can drag to rotate the muscular 3D dummy!)
    var rotationY by remember { mutableStateOf(0.4f) }
    var rotationX by remember { mutableStateOf(0.1f) }

    // Loop animation for movement
    val transition = rememberInfiniteTransition(label = "AnatomyAnimation")
    val animationProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dummy_movement"
    )

    // Hologram warning pulsing/glow
    val hologramGlow by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hologram_pulse"
    )

    // Scanline vertical sweep animation
    val scanlineY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline_sweep"
    )

    // Interactive shockwave click ripple
    var rippleTrigger by remember { mutableStateOf(0) }
    val rippleScale by animateFloatAsState(
        targetValue = if (rippleTrigger > 0) 1.5f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "ripple_scale",
        finishedListener = { rippleTrigger = 0 }
    )

    Box(
        modifier = modifier
            .testTag("anatomy_coach_container")
            .background(CosmicCardNight, RoundedCornerShape(16.dp))
            .border(2.dp, ComicBorder, RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        rotationY += dragAmount.x * 0.006f
                        rotationX -= dragAmount.y * 0.006f
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    rippleTrigger++
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Solo Leveling Style Holographic Grid and Rings
            drawHologramSystem(width, height, scanlineY, hologramGlow)

            // 2. Compute 3D Joints for Muscular Anatomy Coach
            val joints = getAnimatedJoints(exerciseName, animationProgress)

            // 3. Project 3D to 2D Screen Space
            val projectedPoints = mutableMapOf<String, Offset>()
            for ((jointName, pos) in joints) {
                projectedPoints[jointName] = project3D(
                    x = pos[0], y = pos[1], z = pos[2],
                    angleY = rotationY, angleX = rotationX,
                    width = width, height = height
                )
            }

            // 4. Draw Muscular Humanoid 3D Dummy with outlines
            drawMuscularDummy(projectedPoints, exerciseName, hologramGlow)

            // 5. Interactive touch feedback shockwave ring
            if (rippleScale > 0f) {
                drawCircle(
                    color = HologramCyan.copy(alpha = (1f - rippleScale / 1.5f).coerceIn(0f, 1f)),
                    radius = (width * 0.4f) * rippleScale,
                    center = Offset(width / 2f, height / 2.2f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }

        // Floating Target Legend - Shonen Style
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
                .background(CosmicCardNight.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.5.dp, ComicBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Text(
                text = "SYSTEM ACTIVE QUEST",
                color = HologramPurple,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "STIMULATED GROUPS",
                color = ComicBorder,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            getTargetMuscles(exerciseName).forEach { muscle ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AccentCrimson, RoundedCornerShape(2.dp))
                            .border(1.dp, ComicBorder, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = muscle.uppercase(),
                        color = ComicBorder,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Solo Leveling Floating Status Screen HUD overlays
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        ) {
            Text(
                text = "SOLO HUNTER: RIG ACTIVE",
                color = HologramCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "ROTATION AXIS: 3D LOCK",
                color = ComicBorder.copy(alpha = 0.7f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(HologramCyan, RoundedCornerShape(4.dp))
                .border(1.5.dp, ComicBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "LEVELING-DUMMY v2.0",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

// 3D Perspective Projection formula
private fun project3D(
    x: Float, y: Float, z: Float,
    angleY: Float, angleX: Float,
    width: Float, height: Float
): Offset {
    val cosY = cos(angleY)
    val sinY = sin(angleY)
    val x1 = x * cosY - z * sinY
    val z1 = x * sinY + z * cosY

    val cosX = cos(angleX)
    val sinX = sin(angleX)
    val y2 = y * cosX - z1 * sinX
    val z2 = y * sinX + z1 * cosX

    val fovDistance = 3.8f
    val scaleFactor = (fovDistance / (fovDistance + z2)) * (width * 0.38f)

    val px = width / 2f + x1 * scaleFactor
    val py = height / 2.1f - y2 * scaleFactor

    return Offset(px, py)
}

// 3D Joint offset physics mapping
private fun getAnimatedJoints(exerciseName: String, progress: Float): Map<String, FloatArray> {
    val map = mutableMapOf<String, FloatArray>()
    val t = progress * 2f * Math.PI.toFloat() // 2pi loop

    var headY = 1.55f
    var neckY = 1.4f
    var spineY = 1.15f
    var pelvisY = 0.9f
    
    var leftShoulder = floatArrayOf(-0.35f, 1.35f, 0f)
    var rightShoulder = floatArrayOf(0.35f, 1.35f, 0f)
    var leftElbow = floatArrayOf(-0.48f, 1.05f, 0f)
    var rightElbow = floatArrayOf(0.48f, 1.05f, 0f)
    var leftWrist = floatArrayOf(-0.55f, 0.78f, 0.1f)
    var rightWrist = floatArrayOf(0.55f, 0.78f, 0.1f)

    var leftHip = floatArrayOf(-0.18f, 0.85f, 0f)
    var rightHip = floatArrayOf(0.18f, 0.85f, 0f)
    var leftKnee = floatArrayOf(-0.18f, 0.45f, 0f)
    var rightKnee = floatArrayOf(0.18f, 0.45f, 0f)
    var leftAnkle = floatArrayOf(-0.18f, 0.05f, 0f)
    var rightAnkle = floatArrayOf(0.18f, 0.05f, 0f)

    when (exerciseName) {
        "Push-Ups" -> {
            val cycle = sin(t) * 0.5f + 0.5f
            val pushUpHeight = 0.2f + cycle * 0.28f
            val shoulderZ = pushUpHeight
            val pelvisZ = pushUpHeight * 0.8f
            val kneeZ = pushUpHeight * 0.35f

            headY = 0.75f
            neckY = 0.65f
            spineY = 0.25f
            pelvisY = -0.15f

            leftShoulder = floatArrayOf(-0.35f, 0.6f, shoulderZ)
            rightShoulder = floatArrayOf(0.35f, 0.6f, shoulderZ)
            leftWrist = floatArrayOf(-0.45f, 0.5f, 0.05f)
            rightWrist = floatArrayOf(0.45f, 0.5f, 0.05f)

            val elbowZ = shoulderZ * 0.45f - 0.1f * (1f - cycle)
            leftElbow = floatArrayOf(-0.55f, 0.5f, elbowZ)
            rightElbow = floatArrayOf(0.55f, 0.5f, elbowZ)

            leftHip = floatArrayOf(-0.18f, -0.15f, pelvisZ)
            rightHip = floatArrayOf(0.18f, -0.15f, pelvisZ)
            leftKnee = floatArrayOf(-0.18f, -0.5f, kneeZ)
            rightKnee = floatArrayOf(0.18f, -0.5f, kneeZ)
            leftAnkle = floatArrayOf(-0.18f, -0.85f, 0.05f)
            rightAnkle = floatArrayOf(0.18f, -0.85f, 0.05f)
        }
        "Squats" -> {
            val cycle = sin(t) * 0.5f + 0.5f
            val squatOffset = cycle * 0.48f

            headY = 1.55f - squatOffset
            neckY = 1.4f - squatOffset
            spineY = 1.15f - squatOffset * 0.9f
            pelvisY = 0.85f - squatOffset * 0.8f

            leftShoulder = floatArrayOf(-0.35f, 1.35f - squatOffset, 0.1f)
            rightShoulder = floatArrayOf(0.35f, 1.35f - squatOffset, 0.1f)
            leftWrist = floatArrayOf(-0.35f, 1.35f - squatOffset, 0.55f)
            rightWrist = floatArrayOf(0.35f, 1.35f - squatOffset, 0.55f)
            leftElbow = floatArrayOf(-0.38f, 1.35f - squatOffset, 0.28f)
            rightElbow = floatArrayOf(0.38f, 1.35f - squatOffset, 0.28f)

            val hipZ = cycle * 0.28f
            leftHip = floatArrayOf(-0.18f, 0.85f - squatOffset * 0.8f, hipZ)
            rightHip = floatArrayOf(0.18f, 0.85f - squatOffset * 0.8f, hipZ)

            val kneeYDepth = 0.45f - squatOffset * 0.35f
            val kneeZDepth = -cycle * 0.18f
            leftKnee = floatArrayOf(-0.2f, kneeYDepth, kneeZDepth)
            rightKnee = floatArrayOf(0.2f, kneeYDepth, kneeZDepth)

            leftAnkle = floatArrayOf(-0.18f, 0.05f, 0f)
            rightAnkle = floatArrayOf(0.18f, 0.05f, 0f)
        }
        "Sit-Ups" -> {
            val cycle = sin(t) * 0.5f + 0.5f
            val angle = cycle * (Math.PI / 2.3).toFloat()

            headY = 0.25f + sin(angle) * 0.72f
            val headZ = -cos(angle) * 0.72f

            neckY = 0.25f + sin(angle) * 0.58f
            val neckZ = -cos(angle) * 0.58f

            spineY = 0.25f + sin(angle) * 0.28f
            val spineZ = -cos(angle) * 0.28f

            pelvisY = 0.25f
            val pelvisZ = 0f

            leftShoulder = floatArrayOf(-0.3f, neckY, neckZ)
            rightShoulder = floatArrayOf(0.3f, neckY, neckZ)
            leftWrist = floatArrayOf(-0.12f, headY, headZ - 0.12f)
            rightWrist = floatArrayOf(0.12f, headY, headZ - 0.12f)
            leftElbow = floatArrayOf(-0.45f, headY, headZ)
            rightElbow = floatArrayOf(0.45f, headY, headZ)

            leftHip = floatArrayOf(-0.18f, pelvisY, pelvisZ)
            rightHip = floatArrayOf(0.18f, pelvisY, pelvisZ)
            leftKnee = floatArrayOf(-0.22f, 0.35f, 0.42f)
            rightKnee = floatArrayOf(0.22f, 0.35f, 0.42f)
            leftAnkle = floatArrayOf(-0.2f, 0.05f, 0.58f)
            rightAnkle = floatArrayOf(0.2f, 0.05f, 0.58f)
        }
        "Plank" -> {
            val microVibe = sin(t * 6f) * 0.006f
            val plankHeight = 0.24f + microVibe

            headY = 0.78f
            neckY = 0.68f
            spineY = 0.28f
            pelvisY = -0.12f

            leftShoulder = floatArrayOf(-0.32f, 0.62f, plankHeight)
            rightShoulder = floatArrayOf(0.32f, 0.62f, plankHeight)
            leftElbow = floatArrayOf(-0.35f, 0.52f, 0.05f)
            rightElbow = floatArrayOf(0.35f, 0.52f, 0.05f)
            leftWrist = floatArrayOf(-0.25f, 0.62f, 0.05f)
            rightWrist = floatArrayOf(0.25f, 0.62f, 0.05f)

            leftHip = floatArrayOf(-0.18f, -0.12f, plankHeight - 0.03f)
            rightHip = floatArrayOf(0.18f, -0.12f, plankHeight - 0.03f)
            leftKnee = floatArrayOf(-0.18f, -0.48f, plankHeight - 0.05f)
            rightKnee = floatArrayOf(0.18f, -0.48f, plankHeight - 0.05f)
            leftAnkle = floatArrayOf(-0.18f, -0.82f, 0.05f)
            rightAnkle = floatArrayOf(0.18f, -0.82f, 0.05f)
        }
        else -> { // Running / Cardio
            val cycle = sin(t)
            val absCycle = cos(t)

            headY = 1.55f + absCycle * 0.05f
            neckY = 1.4f + absCycle * 0.05f
            spineY = 1.15f + absCycle * 0.02f
            pelvisY = 0.85f

            val leftArmSwing = cycle * 0.42f
            val rightArmSwing = -cycle * 0.42f

            leftShoulder = floatArrayOf(-0.35f, 1.32f, 0f)
            rightShoulder = floatArrayOf(0.35f, 1.32f, 0f)
            leftElbow = floatArrayOf(-0.45f, 1.05f + leftArmSwing * 0.22f, leftArmSwing)
            rightElbow = floatArrayOf(0.45f, 1.05f + rightArmSwing * 0.22f, rightArmSwing)
            leftWrist = floatArrayOf(-0.4f, 0.85f + leftArmSwing * 0.45f, leftArmSwing + 0.22f)
            rightWrist = floatArrayOf(0.4f, 0.85f + rightArmSwing * 0.45f, rightArmSwing + 0.22f)

            val leftLegSwing = cycle * 0.52f
            val rightLegSwing = -cycle * 0.52f

            leftHip = floatArrayOf(-0.18f, 0.82f, 0f)
            rightHip = floatArrayOf(0.18f, 0.82f, 0f)

            val leftKneeY = 0.48f + leftLegSwing * 0.22f
            val leftKneeZ = leftLegSwing * 0.42f
            val rightKneeY = 0.48f + rightLegSwing * 0.22f
            val rightKneeZ = rightLegSwing * 0.42f

            leftKnee = floatArrayOf(-0.18f, leftKneeY, leftKneeZ)
            rightKnee = floatArrayOf(0.18f, rightKneeY, rightKneeZ)
            leftAnkle = floatArrayOf(-0.18f, 0.1f + leftLegSwing * 0.32f, leftLegSwing * 0.52f - 0.1f)
            rightAnkle = floatArrayOf(0.18f, 0.1f + rightLegSwing * 0.32f, rightLegSwing * 0.52f - 0.1f)
        }
    }

    map["HEAD"] = floatArrayOf(0f, headY, 0f)
    map["NECK"] = floatArrayOf(0f, neckY, 0f)
    map["SPINE"] = floatArrayOf(0f, spineY, 0f)
    map["PELVIS"] = floatArrayOf(0f, pelvisY, 0f)
    
    map["L_SHOULDER"] = leftShoulder
    map["R_SHOULDER"] = rightShoulder
    map["L_ELBOW"] = leftElbow
    map["R_ELBOW"] = rightElbow
    map["L_WRIST"] = leftWrist
    map["R_WRIST"] = rightWrist

    map["L_HIP"] = leftHip
    map["R_HIP"] = rightHip
    map["L_KNEE"] = leftKnee
    map["R_KNEE"] = rightKnee
    map["L_ANKLE"] = leftAnkle
    map["R_ANKLE"] = rightAnkle

    return map
}

// Draw Solo Leveling Technical HUD hologram system
private fun DrawScope.drawHologramSystem(
    width: Float,
    height: Float,
    scanline: Float,
    glow: Float
) {
    val gridColor = HologramCyan.copy(alpha = 0.12f)
    val numLines = 16
    val gapX = width / numLines
    val gapY = height / numLines

    // 1. Cyber grid
    for (i in 0..numLines) {
        val x = i * gapX
        drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, height), strokeWidth = 1f)
    }
    for (i in 0..numLines) {
        val y = i * gapY
        drawLine(color = gridColor, start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1f)
    }

    // 2. Solo Leveling Central Tech Sight Reticle Rings
    val center = Offset(width / 2f, height / 2.1f)
    drawCircle(
        color = HologramCyan.copy(alpha = 0.08f),
        radius = width * 0.35f,
        center = center,
        style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
    )
    drawCircle(
        color = HologramPurple.copy(alpha = 0.05f + 0.05f * glow),
        radius = width * 0.22f,
        center = center,
        style = Stroke(width = 1f)
    )

    // 3. Floating Spark Particles (Holographic Digital Dust)
    val seedOffsets = listOf(
        Offset(0.15f, 0.3f), Offset(0.8f, 0.25f), Offset(0.28f, 0.7f),
        Offset(0.72f, 0.65f), Offset(0.48f, 0.15f), Offset(0.85f, 0.8f)
    )
    seedOffsets.forEach { base ->
        val px = width * base.x
        // Floating upwards
        val py = (height * base.y - (scanline * 80.dp.toPx())) % height
        val sizeVal = 3.dp.toPx()
        drawRect(
            color = HologramCyan.copy(alpha = (0.3f + 0.3f * glow)),
            topLeft = Offset(px, py),
            size = Size(sizeVal, sizeVal)
        )
    }

    // 4. Sweep scanline (Solo Leveling blue holographic laser sweep)
    val laserY = height * scanline
    drawLine(
        color = HologramCyan.copy(alpha = 0.35f),
        start = Offset(0f, laserY),
        end = Offset(width, laserY),
        strokeWidth = 2.5.dp.toPx()
    )
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(HologramCyan.copy(alpha = 0.1f), Color.Transparent),
            startY = laserY,
            endY = (laserY + 30.dp.toPx()).coerceAtMost(height)
        ),
        topLeft = Offset(0f, laserY),
        size = Size(width, 30.dp.toPx())
    )
}

// Redraws the bones into a proper muscular humanoid body parts
private fun DrawScope.drawMuscularDummy(
    pts: Map<String, Offset>,
    exerciseName: String,
    glow: Float
) {
    val tPush = (exerciseName == "Push-Ups")
    val tSquat = (exerciseName == "Squats")
    val tSitUp = (exerciseName == "Sit-Ups")
    val tPlank = (exerciseName == "Plank")
    val tRun = (exerciseName == "Running")

    val baseMuscleColor = HologramCyan.copy(alpha = 0.25f)
    val lineStrokeColor = ComicBorder

    // Retrieve projected points
    val head = pts["HEAD"] ?: return
    val neck = pts["NECK"] ?: return
    val spine = pts["SPINE"] ?: return
    val pelvis = pts["PELVIS"] ?: return

    val lShoulder = pts["L_SHOULDER"] ?: return
    val rShoulder = pts["R_SHOULDER"] ?: return
    val lElbow = pts["L_ELBOW"] ?: return
    val rElbow = pts["R_ELBOW"] ?: return
    val lWrist = pts["L_WRIST"] ?: return
    val rWrist = pts["R_WRIST"] ?: return

    val lHip = pts["L_HIP"] ?: return
    val rHip = pts["R_HIP"] ?: return
    val lKnee = pts["L_KNEE"] ?: return
    val rKnee = pts["R_KNEE"] ?: return
    val lAnkle = pts["L_ANKLE"] ?: return
    val rAnkle = pts["R_ANKLE"] ?: return

    // 1. DRAW ARMS & LEGS (LIMBS DRAWN FIRST SO TRUNK OVERLAYS THEM PROPERLY)
    
    // Left Arm (Upper: Shoulder->Elbow, Lower: Elbow->Wrist)
    drawMuscularConnection(lShoulder, lElbow, 14.dp.toPx(), baseMuscleColor, lineStrokeColor, tPush || tPlank, glow)
    drawMuscularConnection(lElbow, lWrist, 10.dp.toPx(), baseMuscleColor, lineStrokeColor, false, glow)

    // Right Arm
    drawMuscularConnection(rShoulder, rElbow, 14.dp.toPx(), baseMuscleColor, lineStrokeColor, tPush || tPlank, glow)
    drawMuscularConnection(rElbow, rWrist, 10.dp.toPx(), baseMuscleColor, lineStrokeColor, false, glow)

    // Left Leg (Thigh: Hip->Knee, Calf: Knee->Ankle)
    drawMuscularConnection(lHip, lKnee, 20.dp.toPx(), baseMuscleColor, lineStrokeColor, tSquat || tRun, glow)
    drawMuscularConnection(lKnee, lAnkle, 14.dp.toPx(), baseMuscleColor, lineStrokeColor, tSquat || tRun, glow)

    // Right Leg
    drawMuscularConnection(rHip, rKnee, 20.dp.toPx(), baseMuscleColor, lineStrokeColor, tSquat || tRun, glow)
    drawMuscularConnection(rKnee, rAnkle, 14.dp.toPx(), baseMuscleColor, lineStrokeColor, tSquat || tRun, glow)


    // 2. DRAW THE TRUNK (CHEST, SPINE & ABDOMINALS)

    // Draw Neck Support Collar
    drawLine(
        color = lineStrokeColor,
        start = head,
        end = neck,
        strokeWidth = 6.dp.toPx()
    )
    drawCircle(
        color = HologramCyan,
        radius = 4.dp.toPx(),
        center = neck,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Pelvis Triangular Base Plate
    val pelvisPath = Path().apply {
        moveTo(pelvis.x, pelvis.y)
        lineTo(lHip.x, lHip.y)
        lineTo(rHip.x, rHip.y)
        close()
    }
    drawPath(path = pelvisPath, color = baseMuscleColor)
    drawPath(path = pelvisPath, color = lineStrokeColor, style = Stroke(width = 2.dp.toPx()))


    // 3. SIX PACK ABDOMINAL CORE TILES (Drawn along Spine)
    // We place 3 rows of 2 abdominal tiles symmetrically around the spine-pelvis vector
    val coreStart = neck
    val coreEnd = pelvis
    
    for (i in 0..2) {
        val pct = 0.45f + i * 0.16f
        val spineCenter = Offset(
            coreStart.x + (coreEnd.x - coreStart.x) * pct,
            coreStart.y + (coreEnd.y - coreStart.y) * pct
        )
        // Vector pointing outwards from spine
        val dx = coreEnd.x - coreStart.x
        val dy = coreEnd.y - coreStart.y
        val spineLen = kotlin.math.sqrt(dx * dx + dy * dy)
        
        if (spineLen > 5f) {
            val nx = -dy / spineLen
            val ny = dx / spineLen
            
            val absWidth = 14.dp.toPx()
            val absHeight = 9.dp.toPx()

            // Left and Right Ab plates
            val leftAbCenter = Offset(spineCenter.x + nx * (absWidth * 0.8f), spineCenter.y + ny * (absWidth * 0.8f))
            val rightAbCenter = Offset(spineCenter.x - nx * (absWidth * 0.8f), spineCenter.y - ny * (absWidth * 0.8f))

            val abColor = if (tSitUp || tPlank) AccentCrimson.copy(alpha = 0.3f + 0.6f * glow) else baseMuscleColor.copy(alpha = 0.5f)

            // Left Tile
            drawRect(
                color = abColor,
                topLeft = Offset(leftAbCenter.x - absWidth / 2f, leftAbCenter.y - absHeight / 2f),
                size = Size(absWidth, absHeight)
            )
            drawRect(
                color = lineStrokeColor,
                topLeft = Offset(leftAbCenter.x - absWidth / 2f, leftAbCenter.y - absHeight / 2f),
                size = Size(absWidth, absHeight),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Right Tile
            drawRect(
                color = abColor,
                topLeft = Offset(rightAbCenter.x - absWidth / 2f, rightAbCenter.y - absHeight / 2f),
                size = Size(absWidth, absHeight)
            )
            drawRect(
                color = lineStrokeColor,
                topLeft = Offset(rightAbCenter.x - absWidth / 2f, rightAbCenter.y - absHeight / 2f),
                size = Size(absWidth, absHeight),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }


    // 4. MASSIVE MUSCULAR CHEST PLATES (Pectorals)
    // Left and Right Pectorals drawn as quad plates below the shoulders
    val chestCenter = Offset(
        coreStart.x + (coreEnd.x - coreStart.x) * 0.32f,
        coreStart.y + (coreEnd.y - coreStart.y) * 0.32f
    )

    val chestColor = if (tPush) AccentCrimson.copy(alpha = 0.3f + 0.6f * glow) else baseMuscleColor

    // Left Pec Plate (Left shoulder, neck base, chest center, lower-left chest point)
    val leftPecPath = Path().apply {
        moveTo(neck.x, neck.y)
        lineTo(lShoulder.x, lShoulder.y)
        // Mid chest line
        lineTo(lShoulder.x + (chestCenter.x - lShoulder.x) * 0.5f, lShoulder.y + (chestCenter.y - lShoulder.y) * 1.3f)
        lineTo(chestCenter.x, chestCenter.y)
        close()
    }
    drawPath(path = leftPecPath, color = chestColor)
    drawPath(path = leftPecPath, color = lineStrokeColor, style = Stroke(width = 2.dp.toPx()))

    // Right Pec Plate
    val rightPecPath = Path().apply {
        moveTo(neck.x, neck.y)
        lineTo(rShoulder.x, rShoulder.y)
        lineTo(rShoulder.x + (chestCenter.x - rShoulder.x) * 0.5f, rShoulder.y + (chestCenter.y - rShoulder.y) * 1.3f)
        lineTo(chestCenter.x, chestCenter.y)
        close()
    }
    drawPath(path = rightPecPath, color = chestColor)
    drawPath(path = rightPecPath, color = lineStrokeColor, style = Stroke(width = 2.dp.toPx()))


    // 5. THE HEAD (GORGEOUS DETAILED HUMANOID HEAD VISOR)
    val headRadius = 22.dp.toPx()
    // Filled face
    drawCircle(
        color = baseMuscleColor,
        radius = headRadius,
        center = head
    )
    // 2D bold anime border
    drawCircle(
        color = lineStrokeColor,
        radius = headRadius,
        center = head,
        style = Stroke(width = 2.5.dp.toPx())
    )

    // Hologram visor glowing band (Solo Leveling System visor style)
    val visorPath = Path().apply {
        moveTo(head.x - headRadius * 0.8f, head.y - headRadius * 0.15f)
        lineTo(head.x + headRadius * 0.8f, head.y - headRadius * 0.15f)
        lineTo(head.x + headRadius * 0.6f, head.y + headRadius * 0.25f)
        lineTo(head.x - headRadius * 0.6f, head.y + headRadius * 0.25f)
        close()
    }
    drawPath(
        path = visorPath,
        color = HologramCyan.copy(alpha = 0.5f + 0.5f * glow)
    )
    drawPath(
        path = visorPath,
        color = lineStrokeColor,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Glowing core joints
    val keyJoints = listOf(lShoulder, rShoulder, lElbow, rElbow, lWrist, rWrist, lHip, rHip, lKnee, rKnee, lAnkle, rAnkle)
    for (node in keyJoints) {
        drawCircle(
            color = ComicBorder,
            radius = 6.dp.toPx(),
            center = node
        )
        drawCircle(
            color = HologramCyan,
            radius = 3.5.dp.toPx(),
            center = node
        )
    }
}

// Custom muscular capsule renderer with perpendicular coordinates
private fun DrawScope.drawMuscularConnection(
    start: Offset,
    end: Offset,
    muscleWidth: Float,
    baseColor: Color,
    borderColor: Color,
    isTargeted: Boolean,
    glowAlpha: Float
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = kotlin.math.sqrt(dx * dx + dy * dy)
    if (len < 1f) return

    val nx = -dy / len
    val ny = dx / len

    // Set up 4 corners of a gorgeous muscle plate
    val p1 = Offset(start.x + nx * muscleWidth, start.y + ny * muscleWidth)
    val p2 = Offset(end.x + nx * (muscleWidth * 0.7f), end.y + ny * (muscleWidth * 0.7f))
    val p3 = Offset(end.x - nx * (muscleWidth * 0.7f), end.y - ny * (muscleWidth * 0.7f))
    val p4 = Offset(start.x - nx * muscleWidth, start.y - ny * muscleWidth)

    val path = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        lineTo(p4.x, p4.y)
        close()
    }

    if (isTargeted) {
        // High contrast hot crimson neon target stimulation
        val targetColor = AccentCrimson
        drawPath(
            path = path,
            color = targetColor.copy(alpha = 0.3f + 0.5f * glowAlpha)
        )
        drawPath(
            path = path,
            color = targetColor.copy(alpha = 0.85f)
        )
    } else {
        // Hologram Cyan gradient fill
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(baseColor, baseColor.copy(alpha = 0.35f)),
                start = start,
                end = end
            )
        )
    }

    // Bold comic outlines
    drawPath(
        path = path,
        color = borderColor,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun getTargetMuscles(exerciseName: String): List<String> {
    return when (exerciseName) {
        "Push-Ups" -> listOf("Pectorals", "Triceps", "Anterior Deltoids")
        "Squats" -> listOf("Quadriceps", "Glutes", "Calves")
        "Sit-Ups" -> listOf("Abdominals", "Obliques", "Hip Flexors")
        "Plank" -> listOf("Transverse Abdominis", "Core Spine", "Serratus")
        else -> listOf("Hamstrings", "Quadriceps", "Calves", "Cardio Engine")
    }
}
