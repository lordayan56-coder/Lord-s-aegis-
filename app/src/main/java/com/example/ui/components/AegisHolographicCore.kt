package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AegisCrimson
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisElectricBlue
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.voice.VoiceState
import kotlin.math.cos
import kotlin.math.sin

private data class Particle(
    val baseAngle: Float,
    val orbitRadiusFraction: Float,
    val speed: Float,
    val size: Float,
    val opacity: Float
)

@Composable
fun AegisHolographicCore(
    voiceState: VoiceState,
    audioRms: Float,
    modifier: Modifier = Modifier
) {
    // Infinite animations for rotating rings and energy oscillations
    val infiniteTransition = rememberInfiniteTransition(label = "CoreTransition")

    val ringRotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotationOuter"
    )

    val ringRotationMiddle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotationMiddle"
    )

    val ringRotationInner by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotationInner"
    )

    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingPulse"
    )

    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticlePhase"
    )

    // Dynamic state colors
    val targetCoreColor = when (voiceState) {
        VoiceState.IDLE -> AegisCyanGlow
        VoiceState.LISTENING -> AegisEmerald
        VoiceState.THINKING -> AegisElectricBlue
        VoiceState.SPEAKING -> AegisPlasmaGold
        VoiceState.ERROR -> AegisCrimson
    }

    val animatedCoreColor by animateColorAsState(
        targetValue = targetCoreColor,
        animationSpec = tween(400),
        label = "CoreColor"
    )

    // Audio reactive expansion
    val animatedRms by animateFloatAsState(
        targetValue = audioRms.coerceIn(0f, 1f),
        animationSpec = tween(60),
        label = "AnimatedRms"
    )

    // Pre-allocated particles for holographic particle field
    val particles = remember {
        List(24) { i ->
            Particle(
                baseAngle = (i * 15f),
                orbitRadiusFraction = 0.35f + (i % 5) * 0.12f,
                speed = 0.6f + (i % 3) * 0.4f,
                size = 2f + (i % 4) * 1.5f,
                opacity = 0.4f + (i % 4) * 0.15f
            )
        }
    }

    Box(
        modifier = modifier
            .size(320.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.88f
            val reactiveBoost = animatedRms * (baseRadius * 0.16f)
            val currentRadius = (baseRadius + reactiveBoost) * breathingPulse

            // 1. Background holographic radial ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedCoreColor.copy(alpha = 0.28f + animatedRms * 0.25f),
                        animatedCoreColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.35f
                ),
                radius = currentRadius * 1.35f,
                center = center
            )

            // 2. Outer Segmented Ring with Ticks (rotating counter-clockwise)
            rotate(ringRotationOuter, center) {
                drawCircle(
                    color = animatedCoreColor.copy(alpha = 0.35f),
                    radius = currentRadius,
                    center = center,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 20f), 0f)
                    )
                )

                // 12 Tactical tick marks along outer rim
                for (i in 0 until 12) {
                    val angleRad = Math.toRadians((i * 30.0)).toFloat()
                    val startOffset = Offset(
                        center.x + (currentRadius - 6.dp.toPx()) * cos(angleRad),
                        center.y + (currentRadius - 6.dp.toPx()) * sin(angleRad)
                    )
                    val endOffset = Offset(
                        center.x + (currentRadius + 6.dp.toPx()) * cos(angleRad),
                        center.y + (currentRadius + 6.dp.toPx()) * sin(angleRad)
                    )
                    drawLine(
                        color = animatedCoreColor.copy(alpha = if (i % 3 == 0) 0.85f else 0.45f),
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = if (i % 3 == 0) 2.5.dp.toPx() else 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Middle Tactical Ring with Segmented Arcs (counter-rotating)
            rotate(ringRotationMiddle, center) {
                val midRadius = currentRadius * 0.76f
                drawCircle(
                    color = animatedCoreColor.copy(alpha = 0.45f),
                    radius = midRadius,
                    center = center,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(45f, 30f, 15f, 30f), 0f)
                    )
                )

                // Accent arcs
                drawArc(
                    color = animatedCoreColor.copy(alpha = 0.8f),
                    startAngle = 0f,
                    sweepAngle = 45f,
                    useCenter = false,
                    topLeft = Offset(center.x - midRadius, center.y - midRadius),
                    size = androidx.compose.ui.geometry.Size(midRadius * 2, midRadius * 2),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                drawArc(
                    color = animatedCoreColor.copy(alpha = 0.8f),
                    startAngle = 180f,
                    sweepAngle = 45f,
                    useCenter = false,
                    topLeft = Offset(center.x - midRadius, center.y - midRadius),
                    size = androidx.compose.ui.geometry.Size(midRadius * 2, midRadius * 2),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 4. Inner Ring with Hexagonal Crosshairs
            rotate(ringRotationInner, center) {
                val innerRadius = currentRadius * 0.52f
                drawCircle(
                    color = animatedCoreColor.copy(alpha = 0.65f),
                    radius = innerRadius,
                    center = center,
                    style = Stroke(
                        width = 1.8.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 10f), 0f)
                    )
                )

                // Crosshair indicators
                drawLine(
                    color = animatedCoreColor.copy(alpha = 0.5f),
                    start = Offset(center.x - innerRadius * 0.4f, center.y),
                    end = Offset(center.x + innerRadius * 0.4f, center.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = animatedCoreColor.copy(alpha = 0.5f),
                    start = Offset(center.x, center.y - innerRadius * 0.4f),
                    end = Offset(center.x, center.y + innerRadius * 0.4f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 5. Swirling Energy Particles
            particles.forEach { p ->
                val angle = (p.baseAngle + particlePhase * p.speed) % 360f
                val rad = Math.toRadians(angle.toDouble()).toFloat()
                val r = currentRadius * p.orbitRadiusFraction
                val px = center.x + r * cos(rad)
                val py = center.y + r * sin(rad)

                drawCircle(
                    color = animatedCoreColor.copy(alpha = p.opacity),
                    radius = p.size.dp.toPx(),
                    center = Offset(px, py)
                )
            }

            // 6. Central Arc Reactor Sphere
            val coreSphereRadius = (currentRadius * 0.28f) * (1f + animatedRms * 0.45f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        animatedCoreColor,
                        animatedCoreColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreSphereRadius * 1.25f
                ),
                radius = coreSphereRadius,
                center = center
            )

            // Inner glowing node
            drawCircle(
                color = Color.White,
                radius = coreSphereRadius * 0.35f,
                center = center
            )
        }
    }
}
