package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AegisDeck
import com.example.ui.theme.AegisBorderGlow
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisDarkSurface
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.ui.theme.AegisSurfaceElevated
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisVoidBlack
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AegisTopHUD(
    activeDeck: AegisDeck,
    onSelectDeck: (AegisDeck) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            currentTime = sdf.format(Date())
            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Upper Telemetry Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Title & System Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AegisEmerald)
                )
                Text(
                    text = " LORD'S AEGIS ",
                    color = AegisCyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "// GUARDIAN",
                    color = AegisTextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }

            // Right Owner & Clock
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AegisSurfaceElevated)
                        .border(0.8.dp, AegisPlasmaGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "OWNER: LORD",
                        color = AegisPlasmaGold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "  $currentTime",
                    color = AegisTextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Sub Telemetry Status Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "THREAT: ZERO // DEFENSE MATRIX ACTIVE",
                color = AegisTextMuted,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "LINK: 99.8% // CIPHER: AES-256",
                color = AegisCyanGlow.copy(alpha = 0.8f),
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Deck Navigation Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeckPill(
                title = "CORE HUD",
                icon = Icons.Default.Shield,
                isSelected = activeDeck == AegisDeck.CORE_HUD,
                onClick = { onSelectDeck(AegisDeck.CORE_HUD) },
                tag = "deck_pill_core"
            )
            DeckPill(
                title = "MEMORY VAULT",
                icon = Icons.Default.Memory,
                isSelected = activeDeck == AegisDeck.MEMORY_VAULT,
                onClick = { onSelectDeck(AegisDeck.MEMORY_VAULT) },
                tag = "deck_pill_memory"
            )
            DeckPill(
                title = "SECURITY DECK",
                icon = Icons.Default.Security,
                isSelected = activeDeck == AegisDeck.SECURITY_MATRIX,
                onClick = { onSelectDeck(AegisDeck.SECURITY_MATRIX) },
                tag = "deck_pill_security"
            )
            DeckPill(
                title = "SELF-IMPROVEMENT",
                icon = Icons.Default.Build,
                isSelected = activeDeck == AegisDeck.SELF_IMPROVEMENT,
                onClick = { onSelectDeck(AegisDeck.SELF_IMPROVEMENT) },
                tag = "deck_pill_improvement"
            )
            DeckPill(
                title = "MODULES",
                icon = Icons.Default.Widgets,
                isSelected = activeDeck == AegisDeck.MODULES_REGISTRY,
                onClick = { onSelectDeck(AegisDeck.MODULES_REGISTRY) },
                tag = "deck_pill_modules"
            )
        }
    }
}

@Composable
private fun DeckPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val bgColor = if (isSelected) AegisCyanGlow.copy(alpha = 0.18f) else AegisDarkSurface
    val borderColor = if (isSelected) AegisCyanGlow else AegisBorderGlow.copy(alpha = 0.4f)
    val textColor = if (isSelected) AegisCyanGlow else AegisTextMuted

    Row(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = title,
            color = textColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
