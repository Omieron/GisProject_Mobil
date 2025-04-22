package com.example.gismobil.utils

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build

object PerformanceUtils {

    private const val PREF_NAME = "app_performance_prefs"
    private const val KEY_PERFORMANCE_LEVEL = "performance_level"
    private const val KEY_DEVICE_CHECKED = "device_checked"

    // Performans seviyeleri
    const val PERFORMANCE_LOW = 0    // Düşük performans (eski sistem)
    const val PERFORMANCE_MEDIUM = 1 // Orta performans
    const val PERFORMANCE_HIGH = 2   // Yüksek performans/kalite (eski sistem high)

    // Performans durumunu kontrol et ve kaydet
    fun checkAndSavePerformanceMode(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Eğer daha önce cihaz kontrolü yapılmadıysa
        if (!sharedPrefs.getBoolean(KEY_DEVICE_CHECKED, false)) {
            val recommendedLevel = getRecommendedPerformanceLevel(context)
            sharedPrefs.edit().apply {
                putInt(KEY_PERFORMANCE_LEVEL, recommendedLevel)
                putBoolean(KEY_DEVICE_CHECKED, true)
                apply()
            }
        }
    }

    // Cihaz özelliklerine göre önerilen performans seviyesini belirle
    private fun getRecommendedPerformanceLevel(context: Context): Int {
        // RAM kontrolü
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem / (1024 * 1024) // MB cinsinden

        // Android sürümü kontrolü
        val sdkVersion = Build.VERSION.SDK_INT

        // CPU core sayısı kontrolü
        val cpuCores = Runtime.getRuntime().availableProcessors()

        // Çok düşük performans kriterleri
        val isVeryLowSpec = totalRam < 1536 || // 1.5GB'dan az RAM
                sdkVersion < Build.VERSION_CODES.N || // Android 7'den eski
                cpuCores <= 2 // 2 veya daha az çekirdek

        // Orta seviye performans kriterleri
        val isMediumSpec = totalRam < 3072 || // 3GB'dan az RAM
                sdkVersion < Build.VERSION_CODES.R || // Android 11'den eski
                cpuCores <= 4 // 4 veya daha az çekirdek

        return when {
            isVeryLowSpec -> PERFORMANCE_LOW
            isMediumSpec -> PERFORMANCE_MEDIUM
            else -> PERFORMANCE_HIGH
        }
    }

    // Performans seviyesini al
    fun getPerformanceLevel(context: Context): Int {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getInt(KEY_PERFORMANCE_LEVEL, PERFORMANCE_MEDIUM) // Varsayılan olarak orta seviye
    }

    // Performans seviyesini ayarla
    fun setPerformanceLevel(context: Context, level: Int) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putInt(KEY_PERFORMANCE_LEVEL, level)
            apply()
        }
    }

    // Eski fonksiyonlar - geriye dönük uyumluluk için
    fun isLowPerformanceMode(context: Context): Boolean {
        return getPerformanceLevel(context) == PERFORMANCE_LOW
    }
}