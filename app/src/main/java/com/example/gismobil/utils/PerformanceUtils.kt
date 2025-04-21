package com.example.gismobil.utils

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build

object PerformanceUtils {

    private const val PREF_NAME = "app_performance_prefs"
    private const val KEY_IS_LOW_PERFORMANCE = "is_low_performance"
    private const val KEY_DEVICE_CHECKED = "device_checked"

    // Performans durumunu kontrol et ve kaydet
    fun checkAndSavePerformanceMode(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Eğer daha önce cihaz kontrolü yapılmadıysa
        if (!sharedPrefs.getBoolean(KEY_DEVICE_CHECKED, false)) {
            val isLowPerformance = isLowPerformanceDevice(context)
            sharedPrefs.edit().apply {
                putBoolean(KEY_IS_LOW_PERFORMANCE, isLowPerformance)
                putBoolean(KEY_DEVICE_CHECKED, true)
                apply()
            }
        }
    }

    // Cihazın düşük performanslı olup olmadığını kontrol et
    private fun isLowPerformanceDevice(context: Context): Boolean {
        // RAM kontrolü
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem / (1024 * 1024) // MB cinsinden

        // Android sürümü kontrolü
        val sdkVersion = Build.VERSION.SDK_INT

        // CPU core sayısı kontrolü
        val cpuCores = Runtime.getRuntime().availableProcessors()

        // Düşük performans kriterleri
        val isLowRam = totalRam < 2048 // 2GB'dan az RAM
        val isOldAndroid = sdkVersion < Build.VERSION_CODES.P // Android 9'dan eski
        val isLowCpuCores = cpuCores <= 4 // 4 veya daha az çekirdek

        // Eğer herhangi biri doğruysa düşük performanslı kabul et
        return isLowRam || isOldAndroid || isLowCpuCores
    }

    // Düşük performans modunda olup olmadığını kontrol et
    fun isLowPerformanceMode(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(KEY_IS_LOW_PERFORMANCE, false)
    }
}