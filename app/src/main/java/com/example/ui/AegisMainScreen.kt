package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.security.PrivilegedActionRequest
import com.example.ui.components.AegisAddMemoryDialog
import com.example.ui.components.AegisBiometricDialog
import com.example.ui.components.AegisBottomControls
import com.example.ui.components.AegisCoreScreen
import com.example.ui.components.AegisMemoryVaultScreen
import com.example.ui.components.AegisModulesScreen
import com.example.ui.components.AegisSecurityDeckScreen
import com.example.ui.components.AegisSelfImprovementScreen
import com.example.ui.components.AegisTopHUD
import com.example.ui.theme.AegisVoidBlack
import com.example.voice.VoiceState

@Composable
fun AegisMainScreen(
    viewModel: AegisViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observe ViewModel State
    val currentDeck by viewModel.currentDeck.collectAsState()
    val lastResponse by viewModel.lastResponse.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val allMemories by viewModel.allMemories.collectAsState()
    val allModules by viewModel.allModules.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    // Voice Engine State
    val voiceState by viewModel.voiceEngine.voiceState.collectAsState()
    val audioRms by viewModel.voiceEngine.audioRms.collectAsState()
    val isContinuousListening by viewModel.voiceEngine.isContinuousListening.collectAsState()

    // Security & Self-Improvement State
    val pendingRequest by viewModel.securityManager.pendingRequest.collectAsState()
    val isAnalyzing by viewModel.selfImprovementEngine.isAnalyzing.collectAsState()
    val inspectionResult by viewModel.selfImprovementEngine.inspectionResult.collectAsState()
    val currentProposal by viewModel.selfImprovementEngine.currentProposal.collectAsState()
    val validationResult by viewModel.selfImprovementEngine.validationResult.collectAsState()
    val showAddMemoryDialog by viewModel.showAddMemoryDialog.collectAsState()

    // Microphone Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.voiceEngine.startListening()
        } else {
            Toast.makeText(context, "Microphone permission required for voice communication with Aegis.", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndStartVoice() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (voiceState == VoiceState.LISTENING) {
                viewModel.voiceEngine.stopListening()
            } else {
                viewModel.voiceEngine.startListening()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AegisVoidBlack,
        topBar = {
            AegisTopHUD(
                activeDeck = currentDeck,
                onSelectDeck = { viewModel.switchDeck(it) }
            )
        },
        bottomBar = {
            AegisBottomControls(
                voiceState = voiceState,
                audioRms = audioRms,
                isContinuousListening = isContinuousListening,
                onToggleContinuousListening = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.voiceEngine.toggleContinuousListening(!isContinuousListening)
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onVoiceTrigger = { checkAndStartVoice() },
                onStopVoice = { viewModel.voiceEngine.stopSpeaking() },
                onSubmitTextCommand = { viewModel.handleUserQuery(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AegisVoidBlack)
        ) {
            when (currentDeck) {
                AegisDeck.CORE_HUD -> {
                    AegisCoreScreen(
                        voiceState = voiceState,
                        audioRms = audioRms,
                        lastResponse = lastResponse,
                        activityLogs = activityLogs,
                        onQuickAction = { viewModel.handleUserQuery(it) }
                    )
                }

                AegisDeck.MEMORY_VAULT -> {
                    AegisMemoryVaultScreen(
                        memories = allMemories,
                        onAddMemoryClick = { viewModel.setShowAddMemoryDialog(true) },
                        onDeleteMemory = { viewModel.deleteMemory(it) },
                        onPurgeAllClick = { viewModel.requestMemoryPurgeApproval() }
                    )
                }

                AegisDeck.SECURITY_MATRIX -> {
                    AegisSecurityDeckScreen(
                        auditLogs = auditLogs,
                        onTriggerTestBiometric = {
                            viewModel.securityManager.requestPrivilegedApproval(
                                PrivilegedActionRequest(
                                    id = "REQ-TEST-BIOMETRIC",
                                    title = "Owner Identity Verification Test",
                                    description = "Diagnostic probe testing Lord's holographic biometric sensor calibration.",
                                    payload = "BIOMETRIC_PROBE",
                                    onExecute = {
                                        viewModel.voiceEngine.speak("Lord's biometric thumbprint verified successfully. Aegis security matrix nominal.")
                                        "Biometric Verified"
                                    }
                                )
                            )
                        }
                    )
                }

                AegisDeck.SELF_IMPROVEMENT -> {
                    AegisSelfImprovementScreen(
                        isAnalyzing = isAnalyzing,
                        inspectionResult = inspectionResult,
                        currentProposal = currentProposal,
                        validationResult = validationResult,
                        onRunScan = { viewModel.runManualProjectScan() },
                        onRequestDeploy = { proposal ->
                            viewModel.requestUpgradeApproval(proposal)
                        }
                    )
                }

                AegisDeck.MODULES_REGISTRY -> {
                    AegisModulesScreen(
                        modules = allModules,
                        onToggleModule = { viewModel.toggleModule(it) }
                    )
                }
            }
        }
    }

    // Biometric & Master Cipher Approval Dialog
    pendingRequest?.let { req ->
        AegisBiometricDialog(
            request = req,
            onAuthorizeBiometrics = { viewModel.authorizeCurrentRequestBiometrically() },
            onAuthorizeCipher = { cipher ->
                viewModel.authorizeCurrentRequestWithCipher(cipher) { err ->
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            },
            onDeny = { viewModel.denyCurrentRequest() }
        )
    }

    // Add Memory Dialog
    if (showAddMemoryDialog) {
        AegisAddMemoryDialog(
            onDismiss = { viewModel.setShowAddMemoryDialog(false) },
            onSaveMemory = { category, key, content ->
                viewModel.addManualMemory(category, key, content)
            }
        )
    }
}
