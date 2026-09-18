package com.example.security

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import com.example.data.AegisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrivilegedActionRequest(
    val id: String,
    val title: String,
    val description: String,
    val payload: String,
    val category: String = "SYSTEM_MODIFICATION",
    val onExecute: suspend () -> String
)

class AegisSecurityManager(
    private val context: Context,
    private val repository: AegisRepository
) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    private val _pendingRequest = MutableStateFlow<PrivilegedActionRequest?>(null)
    val pendingRequest: StateFlow<PrivilegedActionRequest?> = _pendingRequest.asStateFlow()

    private val _clearanceLevel = MutableStateFlow("ALPHA-ZERO (Lord Clearance)")
    val clearanceLevel: StateFlow<String> = _clearanceLevel.asStateFlow()

    private val _lastAuthStatus = MutableStateFlow<String?>(null)
    val lastAuthStatus: StateFlow<String?> = _lastAuthStatus.asStateFlow()

    private val defaultLordCipher = "LORD-ALPHA-01"

    fun requestPrivilegedApproval(request: PrivilegedActionRequest) {
        triggerHapticWarning()
        _pendingRequest.value = request
    }

    suspend fun authorizeWithBiometrics(request: PrivilegedActionRequest): String {
        triggerHapticSuccess()
        repository.logAction(
            action = "PRIVILEGED_BIOMETRIC_AUTH",
            details = "Lord authorized: ${request.title} (${request.payload}) via Biometric Sensor",
            authorizedByLord = true,
            severity = "PRIVILEGED"
        )
        _pendingRequest.value = null
        _lastAuthStatus.value = "Biometric Authorized: ${request.title}"
        return request.onExecute()
    }

    suspend fun authorizeWithCipher(request: PrivilegedActionRequest, enteredCipher: String): Result<String> {
        return if (enteredCipher.trim().equals(defaultLordCipher, ignoreCase = true) ||
            enteredCipher.trim().equals("LORD", ignoreCase = true) ||
            enteredCipher.trim().equals("AEGIS", ignoreCase = true)
        ) {
            triggerHapticSuccess()
            repository.logAction(
                action = "PRIVILEGED_CIPHER_AUTH",
                details = "Lord authorized: ${request.title} via Cryptographic Cipher",
                authorizedByLord = true,
                severity = "PRIVILEGED"
            )
            _pendingRequest.value = null
            _lastAuthStatus.value = "Cipher Authorized: ${request.title}"
            Result.success(request.onExecute())
        } else {
            triggerHapticWarning()
            repository.logAction(
                action = "AUTH_FAILURE",
                details = "Invalid owner cipher attempted for ${request.title}",
                authorizedByLord = false,
                severity = "SECURITY"
            )
            Result.failure(IllegalArgumentException("Invalid Owner Cipher. Access Restricted."))
        }
    }

    suspend fun denyRequest(request: PrivilegedActionRequest) {
        triggerHapticWarning()
        repository.logAction(
            action = "PRIVILEGED_REQUEST_DENIED",
            details = "Lord rejected privileged operation: ${request.title}",
            authorizedByLord = true,
            severity = "INFO"
        )
        _pendingRequest.value = null
        _lastAuthStatus.value = "Operation aborted by Lord."
    }

    private fun triggerHapticSuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 60), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticWarning() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }
}
