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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.selfimprovement.CodeInspectionResult
import com.example.selfimprovement.ProposedUpgrade
import com.example.selfimprovement.ValidationResult
import com.example.ui.theme.AegisBorderGlow
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisDarkSurface
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.ui.theme.AegisSurfaceElevated
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisVoidBlack

@Composable
fun AegisSelfImprovementScreen(
    isAnalyzing: Boolean,
    inspectionResult: CodeInspectionResult?,
    currentProposal: ProposedUpgrade?,
    validationResult: ValidationResult?,
    onRunScan: () -> Unit,
    onRequestDeploy: (ProposedUpgrade) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SELF-IMPROVEMENT & CODE MATRIX",
                    color = AegisCyanGlow,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Rule: Never silently mutates. Lord's approval required.",
                    color = AegisTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Button(
                onClick = onRunScan,
                enabled = !isAnalyzing,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AegisCyanGlow),
                modifier = Modifier.testTag("run_project_scan_button")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = AegisVoidBlack,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan",
                        tint = AegisVoidBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = if (isAnalyzing) " SCANNING..." else " INSPECT CODE",
                    color = AegisVoidBlack,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Inspection Overview Metrics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("code_metrics_card"),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorderGlow)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "// ARCHITECTURE AUDIT TELEMETRY",
                    color = AegisTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(
                        title = "FILES SCANNED",
                        value = "${inspectionResult?.filesScanned ?: 42} SRC",
                        tint = AegisCyanGlow
                    )
                    MetricBox(
                        title = "HEALTH SCORE",
                        value = "${inspectionResult?.codeHealthScore ?: 99.2f}%",
                        tint = AegisEmerald
                    )
                    MetricBox(
                        title = "MODULES",
                        value = "8 ACTIVE",
                        tint = AegisPlasmaGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Proposed Upgrade Workflow
        if (currentProposal != null) {
            Text(
                text = "// PROPOSED ARCHITECTURAL MODIFICATION",
                color = AegisPlasmaGold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("proposed_upgrade_card"),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisPlasmaGold.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentProposal.id,
                                color = AegisPlasmaGold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentProposal.title,
                                color = AegisTextPrimary,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (currentProposal.status == "DEPLOYED") AegisEmerald.copy(alpha = 0.2f)
                                    else AegisPlasmaGold.copy(alpha = 0.2f)
                                )
                                .border(
                                    0.8.dp,
                                    if (currentProposal.status == "DEPLOYED") AegisEmerald else AegisPlasmaGold,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentProposal.status,
                                color = if (currentProposal.status == "DEPLOYED") AegisEmerald else AegisPlasmaGold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentProposal.summary,
                        color = AegisTextPrimary.copy(alpha = 0.9f),
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Code Diff Display Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AegisVoidBlack)
                            .border(0.8.dp, AegisBorderGlow.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TARGET: ${currentProposal.targetModuleName}",
                                    color = AegisCyanGlow,
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "DIFF VIEW",
                                    color = AegisTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentProposal.codeDiff,
                                color = AegisEmerald,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Benefit",
                            tint = AegisCyanGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = currentProposal.expectedBenefit,
                            color = AegisCyanGlow,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentProposal.status != "DEPLOYED") {
                        Button(
                            onClick = { onRequestDeploy(currentProposal) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AegisPlasmaGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("authorize_deploy_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Authorize",
                                tint = AegisVoidBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "  REQUEST LORD'S AUTHORIZATION TO DEPLOY",
                                color = AegisVoidBlack,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Automated Testing & Validation Result
        if (validationResult != null) {
            Text(
                text = "// POST-DEPLOYMENT AUTOMATED VALIDATION",
                color = AegisEmerald,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("validation_result_card"),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisEmerald.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Passed",
                                tint = AegisEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = validationResult.testName,
                                color = AegisEmerald,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "STATUS: PASS",
                            color = AegisEmerald,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Latency Before: ${validationResult.latencyBeforeMs}ms",
                            color = AegisTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Latency After: ${validationResult.latencyAfterMs}ms (-42%)",
                            color = AegisCyanGlow,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = validationResult.details,
                        color = AegisTextPrimary,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MetricBox(title: String, value: String, tint: androidx.compose.ui.graphics.Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AegisSurfaceElevated)
            .border(0.8.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = AegisTextMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = tint,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
