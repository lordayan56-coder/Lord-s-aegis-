package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBorderGlow
import com.example.ui.theme.AegisCrimson
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisDarkSurface
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.ui.theme.AegisSurfaceElevated
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.voice.VoiceState
import kotlin.math.sin

@Composable
fun AegisBottomControls(
    voiceState: VoiceState,
    audioRms: Float,
    isContinuousListening: Boolean,
    onToggleContinuousListening: () -> Unit,
    onVoiceTrigger: () -> Unit,
    onStopVoice: () -> Unit,
    onSubmitTextCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }

    val infinitePulse = rememberInfiniteTransition(label = "MicPulse")
    val pulseScale by infinitePulse.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // 1. Audio Reactive Waveform Scope
        AudioReactiveWaveform(
            voiceState = voiceState,
            audioRms = audioRms,
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .padding(bottom = 6.dp)
        )

        // 2. Tactical Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Continuous Listening Toggle Pill
            Box(
                modifier = Modifier
                    .testTag("continuous_listening_toggle")
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isContinuousListening) AegisEmerald.copy(alpha = 0.2f) else AegisDarkSurface)
                    .border(
                        1.dp,
                        if (isContinuousListening) AegisEmerald else AegisBorderGlow,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onToggleContinuousListening() }
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isContinuousListening) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.MicOff,
                        contentDescription = "Continuous Listening",
                        tint = if (isContinuousListening) AegisEmerald else AegisTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isContinuousListening) "WAKE ACTIVE" else "WAKE OFF",
                        color = if (isContinuousListening) AegisEmerald else AegisTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Keyboard Text Command Input Bar
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("command_text_input"),
                placeholder = {
                    Text(
                        text = when (voiceState) {
                            VoiceState.LISTENING -> "Listening to Lord..."
                            VoiceState.THINKING -> "Aegis analyzing..."
                            VoiceState.SPEAKING -> "Transmitting briefing..."
                            else -> "Enter command for Aegis..."
                        },
                        color = AegisTextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisCyanGlow,
                    unfocusedBorderColor = AegisBorderGlow,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary,
                    cursorColor = AegisCyanGlow
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (textInput.isNotBlank()) {
                        onSubmitTextCommand(textInput)
                        textInput = ""
                    }
                }),
                trailingIcon = {
                    if (textInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSubmitTextCommand(textInput)
                                textInput = ""
                            },
                            modifier = Modifier.testTag("send_command_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Command",
                                tint = AegisCyanGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            // Primary Glowing Voice Mic Button
            val isListening = voiceState == VoiceState.LISTENING
            val isSpeaking = voiceState == VoiceState.SPEAKING
            val micColor = when {
                isSpeaking -> AegisPlasmaGold
                isListening -> AegisEmerald
                else -> AegisCyanGlow
            }

            Box(
                modifier = Modifier
                    .testTag("voice_trigger_button")
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AegisDarkSurface)
                    .border(
                        width = if (isListening) 2.dp else 1.2.dp,
                        color = micColor,
                        shape = CircleShape
                    )
                    .clickable {
                        if (isSpeaking) {
                            onStopVoice()
                        } else {
                            onVoiceTrigger()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isSpeaking -> Icons.Default.Stop
                        isListening -> Icons.Default.Mic
                        else -> Icons.Default.Mic
                    },
                    contentDescription = "Voice Control",
                    tint = micColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun AudioReactiveWaveform(
    voiceState: VoiceState,
    audioRms: Float,
    modifier: Modifier = Modifier
) {
    val barColor = when (voiceState) {
        VoiceState.IDLE -> AegisCyanGlow.copy(alpha = 0.35f)
        VoiceState.LISTENING -> AegisEmerald
        VoiceState.THINKING -> AegisCyanGlow
        VoiceState.SPEAKING -> AegisPlasmaGold
        VoiceState.ERROR -> AegisCrimson
    }

    val phaseTransition = rememberInfiniteTransition(label = "WavePhase")
    val phase by phaseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Phase"
    )

    Canvas(modifier = modifier) {
        val barCount = 32
        val spacing = size.width / barCount
        val maxHeight = size.height * 0.9f

        for (i in 0 until barCount) {
            val normalizedIdx = i.toFloat() / barCount
            val wave = sin(Math.toRadians(((i * 12.0) + phase)).toDouble()).toFloat()
            val dynamicScale = when (voiceState) {
                VoiceState.SPEAKING, VoiceState.LISTENING -> (0.2f + audioRms * 0.8f) * (0.4f + Math.abs(wave) * 0.6f)
                VoiceState.THINKING -> 0.3f + (Math.abs(wave) * 0.4f)
                else -> 0.12f + (Math.abs(wave) * 0.1f)
            }

            val barHeight = (maxHeight * dynamicScale).coerceAtLeast(3f)
            val x = i * spacing + spacing / 2f
            val yTop = (size.height - barHeight) / 2f
            val yBottom = yTop + barHeight

            drawLine(
                color = barColor,
                start = Offset(x, yTop),
                end = Offset(x, yBottom),
                strokeWidth = (spacing * 0.55f).coerceAtLeast(2f),
                cap = StrokeCap.Round
            )
        }
    }
}
