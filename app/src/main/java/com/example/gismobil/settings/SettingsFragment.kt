package com.example.gismobil.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.gismobil.R
import com.example.gismobil.utils.PerformanceUtils

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ayarlar için UI bileşenlerini başlat
        val performanceSwitch: SwitchCompat = view.findViewById(R.id.switch_performance)

        // Mevcut performans durumunu kontrol et
        val isLowPerformance = context?.let { PerformanceUtils.isLowPerformanceMode(it) } ?: false
        performanceSwitch.isChecked = !isLowPerformance

        // Switch değiştiğinde ayarları güncelle
        performanceSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Performans modunu sakla
            context?.let { context ->
                val sharedPrefs = context.getSharedPreferences("app_performance_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().apply {
                    putBoolean("is_low_performance", !isChecked)
                    apply()
                }

                // Değişikliklerin etkili olması için yeniden başlatma öner
                Toast.makeText(context, "Ayarların etkili olması için uygulamayı yeniden başlatın", Toast.LENGTH_LONG).show()
            }
        }
    }
}