package com.example.gismobil.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gismobil.R
import com.example.gismobil.utils.PerformanceUtils

class SettingsFragment : Fragment() {

    private lateinit var performanceSeekBar: SeekBar
    private lateinit var performanceLevelText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI bileşenlerini başlat
        performanceSeekBar = view.findViewById(R.id.seekbar_performance)
        performanceLevelText = view.findViewById(R.id.tv_performance_level)

        // Maksimum değeri 2 olarak ayarla (3 seviye: 0, 1, 2)
        performanceSeekBar.max = 2

        // Mevcut performans seviyesini kontrol et
        val currentLevel = context?.let { PerformanceUtils.getPerformanceLevel(it) } ?: PerformanceUtils.PERFORMANCE_MEDIUM

        // SeekBar'ı güncel değere ayarla
        performanceSeekBar.progress = currentLevel

        // Seviye metnini güncelle
        updatePerformanceLevelText(currentLevel)

        // SeekBar değiştiğinde ayarları güncelle
        performanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Anlık olarak metni güncelle
                updatePerformanceLevelText(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Boş bırakılabilir
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Kullanıcı kaydırmayı bıraktığında ayarı kaydet
                val newLevel = seekBar?.progress ?: PerformanceUtils.PERFORMANCE_MEDIUM
                context?.let { context ->
                    PerformanceUtils.setPerformanceLevel(context, newLevel)

                    // Değişikliklerin etkili olması için yeniden başlatma öner
                    Toast.makeText(
                        context,
                        "Ayarların etkili olması için uygulamayı yeniden başlatın",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    /**
     * Performans seviyesi metnini günceller
     */
    private fun updatePerformanceLevelText(level: Int) {
        val levelText = when (level) {
            PerformanceUtils.PERFORMANCE_LOW -> "Performans (Düşük)"
            PerformanceUtils.PERFORMANCE_MEDIUM -> "Denge (Orta)"
            PerformanceUtils.PERFORMANCE_HIGH -> "Kalite (Yüksek)"
            else -> "Denge (Orta)"
        }
        performanceLevelText.text = levelText
    }
}