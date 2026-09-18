package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.security.PrivilegedActionRequest
import com.example.ui.theme.AegisBorderGlow
import com.example.ui.theme.AegisCrimson
import com.example.ui.theme.AegisCyanGlow
import com.example.ui.theme.AegisDarkSurface
import com.example.ui.theme.AegisEmerald
import com.example.ui.theme.AegisPlasmaGold
import com.example.ui.theme.AegisSurfaceElevated
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisVoidBlack

@Composable
fun AegisBiometricDialog(
    request: PrivilegedActionRequest,
    onAuthorizeBiometrics: () -> Unit,
    onAuthorizeCipher: (String) -> Unit,
    onDeny: () -> Unit
) {
    var cipherInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var useCipherMode by remember { mutableStateOf(false) }

    val infinitePulse = rememberInfiniteTransition(label = "BioPulse")
    val pulseGlow by infinitePulse.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    Dialog(onDismissRequest = onDeny) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("biometric_auth_dialog"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AegisPlasmaGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = AegisPlasmaGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "OWNER AUTHORIZATION MANDATORY",
                        color = AegisPlasmaGold,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = request.title,
                    color = AegisTextPrimary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = request.description,
                    color = AegisTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!useCipherMode) {
                    // Holographic Biometric Scanner Pad
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(pulseGlow)
                            .clip(CircleShape)
                            .background(AegisSurfaceElevated)
                            .border(2.dp, AegisEmerald, CircleShape)
                            .clickable { onAuthorizeBiometrics() }
                            .testTag("biometric_thumbprint_pad"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint Sensor",
                                tint = AegisEmerald,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "TOUCH TO VERIFY",
                                color = AegisEmerald,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Lord's Thumbprint Authorization Sensor",
                        color = AegisTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "› Use Cryptographic Master Cipher instead",
                        color = AegisCyanGlow,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { useCipherMode = true }
                            .padding(4.dp)
                    )
                } else {
                    // Cipher Entry Mode
                    OutlinedTextField(
                        value = cipherInput,
                        onValueChange = {
                            cipherInput = it
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("master_cipher_input"),
                        label = {
                            Text(
                                text = "ENTER LORD MASTER CIPHER",
                                color = AegisTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        placeholder = {
                            Text(
                                text = "e.g. LORD-ALPHA-01",
                                color = AegisTextMuted.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AegisPlasmaGold,
                            unfocusedBorderColor = AegisBorderGlow,
                            focusedTextColor = AegisTextPrimary,
                            unfocusedTextColor = AegisTextPrimary
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorMessage!!,
                            color = AegisCrimson,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (cipherInput.isNotBlank()) {
                                onAuthorizeCipher(cipherInput)
                            } else {
                                errorMessage = "Cipher cannot be empty."
                            }
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPlasmaGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_cipher_button")
                    ) {
                        Text(
                            text = "AUTHORIZE WITH CIPHER",
                            color = AegisVoidBlack,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "‹ Return to Biometric Thumbprint",
                        color = AegisCyanGlow,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { useCipherMode = false }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Deny / Abort Button
                OutlinedButton(
                    onClick = onDeny,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AegisCrimson),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deny_privileged_request_button")
                ) {
                    Text(
                        text = "ABORT & REJECT OPERATION",
                        color = AegisCrimson,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
