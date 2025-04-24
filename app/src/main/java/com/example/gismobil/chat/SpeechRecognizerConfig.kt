package com.example.gismobil.chat

import android.content.Intent
import android.speech.RecognizerIntent
import java.util.Locale

/**
 * Ses tanıma ayarlarını içeren yardımcı sınıf
 */
object SpeechRecognizerConfig {

    /**
     * Standart dinleme için intent oluşturur (yaklaşık 10-15 saniye)
     */
    fun createStandardIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("tr", "TR").toString())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Standart dinleme süresi ayarları
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
        }
    }

    /**
     * Uzun dinleme için intent oluşturur (20+ saniye)
     */
    fun createLongListeningIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("tr", "TR").toString())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Uzun dinleme süresi ayarları
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 15000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000)
        }
    }
}