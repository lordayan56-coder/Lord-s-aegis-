package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AegisBrain
import com.example.ai.BrainOutput
import com.example.data.AegisDatabase
import com.example.data.AegisRepository
import com.example.data.AuditLogEntity
import com.example.data.MemoryEntity
import com.example.data.ProjectModuleEntity
import com.example.security.AegisSecurityManager
import com.example.security.PrivilegedActionRequest
import com.example.selfimprovement.AegisSelfImprovementEngine
import com.example.selfimprovement.ProposedUpgrade
import com.example.voice.AegisVoiceEngine
import com.example.voice.VoiceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AegisDeck {
    CORE_HUD,
    MEMORY_VAULT,
    SECURITY_MATRIX,
    SELF_IMPROVEMENT,
    MODULES_REGISTRY
}

data class ActivityLog(
    val id: String = System.currentTimeMillis().toString() + "_" + (Math.random() * 1000).toInt(),
    val tag: String, // "[SYS]", "[VOICE]", "[BRAIN]", "[SEC]", "[UPGRADE]"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AegisViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AegisDatabase.getDatabase(application, viewModelScope)
    val repository = AegisRepository(database.aegisDao())

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allModules: StateFlow<List<ProjectModuleEntity>> = repository.allModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.recentAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityManager = AegisSecurityManager(application, repository)
    val selfImprovementEngine = AegisSelfImprovementEngine(repository)

    private val brain = AegisBrain(repository)

    val voiceEngine = AegisVoiceEngine(application) { spokenCommand ->
        handleUserQuery(spokenCommand)
    }

    private val _currentDeck = MutableStateFlow(AegisDeck.CORE_HUD)
    val currentDeck: StateFlow<AegisDeck> = _currentDeck.asStateFlow()

    private val _lastResponse = MutableStateFlow(
        BrainOutput.SpeechAndAction(
            spokenText = "LORD'S AEGIS online. All systems calibrated for Lord.",
            displayText = "LORD'S AEGIS // DEFENSE & COMMAND MATRIX ONLINE\nAll core subroutines operating at peak fidelity. Standing by for Lord's orders.",
            suggestedQuickActions = listOf("Status Report", "Analyze Project", "Run Diagnostics")
        )
    )
    val lastResponse: StateFlow<BrainOutput.SpeechAndAction> = _lastResponse.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(
        listOf(
            ActivityLog(tag = "[SYS]", message = "Aegis Kernel initialized. Owner: Lord."),
            ActivityLog(tag = "[VOICE]", message = "Acoustic Synthesizer primed (Deep Male / Baritone)."),
            ActivityLog(tag = "[SEC]", message = "Biometric & Cipher shields engaged. Clearance: ALPHA-ZERO."),
            ActivityLog(tag = "[NET]", message = "Sensory neural bridge linked.")
        )
    )
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _showCipherPrompt = MutableStateFlow(false)
    val showCipherPrompt: StateFlow<Boolean> = _showCipherPrompt.asStateFlow()

    private val _showAddMemoryDialog = MutableStateFlow(false)
    val showAddMemoryDialog: StateFlow<Boolean> = _showAddMemoryDialog.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun switchDeck(deck: AegisDeck) {
        _currentDeck.value = deck
        addActivityLog("[NAV]", "Tactical view changed to ${deck.name}")
    }

    fun handleUserQuery(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch(Dispatchers.Main) {
            _isProcessing.value = true
            voiceEngine.setThinkingState()
            addActivityLog("[COMMAND]", "Lord: \"$query\"")

            val output = brain.processQuery(query, allMemories.value)
            _lastResponse.value = output
            _isProcessing.value = false

            addActivityLog("[AEGIS]", output.displayText.lines().firstOrNull() ?: "Processed command")

            // Speak response if voice engine is ready
            voiceEngine.speak(output.spokenText)

            // Handle privileged requirement
            if (output.requiresLordApproval && output.privilegedPayload != null) {
                when (output.privilegedPayload) {
                    "APPLY_TELEMETRY_OPTIMIZER_PATCH" -> {
                        val proposal = selfImprovementEngine.currentProposal.value
                        if (proposal != null) {
                            requestUpgradeApproval(proposal)
                        }
                    }
                    "PURGE_ALL_MEMORIES" -> {
                        requestMemoryPurgeApproval()
                    }
                }
            }
        }
    }

    fun requestUpgradeApproval(proposal: ProposedUpgrade) {
        securityManager.requestPrivilegedApproval(
            PrivilegedActionRequest(
                id = proposal.id,
                title = "Deploy Self-Improvement Patch: ${proposal.title}",
                description = proposal.summary,
                payload = proposal.id,
                category = "CODE_MUTATION",
                onExecute = {
                    val result = selfImprovementEngine.applyApprovedUpgrade(proposal, allModules.value)
                    voiceEngine.speak("Modification applied successfully, Lord. Latency reduced to ${result.latencyAfterMs} milliseconds.")
                    addActivityLog("[UPGRADE]", "Patch ${proposal.id} deployed by Lord.")
                    "Upgrade Applied: Latency ${result.latencyAfterMs}ms."
                }
            )
        )
    }

    fun requestMemoryPurgeApproval() {
        securityManager.requestPrivilegedApproval(
            PrivilegedActionRequest(
                id = "REQ-PURGE-MEM",
                title = "Complete Memory Vault Purge",
                description = "Wipes all recorded memory sectors, owner facts, and historical notes from the quantum bank.",
                payload = "PURGE_ALL_MEMORIES",
                category = "DATABASE_DESTRUCTION",
                onExecute = {
                    repository.clearMemories()
                    voiceEngine.speak("Quantum memory archives have been purged per Lord's direct authorization.")
                    addActivityLog("[SEC]", "Memory Vault wiped by Lord.")
                    "Memory Purged."
                }
            )
        )
    }

    fun authorizeCurrentRequestBiometrically() {
        val request = securityManager.pendingRequest.value ?: return
        viewModelScope.launch(Dispatchers.Main) {
            securityManager.authorizeWithBiometrics(request)
            addActivityLog("[AUTH]", "Lord authorized ${request.title} via Biometric Thumbprint.")
        }
    }

    fun authorizeCurrentRequestWithCipher(cipher: String, onError: (String) -> Unit) {
        val request = securityManager.pendingRequest.value ?: return
        viewModelScope.launch(Dispatchers.Main) {
            val result = securityManager.authorizeWithCipher(request, cipher)
            result.onSuccess {
                _showCipherPrompt.value = false
                addActivityLog("[AUTH]", "Lord authorized ${request.title} via Master Cipher.")
            }.onFailure { ex ->
                onError(ex.message ?: "Authorization failed.")
            }
        }
    }

    fun denyCurrentRequest() {
        val request = securityManager.pendingRequest.value ?: return
        viewModelScope.launch(Dispatchers.Main) {
            securityManager.denyRequest(request)
            _showCipherPrompt.value = false
            voiceEngine.speak("Privileged operation cancelled, Lord.")
            addActivityLog("[SEC]", "Operation ${request.title} aborted by Lord.")
        }
    }

    fun runManualProjectScan() {
        viewModelScope.launch(Dispatchers.Main) {
            addActivityLog("[SCAN]", "Initiating project architecture scan...")
            val result = selfImprovementEngine.runProjectInspection()
            voiceEngine.speak("Project scan complete, Lord. Forty-two source files analyzed with ninety-nine point two percent structural integrity.")
            addActivityLog("[SCAN]", "Scanned ${result.filesScanned} files. Proposed Upgrade generated.")
        }
    }

    fun addManualMemory(category: String, key: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMemory(category, key, content)
            repository.logAction("MANUAL_MEMORY_ADD", "Lord added memory: $key", authorizedByLord = true)
            addActivityLog("[MEMORY]", "Recorded memory: $key")
        }
        _showAddMemoryDialog.value = false
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMemory(id)
            addActivityLog("[MEMORY]", "Deleted memory sector #$id")
        }
    }

    fun toggleModule(module: ProjectModuleEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = if (module.status == "ONLINE") "STANDBY" else "ONLINE"
            repository.updateModuleStatus(module.id, newStatus)
            addActivityLog("[MODULE]", "Module ${module.name} shifted to $newStatus")
        }
    }

    fun setShowCipherPrompt(show: Boolean) {
        _showCipherPrompt.value = show
    }

    fun setShowAddMemoryDialog(show: Boolean) {
        _showAddMemoryDialog.value = show
    }

    private fun addActivityLog(tag: String, message: String) {
        val newLog = ActivityLog(tag = tag, message = message)
        _activityLogs.value = (listOf(newLog) + _activityLogs.value).take(40)
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.shutdown()
    }
}
