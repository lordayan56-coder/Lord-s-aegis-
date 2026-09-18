package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuditLogEntity
import com.example.ui.theme.AegisBorderGlow
import com.example.ui.theme.AegisCrimson
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisDarkSurface
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.ui.theme.AegisSurfaceElevated
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AegisSecurityDeckScreen(
    auditLogs: List<AuditLogEntity>,
    onTriggerTestBiometric: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "OWNER SECURITY & AUTHORIZATION MATRIX",
            color = AegisCyanGlow,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Owner Protocol: LORD // Privileged Operations Shielded",
            color = AegisTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Clearance & Sensor Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("security_clearance_card"),
            shape = RoundedCornerShape(8.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Shield",
                            tint = AegisEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "CLEARANCE: ALPHA-ZERO",
                                color = AegisEmerald,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lord's Supreme Executive Authority",
                                color = AegisTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onTriggerTestBiometric,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AegisEmerald),
                        modifier = Modifier.testTag("test_biometric_auth_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Auth",
                            tint = AegisDarkSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " TEST SCAN",
                            color = AegisDarkSurface,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SecurityPill("BIOMETRIC", "SENSOR ONLINE", AegisEmerald)
                    SecurityPill("MASTER CIPHER", "LORD-ALPHA-01", AegisPlasmaGold)
                    SecurityPill("SANDBOX", "ENFORCED", AegisCyanGlow)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "// PRIVILEGED AUDIT TRAIL (CRYPTOGRAPHIC LOG)",
            color = AegisTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Audit Trail List
        if (auditLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No audit log entries recorded yet.",
                    color = AegisTextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(auditLogs, key = { it.id }) { log ->
                    AuditLogCard(log = log)
                }
            }
        }
    }
}

@Composable
private fun SecurityPill(title: String, value: String, tintColor: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(AegisSurfaceElevated)
            .border(0.6.dp, tintColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = AegisTextMuted,
            fontSize = 8.5.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = tintColor,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AuditLogCard(log: AuditLogEntity) {
    val dateStr = SimpleDateFormat("HH:mm:ss // MM-dd", Locale.getDefault()).format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audit_log_${log.id}"),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            0.6.dp,
            if (log.severity == "PRIVILEGED") AegisPlasmaGold.copy(alpha = 0.6f) else AegisBorderGlow.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = when (log.severity) {
                    "PRIVILEGED" -> Icons.Default.Lock
                    "SECURITY" -> Icons.Default.Warning
                    else -> Icons.Default.CheckCircle
                },
                contentDescription = log.severity,
                tint = when (log.severity) {
                    "PRIVILEGED" -> AegisPlasmaGold
                    "SECURITY" -> AegisCrimson
                    else -> AegisEmerald
                },
                modifier = Modifier.size(16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.action,
                        color = AegisCyanGlow,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateStr,
                        color = AegisTextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = log.details,
                    color = AegisTextPrimary.copy(alpha = 0.9f),
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 2
                )
            }
        }
    }
}
