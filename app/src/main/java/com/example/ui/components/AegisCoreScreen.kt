package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.BrainOutput
import com.example.ui.ActivityLog
import com.example.ui.theme.AegisBorderGlow
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisDarkSurface
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.ui.theme.AegisSurfaceElevated
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.voice.VoiceState

@Composable
fun AegisCoreScreen(
    voiceState: VoiceState,
    audioRms: Float,
    lastResponse: BrainOutput.SpeechAndAction,
    activityLogs: List<ActivityLog>,
    onQuickAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Voice State Status Badge
        VoiceStateBadge(voiceState = voiceState)

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Central Animated Holographic AI Core
        AegisHolographicCore(
            voiceState = voiceState,
            audioRms = audioRms,
            modifier = Modifier
                .size(290.dp)
                .testTag("aegis_holographic_core")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Quick Tactical Directive Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chips = if (lastResponse.suggestedQuickActions.isNotEmpty()) {
                lastResponse.suggestedQuickActions
            } else {
                listOf("Status Report", "Analyze Project", "Run Diagnostics", "Who is Lord")
            }

            chips.forEach { chip ->
                TacticalChip(
                    label = chip,
                    onClick = { onQuickAction(chip) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Holographic Briefing & Response Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .testTag("tactical_briefing_card"),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorderGlow)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Briefing",
                            tint = AegisCyanGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "AEGIS TACTICAL BRIEFING",
                            color = AegisCyanGlow,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    if (lastResponse.requiresLordApproval) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AegisPlasmaGold.copy(alpha = 0.2f))
                                .border(1.dp, AegisPlasmaGold, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "APPROVAL REQUIRED",
                                color = AegisPlasmaGold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Spoken excerpt / primary text
                Text(
                    text = lastResponse.displayText,
                    color = AegisTextPrimary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Live Activity Feed Telemetry Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .heightIn(max = 160.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = AegisSurfaceElevated.copy(alpha = 0.6f)),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, AegisBorderGlow.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "// LIVE AEGIS TELEMETRY STREAM",
                    color = AegisTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    reverseLayout = false
                ) {
                    items(activityLogs.take(8)) { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = log.tag,
                                color = when (log.tag) {
                                    "[SYS]" -> AegisEmerald
                                    "[VOICE]" -> AegisPlasmaGold
                                    "[SEC]" -> AegisCyanGlow
                                    "[UPGRADE]" -> AegisPlasmaGold
                                    else -> AegisTextMuted
                                },
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = log.message,
                                color = AegisTextPrimary.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VoiceStateBadge(voiceState: VoiceState) {
    val (label, color, icon) = when (voiceState) {
        VoiceState.IDLE -> Triple("STANDBY // AWAITING COMMAND", AegisCyanGlow, Icons.Default.Info)
        VoiceState.LISTENING -> Triple("ACOUSTIC SENSORS ACTIVE // LISTENING", AegisEmerald, Icons.Default.RecordVoiceOver)
        VoiceState.THINKING -> Triple("NEURAL MATRIX SYNCHRONIZING", AegisCyanGlow, Icons.Default.Psychology)
        VoiceState.SPEAKING -> Triple("TRANSMITTING AUDITORY BRIEFING", AegisPlasmaGold, Icons.Default.ElectricBolt)
        VoiceState.ERROR -> Triple("SENSORY GLITCH // RECOVERING", AegisCyanGlow, Icons.Default.Info)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AegisDarkSurface)
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                color = color,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TacticalChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AegisSurfaceElevated)
            .border(0.8.dp, AegisCyanGlow.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "› $label",
            color = AegisCyanGlow,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
