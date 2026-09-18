package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

class AegisVoiceEngine(
    private val context: Context,
    private val onCommandReceived: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private val handler = Handler(Looper.getMainLooper())

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _isContinuousListening = MutableStateFlow(false)
    val isContinuousListening: StateFlow<Boolean> = _isContinuousListening.asStateFlow()

    private val _lastTranscript = MutableStateFlow("")
    val lastTranscript: StateFlow<String> = _lastTranscript.asStateFlow()

    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListeningInternal = false
    private var simulatedRmsRunnable: Runnable? = null

    // Wake words recognized
    private val wakeWords = listOf("lord's aegis", "lords aegis", "lord aegis", "aegis", "hey aegis")

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                tts.language = Locale.US
                // Configure deep, calm, authoritative male AI voice
                tts.setPitch(0.72f) // Deep baritone
                tts.setSpeechRate(0.95f) // Measured, authoritative pacing

                // Look for an English male voice if voice pack allows
                try {
                    val voices = tts.voices
                    val maleVoice = voices?.firstOrNull { voice ->
                        voice.locale.language == "en" &&
                                (voice.name.contains("male", ignoreCase = true) ||
                                        voice.name.contains("en-us-x-sfg", ignoreCase = true))
                    }
                    if (maleVoice != null) {
                        tts.voice = maleVoice
                    }
                } catch (_: Exception) {
                    // Fall back to default with pitch shift
                }

                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _voiceState.value = VoiceState.SPEAKING
                        startSimulatedSpeakingRms()
                    }

                    override fun onDone(utteranceId: String?) {
                        stopSimulatedSpeakingRms()
                        _audioRms.value = 0f
                        handler.post {
                            if (_isContinuousListening.value) {
                                startListening()
                            } else {
                                _voiceState.value = VoiceState.IDLE
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        stopSimulatedSpeakingRms()
                        _audioRms.value = 0f
                        _voiceState.value = VoiceState.IDLE
                    }
                })

                _isTtsReady.value = true
            }
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).apply {
                setRecognitionListener(AegisRecognitionListener())
            }
        }
    }

    fun toggleContinuousListening(enable: Boolean) {
        _isContinuousListening.value = enable
        if (enable) {
            startListening()
        } else {
            stopListening()
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun startListening() {
        if (isListeningInternal) return
        handler.post {
            try {
                if (speechRecognizer == null) {
                    initSpeechRecognizer()
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }

                speechRecognizer?.startListening(intent)
                isListeningInternal = true
                _voiceState.value = VoiceState.LISTENING
            } catch (e: Exception) {
                isListeningInternal = false
                _voiceState.value = VoiceState.IDLE
            }
        }
    }

    fun stopListening() {
        handler.post {
            try {
                isListeningInternal = false
                speechRecognizer?.stopListening()
            } catch (_: Exception) {}
        }
    }

    fun setThinkingState() {
        _voiceState.value = VoiceState.THINKING
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        handler.post {
            stopListening()
            textToSpeech?.let { tts ->
                val utteranceId = "AEGIS_${System.currentTimeMillis()}"
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            } ?: run {
                // If TTS not ready, still return to idle
                _voiceState.value = VoiceState.IDLE
                onComplete?.invoke()
            }
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        stopSimulatedSpeakingRms()
        _audioRms.value = 0f
        _voiceState.value = VoiceState.IDLE
    }

    private fun startSimulatedSpeakingRms() {
        stopSimulatedSpeakingRms()
        simulatedRmsRunnable = object : Runnable {
            override fun run() {
                if (_voiceState.value == VoiceState.SPEAKING) {
                    // Generate pulsating speech wave dynamics
                    val base = (Math.sin(System.currentTimeMillis() / 90.0) * 0.4 + 0.5).toFloat()
                    val jitter = (Math.random() * 0.3).toFloat()
                    _audioRms.value = (base + jitter).coerceIn(0.15f, 1.0f)
                    handler.postDelayed(this, 50)
                } else {
                    _audioRms.value = 0f
                }
            }
        }
        handler.post(simulatedRmsRunnable!!)
    }

    private fun stopSimulatedSpeakingRms() {
        simulatedRmsRunnable?.let { handler.removeCallbacks(it) }
        simulatedRmsRunnable = null
    }

    fun shutdown() {
        stopSimulatedSpeakingRms()
        stopListening()
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (_: Exception) {}
    }

    private inner class AegisRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            isListeningInternal = true
            _voiceState.value = VoiceState.LISTENING
        }

        override fun onBeginningOfSpeech() {
            _voiceState.value = VoiceState.LISTENING
        }

        override fun onRmsChanged(rmsdB: Float) {
            if (_voiceState.value == VoiceState.LISTENING) {
                // Map speech RMS dB (approx -2dB to 10dB) to 0f..1f
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1.0f)
                _audioRms.value = normalized
            }
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            isListeningInternal = false
            _audioRms.value = 0f
        }

        override fun onError(error: Int) {
            isListeningInternal = false
            _audioRms.value = 0f

            // Auto-recovery in continuous listening mode
            if (_isContinuousListening.value) {
                handler.postDelayed({
                    if (_isContinuousListening.value && _voiceState.value != VoiceState.SPEAKING) {
                        startListening()
                    }
                }, 800)
            } else {
                _voiceState.value = VoiceState.IDLE
            }
        }

        override fun onResults(results: Bundle?) {
            isListeningInternal = false
            _audioRms.value = 0f
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull()?.trim()

            if (!recognizedText.isNullOrBlank()) {
                _lastTranscript.value = recognizedText
                handleRecognizedUtterance(recognizedText)
            } else {
                if (_isContinuousListening.value) {
                    handler.postDelayed({ startListening() }, 500)
                } else {
                    _voiceState.value = VoiceState.IDLE
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val partial = matches?.firstOrNull()?.trim()
            if (!partial.isNullOrBlank()) {
                _lastTranscript.value = partial
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun handleRecognizedUtterance(text: String) {
        val lower = text.lowercase(Locale.ROOT)

        // In continuous listening mode, check if wake word is present or direct address
        var commandText = text
        var isWakeWordTriggered = false

        for (wake in wakeWords) {
            if (lower.contains(wake)) {
                isWakeWordTriggered = true
                val index = lower.indexOf(wake)
                val afterWake = text.substring(index + wake.length).trim(' ', ',', ':', ';', '.')
                commandText = if (afterWake.isNotBlank()) afterWake else "Hello Aegis"
                break
            }
        }

        // If continuous listening is on and no wake word found, ignore background chatter
        if (_isContinuousListening.value && !isWakeWordTriggered) {
            handler.postDelayed({
                if (_isContinuousListening.value && _voiceState.value != VoiceState.SPEAKING) {
                    startListening()
                }
            }, 600)
            return
        }

        // Dispatch command to Aegis Brain
        _voiceState.value = VoiceState.THINKING
        onCommandReceived(commandText)
    }
}
