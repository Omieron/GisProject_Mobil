package com.example.gismobil.chat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gismobil.R
import java.util.Locale

/**
 * Ses tanıma işlemlerini yöneten yardımcı sınıf - Metin korumalı
 */
class SpeechRecognizerHelper(
    private val context: Context,
    private val micButton: ImageButton,
    private val editText: EditText
) {
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val RESTART_LISTENING_DELAY = 500L // 0.5 saniye
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var accumulatedText = "" // Biriktirilen metin
    private var isSessionActive = false // Aktif oturum takibi
    private val handler = Handler(Looper.getMainLooper())

    // Varsayılan dinleme için intent
    private val defaultRecognizerIntent by lazy {
        SpeechRecognizerConfig.createStandardIntent()
    }

    init {
        initializeSpeechRecognizer()
        setupMicButton()
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
        } else {
            micButton.visibility = android.view.View.GONE
            Toast.makeText(context, "Ses tanıma bu cihazda desteklenmiyor", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMicButton() {
        micButton.setOnClickListener {
            if (isListening || isSessionActive) {
                endSession()
            } else {
                // Yeni bir oturum başlat
                startNewSession()
            }
        }
    }

    /**
     * Tamamen yeni bir konuşma oturumu başlatır
     */
    private fun startNewSession() {
        // Önceki birikmiş metni temizle
        accumulatedText = ""
        isSessionActive = true
        checkPermissionAndStartListening()
    }

    /**
     * Oturumu sonlandırır ve dinlemeyi durdurur
     */
    private fun endSession() {
        isSessionActive = false
        stopListening()
        handler.removeCallbacksAndMessages(null)
    }

    private fun checkPermissionAndStartListening() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(permission),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            startListening()
        }
    }

    /**
     * Varsayılan ayarlarla dinlemeyi başlatır
     */
    fun startListening() {
        startListeningWithIntent(defaultRecognizerIntent)
    }

    /**
     * Özel intent ile dinlemeyi başlatır (uzun dinleme için)
     */
    fun startListeningWithIntent(intent: Intent) {
        speechRecognizer?.let {
            it.startListening(intent)
            isListening = true
            updateMicButtonState()

            // İlk dinlemeye başladığımızda kullanıcıya bir kez bildir
            if (accumulatedText.isEmpty()) {
                Toast.makeText(context, "Dinleniyor...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Dinlemeyi durdurur
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
        updateMicButtonState()
    }

    private fun updateMicButtonState() {
        micButton.setImageResource(
            if (isListening || isSessionActive) R.drawable.ic_mic_active else R.drawable.ic_mic
        )
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false

                // Oturum hala aktifse, duraksamadan sonra tekrar dinlemeye başla
                if (isSessionActive) {
                    restartListeningAfterDelay()
                } else {
                    updateMicButtonState()
                }
            }

            /**
             * Kısa bir gecikmeden sonra dinlemeyi tekrar başlatır
             */
            private fun restartListeningAfterDelay() {
                handler.postDelayed({
                    if (isSessionActive) {
                        startListening()
                    }
                }, RESTART_LISTENING_DELAY)
            }

            override fun onError(error: Int) {
                isListening = false

                // Oturum aktifse ve hata kabul edilebilirse, tekrar dinlemeye başla
                if (isSessionActive && (error == SpeechRecognizer.ERROR_NO_MATCH ||
                            error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
                    restartListeningAfterDelay()
                } else {
                    updateMicButtonState()

                    // Ciddi bir hata varsa oturumu sonlandır
                    if (error != SpeechRecognizer.ERROR_NO_MATCH &&
                        error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        isSessionActive = false

                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_NETWORK -> "Ağ hatası"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Yetersiz izin"
                            else -> "Ses tanıma hatası"
                        }

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]

                    // Biriktirilen metni güncelle
                    appendRecognizedText(recognizedText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]

                    // Duraksamalar sırasında göstermek için geçici metin oluştur
                    if (isSessionActive) {
                        val tempText = if (accumulatedText.isEmpty()) {
                            recognizedText
                        } else {
                            "$accumulatedText $recognizedText"
                        }

                        editText.setText(tempText)
                        editText.setSelection(tempText.length)
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    /**
     * Tanınan metni biriktirir ve EditText'e ekler
     */
    private fun appendRecognizedText(newText: String) {
        // Boş metin kontrolü
        if (newText.isBlank()) return

        // Yeni metin biriktir
        accumulatedText = if (accumulatedText.isEmpty()) {
            newText
        } else {
            "$accumulatedText $newText"
        }

        // EditText'i güncelle
        editText.setText(accumulatedText)
        editText.setSelection(accumulatedText.length)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening()
            } else {
                Toast.makeText(context, "Mikrofon izni olmadan ses tanıma çalışmaz", Toast.LENGTH_SHORT).show()
                isSessionActive = false
                updateMicButtonState()
            }
        }
    }

    fun cleanup() {
        isSessionActive = false
        handler.removeCallbacksAndMessages(null)
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}